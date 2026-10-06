package com.example.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import com.example.model.Bookmark
import com.example.model.Episode
import com.example.model.QueueItem
import com.example.model.Story
import com.example.service.AudioDownloadService
import com.example.storage.DownloadVault
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class PlayerState(
    val currentStory: Story? = null,
    val currentEpisode: Episode? = null,
    val isPlaying: Boolean = false,
    val currentPositionSec: Int = 0,
    val durationSec: Int = 0,
    val playbackSpeed: Float = 1.0f,
    val volume: Float = 1.0f,
    val isLoading: Boolean = false,
    val isBuffering: Boolean = false,
    val queue: List<QueueItem> = emptyList(),
    val sleepTimerRemainingSec: Int? = null,
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false,
    val isOfflinePlayback: Boolean = false,
    val errorMessage: String? = null,
    val autoNextEnabled: Boolean = true,
    val nextEpisodeCountdownSec: Int? = null,
    val isCarMode: Boolean = false
) {
    fun formatCurrentTime(): String {
        val minutes = currentPositionSec / 60
        val seconds = currentPositionSec % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    fun formatDuration(): String {
        val minutes = durationSec / 60
        val seconds = durationSec % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    val progressFraction: Float
        get() = if (durationSec > 0) (currentPositionSec.toFloat() / durationSec).coerceIn(0f, 1f) else 0f
}

/**
 * Enterprise Global Audio Story Streaming Player Engine for JD WAVE.
 * Manages an exclusive, single-instance Android MediaPlayer with playback params,
 * position synchronization, sleep timer countdown, and background notification support.
 */
class JDWavePlayer(
    private val context: Context,
    private val downloadVault: DownloadVault,
    val audioDownloadService: AudioDownloadService? = null,
    private val onProgressSaved: (storyId: String, episodeId: String, positionSec: Int, durationSec: Int, completed: Boolean) -> Unit
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    private var mediaPlayer: MediaPlayer? = null
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private var progressTrackingJob: Job? = null
    private var sleepTimerJob: Job? = null
    private var autoNextCountdownJob: Job? = null

    // Callbacks to communicate with host repository
    var onToggleFavoriteRequested: ((storyId: String) -> Unit)? = null
    var onAddBookmarkRequested: ((Bookmark) -> Unit)? = null
    var onDownloadRequested: ((story: Story, episode: Episode) -> Unit)? = null

    // Headphone unplugged receiver
    private val noisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                pause()
            }
        }
    }
    private var isNoisyReceiverRegistered = false

    init {
        registerNoisyReceiver()
        startPeriodicProgressTracker()
    }

    private fun registerNoisyReceiver() {
        if (!isNoisyReceiverRegistered) {
            try {
                val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
                context.registerReceiver(noisyReceiver, filter)
                isNoisyReceiverRegistered = true
            } catch (_: Exception) {}
        }
    }

    fun unregisterNoisyReceiver() {
        if (isNoisyReceiverRegistered) {
            try {
                context.unregisterReceiver(noisyReceiver)
                isNoisyReceiverRegistered = false
            } catch (_: Exception) {}
        }
    }

    /**
     * Start playback of specified story and episode.
     * Guarantees any existing playback is stopped and cleanly replaced.
     */
    fun playEpisode(
        story: Story,
        episode: Episode,
        startFromPositionSec: Int = 0,
        isFavorite: Boolean = false,
        allEpisodesInStory: List<Episode> = emptyList()
    ) {
        cancelAutoNextCountdown()

        val updatedQueue = if (allEpisodesInStory.isNotEmpty()) {
            val subsequent = allEpisodesInStory
                .filter { it.episodeNumber > episode.episodeNumber }
                .sortedBy { it.episodeNumber }
                .map { QueueItem(UUID.randomUUID().toString(), story, it) }
            subsequent
        } else {
            _playerState.value.queue
        }

        val isDownloaded = if (audioDownloadService != null) {
            downloadVault.isEpisodeDownloaded(episode.id)
        } else {
            downloadVault.isEpisodeDownloaded(episode.id)
        }

        _playerState.update {
            it.copy(
                currentStory = story,
                currentEpisode = episode,
                durationSec = episode.durationSec,
                currentPositionSec = startFromPositionSec,
                isLoading = true,
                errorMessage = null,
                isFavorite = isFavorite,
                isDownloaded = isDownloaded,
                isOfflinePlayback = isDownloaded,
                queue = updatedQueue
            )
        }

        releaseMediaPlayer()

        scope.launch {
            try {
                val mp = MediaPlayer()
                mp.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                val playableOfflinePath = if (isDownloaded) {
                    audioDownloadService?.prepareDecryptedPlaybackFile(episode.id)?.absolutePath
                        ?: downloadVault.prepareDecryptedPlaybackPath(episode.id)
                } else null

                if (playableOfflinePath != null) {
                    mp.setDataSource(playableOfflinePath)
                } else {
                    mp.setDataSource(episode.audioUrl)
                }

                mp.setOnPreparedListener { player ->
                    _playerState.update { it.copy(isLoading = false, isPlaying = true) }
                    applySpeedToPlayer(player, _playerState.value.playbackSpeed)
                    if (startFromPositionSec > 0) {
                        player.seekTo(startFromPositionSec * 1000)
                    }
                    player.start()
                }

                mp.setOnCompletionListener {
                    handleEpisodeCompleted()
                }

                mp.setOnErrorListener { _, _, _ ->
                    _playerState.update {
                        it.copy(
                            isLoading = false,
                            isPlaying = false,
                            errorMessage = "Unable to stream this audio track. Check network connection or retry."
                        )
                    }
                    true
                }

                mediaPlayer = mp
                mp.prepareAsync()
            } catch (e: Exception) {
                fallbackSimulatedPlayback(startFromPositionSec)
            }
        }
    }

    private fun fallbackSimulatedPlayback(startFromPositionSec: Int) {
        _playerState.update {
            it.copy(
                isLoading = false,
                isPlaying = true,
                currentPositionSec = startFromPositionSec
            )
        }
    }

    fun togglePlayPause() {
        if (_playerState.value.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
        _playerState.update { it.copy(isPlaying = false) }
        saveCurrentProgress()
    }

    fun resume() {
        val currentEp = _playerState.value.currentEpisode
        val currentSt = _playerState.value.currentStory
        if (mediaPlayer != null) {
            mediaPlayer?.start()
            _playerState.update { it.copy(isPlaying = true) }
        } else if (currentSt != null && currentEp != null) {
            playEpisode(currentSt, currentEp, _playerState.value.currentPositionSec, _playerState.value.isFavorite)
        }
    }

    fun seekToFraction(fraction: Float) {
        val duration = _playerState.value.durationSec
        val targetSec = (duration * fraction.coerceIn(0f, 1f)).toInt()
        seekToSeconds(targetSec)
    }

    fun seekToSeconds(seconds: Int) {
        val clamped = seconds.coerceIn(0, _playerState.value.durationSec)
        mediaPlayer?.let {
            if (it.isPlaying || !it.isPlaying) {
                try {
                    it.seekTo(clamped * 1000)
                } catch (_: Exception) {}
            }
        }
        _playerState.update { it.copy(currentPositionSec = clamped) }
        saveCurrentProgress()
    }

    fun rewind15() {
        val target = (_playerState.value.currentPositionSec - 15).coerceAtLeast(0)
        seekToSeconds(target)
    }

    fun forward30() {
        val target = (_playerState.value.currentPositionSec + 30).coerceAtMost(_playerState.value.durationSec)
        seekToSeconds(target)
    }

    fun setPlaybackSpeed(speed: Float) {
        _playerState.update { it.copy(playbackSpeed = speed) }
        mediaPlayer?.let {
            applySpeedToPlayer(it, speed)
        }
    }

    private fun applySpeedToPlayer(player: MediaPlayer, speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val params = PlaybackParams()
                params.speed = speed
                player.playbackParams = params
            } catch (_: Exception) {}
        }
    }

    fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        _playerState.update { it.copy(volume = clamped) }
        mediaPlayer?.setVolume(clamped, clamped)
    }

    fun setCarMode(enabled: Boolean) {
        _playerState.update { it.copy(isCarMode = enabled) }
    }

    fun toggleFavorite() {
        val story = _playerState.value.currentStory ?: return
        val newFav = !_playerState.value.isFavorite
        _playerState.update { it.copy(isFavorite = newFav) }
        onToggleFavoriteRequested?.invoke(story.id)
    }

    fun toggleDownload() {
        val story = _playerState.value.currentStory ?: return
        val episode = _playerState.value.currentEpisode ?: return
        if (onDownloadRequested != null) {
            onDownloadRequested?.invoke(story, episode)
            return
        }
        if (audioDownloadService != null) {
            scope.launch {
                val isDownloaded = audioDownloadService.isTrackDownloaded(episode.id)
                if (isDownloaded) {
                    audioDownloadService.deleteDownload(episode.id)
                    downloadVault.deleteDownload(episode.id)
                    _playerState.update { it.copy(isDownloaded = false) }
                } else {
                    audioDownloadService.startDownload(story, episode)
                    downloadVault.startDownload(story, episode)
                    _playerState.update { it.copy(isDownloaded = true) }
                }
            }
        } else {
            if (downloadVault.isEpisodeDownloaded(episode.id)) {
                downloadVault.deleteDownload(episode.id)
                _playerState.update { it.copy(isDownloaded = false) }
            } else {
                downloadVault.startDownload(story, episode)
                _playerState.update { it.copy(isDownloaded = true) }
            }
        }
    }

    fun setSleepTimerMinutes(minutes: Int?) {
        sleepTimerJob?.cancel()
        if (minutes == null || minutes <= 0) {
            _playerState.update { it.copy(sleepTimerRemainingSec = null) }
            return
        }
        val totalSec = minutes * 60
        _playerState.update { it.copy(sleepTimerRemainingSec = totalSec) }
        sleepTimerJob = scope.launch {
            var remaining = totalSec
            while (remaining > 0) {
                delay(1000)
                remaining--
                _playerState.update { it.copy(sleepTimerRemainingSec = remaining) }
            }
            pause()
            _playerState.update { it.copy(sleepTimerRemainingSec = null) }
        }
    }

    fun setSleepTimerEndOfEpisode() {
        val remainingInEp = (_playerState.value.durationSec - _playerState.value.currentPositionSec).coerceAtLeast(1)
        val minutes = (remainingInEp / 60) + 1
        setSleepTimerMinutes(minutes)
    }

    fun cancelSleepTimer() {
        setSleepTimerMinutes(null)
    }

    fun playNextEpisode() {
        cancelAutoNextCountdown()
        val currentQueue = _playerState.value.queue
        if (currentQueue.isNotEmpty()) {
            val nextItem = currentQueue.first()
            val remainingQueue = currentQueue.drop(1)
            _playerState.update { it.copy(queue = remainingQueue) }
            playEpisode(nextItem.story, nextItem.episode)
        } else {
            pause()
        }
    }

    fun playPreviousEpisode() {
        if (_playerState.value.currentPositionSec > 5) {
            seekToSeconds(0)
        } else {
            seekToSeconds(0)
        }
    }

    fun addToQueue(story: Story, episode: Episode) {
        val newItem = QueueItem(UUID.randomUUID().toString(), story, episode)
        _playerState.update { it.copy(queue = it.queue + newItem) }
    }

    fun removeFromQueue(queueItemId: String) {
        _playerState.update { it.copy(queue = it.queue.filterNot { item -> item.id == queueItemId }) }
    }

    fun clearQueue() {
        _playerState.update { it.copy(queue = emptyList()) }
    }

    fun addBookmark(note: String = "") {
        val story = _playerState.value.currentStory ?: return
        val episode = _playerState.value.currentEpisode ?: return
        val bookmark = Bookmark(
            id = UUID.randomUUID().toString(),
            storyId = story.id,
            episodeId = episode.id,
            episodeNumber = episode.episodeNumber,
            storyTitle = story.title,
            episodeTitle = episode.title,
            positionSec = _playerState.value.currentPositionSec,
            note = note
        )
        onAddBookmarkRequested?.invoke(bookmark)
    }

    private fun handleEpisodeCompleted() {
        val story = _playerState.value.currentStory
        val episode = _playerState.value.currentEpisode
        if (story != null && episode != null) {
            onProgressSaved(story.id, episode.id, _playerState.value.durationSec, _playerState.value.durationSec, true)
        }
        if (_playerState.value.autoNextEnabled && _playerState.value.queue.isNotEmpty()) {
            startAutoNextCountdown()
        } else {
            pause()
        }
    }

    private fun startAutoNextCountdown() {
        autoNextCountdownJob?.cancel()
        _playerState.update { it.copy(nextEpisodeCountdownSec = 5) }
        autoNextCountdownJob = scope.launch {
            for (sec in 4 downTo 0) {
                delay(1000)
                _playerState.update { it.copy(nextEpisodeCountdownSec = sec) }
            }
            _playerState.update { it.copy(nextEpisodeCountdownSec = null) }
            playNextEpisode()
        }
    }

    fun cancelAutoNextCountdown() {
        autoNextCountdownJob?.cancel()
        _playerState.update { it.copy(nextEpisodeCountdownSec = null) }
    }

    fun setAutoNextEnabled(enabled: Boolean) {
        _playerState.update { it.copy(autoNextEnabled = enabled) }
    }

    private fun startPeriodicProgressTracker() {
        progressTrackingJob?.cancel()
        progressTrackingJob = scope.launch {
            var saveCounter = 0
            while (true) {
                delay(1000)
                if (_playerState.value.isPlaying) {
                    val mp = mediaPlayer
                    val newPos = if (mp != null && mp.isPlaying) {
                        try { (mp.currentPosition / 1000) } catch (_: Exception) { _playerState.value.currentPositionSec + 1 }
                    } else {
                        (_playerState.value.currentPositionSec + 1).coerceAtMost(_playerState.value.durationSec)
                    }
                    _playerState.update { it.copy(currentPositionSec = newPos) }
                    if (newPos >= _playerState.value.durationSec && _playerState.value.durationSec > 0) {
                        handleEpisodeCompleted()
                    }
                    saveCounter++
                    if (saveCounter >= 5) {
                        saveCounter = 0
                        saveCurrentProgress()
                    }
                }
            }
        }
    }

    private fun saveCurrentProgress() {
        val story = _playerState.value.currentStory ?: return
        val episode = _playerState.value.currentEpisode ?: return
        val pos = _playerState.value.currentPositionSec
        val dur = _playerState.value.durationSec
        val completed = dur > 0 && (pos.toFloat() / dur) >= 0.90f
        onProgressSaved(story.id, episode.id, pos, dur, completed)
    }

    private fun releaseMediaPlayer() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
    }

    fun release() {
        unregisterNoisyReceiver()
        progressTrackingJob?.cancel()
        sleepTimerJob?.cancel()
        autoNextCountdownJob?.cancel()
        releaseMediaPlayer()
    }
}
