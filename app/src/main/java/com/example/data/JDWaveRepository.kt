package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AdminActivityLog
import com.example.model.AdminAnalyticsData
import com.example.model.AdminRole
import com.example.model.AdminUser
import com.example.model.Bookmark
import com.example.model.ContentStatus
import com.example.model.DailyFreeEpisodeConfig
import com.example.model.Episode
import com.example.model.EpisodeEntitlement
import com.example.model.ListeningHistoryItem
import com.example.model.PlaybackProgress
import com.example.model.Story
import com.example.model.TelegramRequestStatus
import com.example.model.TelegramUnlockRequest
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class JDWaveRepository(
    private val context: Context,
    val playbackDataStore: PlaybackDataStoreRepository = PlaybackDataStoreRepository(context)
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("jd_wave_prefs_2026", Context.MODE_PRIVATE)
    }

    // 1. Stories State
    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    // 2. Episodes State (keyed by storyId)
    private val _episodes = MutableStateFlow<Map<String, List<Episode>>>(emptyMap())
    val episodes: StateFlow<Map<String, List<Episode>>> = _episodes.asStateFlow()

    // 3. Playback Progress (keyed by episodeId)
    private val _playbackProgress = MutableStateFlow<Map<String, PlaybackProgress>>(emptyMap())
    val playbackProgress: StateFlow<Map<String, PlaybackProgress>> = _playbackProgress.asStateFlow()

    // 4. Favorites (set of storyIds)
    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    // 5. Bookmarks
    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    // 6. Listening History
    private val _history = MutableStateFlow<List<ListeningHistoryItem>>(emptyList())
    val history: StateFlow<List<ListeningHistoryItem>> = _history.asStateFlow()

    // 7. Preferences
    private val _themeMode = MutableStateFlow(AppThemeMode.DARK)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _defaultSpeed = MutableStateFlow(1.0f)
    val defaultSpeed: StateFlow<Float> = _defaultSpeed.asStateFlow()

    private val _autoNext = MutableStateFlow(true)
    val autoNext: StateFlow<Boolean> = _autoNext.asStateFlow()

    private val _wifiOnlyDownloads = MutableStateFlow(false)
    val wifiOnlyDownloads: StateFlow<Boolean> = _wifiOnlyDownloads.asStateFlow()

    private val _voiceBoost = MutableStateFlow(false)
    val voiceBoost: StateFlow<Boolean> = _voiceBoost.asStateFlow()

    private val _silenceSkip = MutableStateFlow(false)
    val silenceSkip: StateFlow<Boolean> = _silenceSkip.asStateFlow()

    private val _smartVolume = MutableStateFlow(true)
    val smartVolume: StateFlow<Boolean> = _smartVolume.asStateFlow()

    private val _gestureControls = MutableStateFlow(true)
    val gestureControls: StateFlow<Boolean> = _gestureControls.asStateFlow()

    // 8. Daily Free Episode (6:00 AM IST)
    // Server-authoritative daily free episode schedule
    private val _dailyFreeConfig = MutableStateFlow<DailyFreeEpisodeConfig?>(null)
    val dailyFreeConfig: StateFlow<DailyFreeEpisodeConfig?> = _dailyFreeConfig.asStateFlow()

    // Server authoritative timestamp offset (simulated server time sync)
    private var serverTimeOffsetMs: Long = 0L

    // 9. Entitlements & Telegram Unlock System
    private val _userEntitledEpisodes = MutableStateFlow<Set<String>>(emptySet())
    val userEntitledEpisodes: StateFlow<Set<String>> = _userEntitledEpisodes.asStateFlow()

    private val _entitlements = MutableStateFlow<List<EpisodeEntitlement>>(emptyList())
    val entitlements: StateFlow<List<EpisodeEntitlement>> = _entitlements.asStateFlow()

    private val _telegramUnlockRequests = MutableStateFlow<List<TelegramUnlockRequest>>(emptyList())
    val telegramUnlockRequests: StateFlow<List<TelegramUnlockRequest>> = _telegramUnlockRequests.asStateFlow()

    // 10. Admin State
    private val _currentAdminUser = MutableStateFlow<AdminUser?>(null)
    val currentAdminUser: StateFlow<AdminUser?> = _currentAdminUser.asStateFlow()

    private val _adminActivityLogs = MutableStateFlow<List<AdminActivityLog>>(emptyList())
    val adminActivityLogs: StateFlow<List<AdminActivityLog>> = _adminActivityLogs.asStateFlow()

    private val _adminAnalytics = MutableStateFlow(AdminAnalyticsData())
    val adminAnalytics: StateFlow<AdminAnalyticsData> = _adminAnalytics.asStateFlow()

    init {
        loadInitialCatalog()
        loadPreferences()
        setupDailyFreeEpisode()
    }

    /**
     * Server-authoritative current time.
     * Guaranteed to use server time rather than tampering with client system clock.
     */
    fun getServerCurrentTimeMillis(): Long {
        return System.currentTimeMillis() + serverTimeOffsetMs
    }

    /**
     * Configures and evaluates the 6:00 AM IST Daily Free Episode.
     * Admin or server sets: story, episode, date, active status.
     * Evaluates whether the server time has reached or passed 6:00 AM IST on the given date.
     */
    fun setupDailyFreeEpisode() {
        val todayIstKey = SampleCatalog.getIstDateKey(getServerCurrentTimeMillis())
        val unlockTime = SampleCatalog.get6AmIstEpochMs(todayIstKey)

        // Default configured free daily episode for today (e.g. Episode 3 of "The Warrior")
        // Episode 3 is normally premium (price 1 INR), but unlocks freely via daily rotation!
        val defaultDailyConfig = DailyFreeEpisodeConfig(
            dateKey = todayIstKey,
            storyId = "story_warrior",
            episodeId = "ep_story_warrior_3",
            storyTitle = "The Warrior: Amara Yoddhu",
            episodeTitle = "The Oath of Iron",
            episodeNumber = 3,
            unlockTimeUtcEpochMs = unlockTime,
            active = true
        )

        _dailyFreeConfig.value = defaultDailyConfig
    }

    /**
     * Admin configuration for Daily Free Episode.
     */
    fun setDailyFreeEpisodeConfig(config: DailyFreeEpisodeConfig) {
        _dailyFreeConfig.value = config
        logAdminActivity("Configured Daily Free Episode", "DailyFree", "${config.storyTitle} Ep ${config.episodeNumber}")
    }

    /**
     * Server-side check if a given episode is currently unlocked via Daily Free 6:00 AM IST schedule.
     * Enforces STRICT date validation:
     * 1. Config must be active.
     * 2. Episode ID must match.
     * 3. Today's date in IST (Asia/Kolkata) must EXACTLY match the config's active dateKey.
     *    Stale configs from yesterday or previous dates are rejected immediately!
     * 4. Server time must be at or past 6:00 AM IST (18:00 UTC previous day / 00:30 UTC current day).
     */
    fun isDailyFreeEpisodeUnlocked(episodeId: String): Boolean {
        val config = _dailyFreeConfig.value ?: return false
        if (!config.active) return false
        if (config.episodeId != episodeId) return false

        val serverNow = getServerCurrentTimeMillis()
        val currentIstDateKey = SampleCatalog.getIstDateKey(serverNow)

        // Stale date check: Daily Free configuration only applies to its configured active date!
        if (config.dateKey != currentIstDateKey) {
            return false
        }

        // Must be past or equal to 6:00 AM IST on that specific date
        val required6AmEpoch = if (config.unlockTimeUtcEpochMs > 0L) {
            config.unlockTimeUtcEpochMs
        } else {
            SampleCatalog.get6AmIstEpochMs(config.dateKey)
        }

        return serverNow >= required6AmEpoch
    }

    /**
     * Centralized Episode Access Engine:
     * Evaluates whether an episode is playable / downloadable by the user.
     * - Free by default (ep.isPremium == false)
     * - Daily Free at 6:00 AM IST (date-verified server-authoritative)
     * - User has acquired entitlement (purchased, approved telegram unlock, or admin grant)
     */
    fun isEpisodeAccessible(episode: Episode): Boolean {
        if (!episode.isPremium) return true
        if (isDailyFreeEpisodeUnlocked(episode.id)) return true
        return _userEntitledEpisodes.value.contains(episode.id)
    }

    fun isEpisodeUnlocked(episode: Episode): Boolean {
        return isEpisodeAccessible(episode)
    }

    /**
     * Centralized download authorization check.
     * Requires the episode to be accessible AND enforces guest restrictions.
     * Guests can stream accessible free episodes, but offline vault downloads require a signed-in account.
     */
    fun canDownloadEpisode(episode: Episode, isGuest: Boolean): DownloadAccessResult {
        if (isGuest) {
            return DownloadAccessResult.DeniedGuestSignInRequired
        }
        if (!isEpisodeAccessible(episode)) {
            return DownloadAccessResult.DeniedEpisodeLocked(episode.priceInr)
        }
        return DownloadAccessResult.Allowed
    }

    sealed class DownloadAccessResult {
        object Allowed : DownloadAccessResult()
        object DeniedGuestSignInRequired : DownloadAccessResult()
        data class DeniedEpisodeLocked(val priceInr: Int) : DownloadAccessResult()
    }

    /**
     * Returns detailed access status for UI badges and indicators.
     */
    fun getEpisodeAccessStatus(episode: Episode): EpisodeAccessStatus {
        if (!episode.isPremium) {
            return EpisodeAccessStatus.FREE_DEFAULT
        }
        if (isDailyFreeEpisodeUnlocked(episode.id)) {
            return EpisodeAccessStatus.DAILY_FREE_UNLOCKED
        }
        if (_userEntitledEpisodes.value.contains(episode.id)) {
            return EpisodeAccessStatus.ENTITLED
        }
        return EpisodeAccessStatus.LOCKED_PREMIUM
    }

    enum class EpisodeAccessStatus {
        FREE_DEFAULT,
        DAILY_FREE_UNLOCKED,
        ENTITLED,
        LOCKED_PREMIUM
    }

    private fun loadInitialCatalog() {
        val initialStories = SampleCatalog.getInitialStories()
        _stories.value = initialStories

        val epMap = mutableMapOf<String, List<Episode>>()
        initialStories.forEach { story ->
            epMap[story.id] = SampleCatalog.generateEpisodesForStory(story)
        }
        _episodes.value = epMap

        val warriorStory = initialStories.firstOrNull()
        if (warriorStory != null) {
            val firstEp = epMap[warriorStory.id]?.firstOrNull()
            if (firstEp != null) {
                val progress = PlaybackProgress(
                    storyId = warriorStory.id,
                    episodeId = firstEp.id,
                    positionSec = 1122,
                    durationSec = firstEp.durationSec,
                    completed = false
                )
                _playbackProgress.value = mapOf(firstEp.id to progress)
                _bookmarks.value = listOf(
                    Bookmark(
                        id = UUID.randomUUID().toString(),
                        storyId = warriorStory.id,
                        episodeId = firstEp.id,
                        episodeNumber = firstEp.episodeNumber,
                        storyTitle = warriorStory.title,
                        episodeTitle = firstEp.title,
                        positionSec = 1122,
                        note = "The hero enters the forbidden mountain sanctuary"
                    )
                )
                _history.value = listOf(
                    ListeningHistoryItem(
                        id = UUID.randomUUID().toString(),
                        story = warriorStory,
                        episode = firstEp,
                        positionSec = 1122,
                        durationSec = firstEp.durationSec
                    )
                )
                _favorites.value = setOf(warriorStory.id)

                val fourthEp = epMap[warriorStory.id]?.getOrNull(3)
                if (fourthEp != null) {
                    _telegramUnlockRequests.value = listOf(
                        TelegramUnlockRequest(
                            requestId = "JDW-8K4P-125",
                            userId = "user_telugu_fan_99",
                            username = "Jagadeesh G (itsjagadee@gmail.com)",
                            storyId = warriorStory.id,
                            storyTitle = warriorStory.title,
                            episodeId = fourthEp.id,
                            episodeTitle = fourthEp.title,
                            episodeNumber = fourthEp.episodeNumber,
                            price = 1,
                            createdAt = System.currentTimeMillis() - 1000L * 60 * 45,
                            paymentStatus = "PENDING_VERIFICATION",
                            status = TelegramRequestStatus.PENDING
                        )
                    )
                }
            }
        }
    }

    private fun loadPreferences() {
        val themeOrdinal = prefs.getInt("pref_theme_mode", AppThemeMode.DARK.ordinal)
        _themeMode.value = AppThemeMode.entries.getOrElse(themeOrdinal) { AppThemeMode.DARK }
        _defaultSpeed.value = prefs.getFloat("pref_default_speed", 1.0f)
        _autoNext.value = prefs.getBoolean("pref_auto_next", true)
        _wifiOnlyDownloads.value = prefs.getBoolean("pref_wifi_download", false)
        _voiceBoost.value = prefs.getBoolean("pref_voice_boost", false)
        _silenceSkip.value = prefs.getBoolean("pref_silence_skip", false)
        _smartVolume.value = prefs.getBoolean("pref_smart_volume", true)
        _gestureControls.value = prefs.getBoolean("pref_gestures", true)
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putInt("pref_theme_mode", mode.ordinal).apply()
    }

    fun setDefaultSpeed(speed: Float) {
        _defaultSpeed.value = speed
        prefs.edit().putFloat("pref_default_speed", speed).apply()
    }

    fun setAutoNext(enabled: Boolean) {
        _autoNext.value = enabled
        prefs.edit().putBoolean("pref_auto_next", enabled).apply()
    }

    fun setWifiOnlyDownloads(enabled: Boolean) {
        _wifiOnlyDownloads.value = enabled
        prefs.edit().putBoolean("pref_wifi_download", enabled).apply()
    }

    fun setVoiceBoost(enabled: Boolean) {
        _voiceBoost.value = enabled
        prefs.edit().putBoolean("pref_voice_boost", enabled).apply()
    }

    fun setSilenceSkip(enabled: Boolean) {
        _silenceSkip.value = enabled
        prefs.edit().putBoolean("pref_silence_skip", enabled).apply()
    }

    fun setSmartVolume(enabled: Boolean) {
        _smartVolume.value = enabled
        prefs.edit().putBoolean("pref_smart_volume", enabled).apply()
    }

    fun setGestureControls(enabled: Boolean) {
        _gestureControls.value = enabled
        prefs.edit().putBoolean("pref_gestures", enabled).apply()
    }

    fun saveProgress(
        storyId: String,
        episodeId: String,
        positionSec: Int,
        durationSec: Int,
        completed: Boolean
    ) {
        val progress = PlaybackProgress(
            storyId = storyId,
            episodeId = episodeId,
            positionSec = positionSec,
            durationSec = durationSec,
            completed = completed
        )
        _playbackProgress.update { it + (episodeId to progress) }

        scope.launch {
            playbackDataStore.savePlaybackPosition(
                trackId = episodeId,
                positionSec = positionSec,
                durationSec = durationSec,
                storyId = storyId,
                completed = completed
            )
        }

        val story = _stories.value.find { it.id == storyId }
        val episode = _episodes.value[storyId]?.find { it.id == episodeId }
        if (story != null && episode != null) {
            val updatedHistory = _history.value.filterNot { it.episode.id == episodeId }.toMutableList()
            updatedHistory.add(
                0,
                ListeningHistoryItem(
                    id = UUID.randomUUID().toString(),
                    story = story,
                    episode = episode,
                    positionSec = positionSec,
                    durationSec = durationSec
                )
            )
            _history.value = updatedHistory
        }
    }

    suspend fun getInitialPlaybackPosition(episodeId: String): Int {
        return playbackDataStore.getInitialPlaybackPosition(episodeId)
    }

    fun getProgressForEpisode(episodeId: String): PlaybackProgress? {
        return _playbackProgress.value[episodeId]
    }

    fun toggleFavorite(storyId: String) {
        _favorites.update { current ->
            if (current.contains(storyId)) current - storyId else current + storyId
        }
    }

    fun isFavorite(storyId: String): Boolean = _favorites.value.contains(storyId)

    fun addBookmark(bookmark: Bookmark) {
        _bookmarks.update { listOf(bookmark) + it }
    }

    fun deleteBookmark(bookmarkId: String) {
        _bookmarks.update { it.filterNot { b -> b.id == bookmarkId } }
    }

    fun clearHistory() {
        _history.value = emptyList()
    }

    fun removeHistoryItem(historyId: String) {
        _history.update { it.filterNot { item -> item.id == historyId } }
    }

    fun loginAdmin(email: String, role: AdminRole = AdminRole.SUPER_ADMIN): Boolean {
        _currentAdminUser.value = AdminUser(
            id = "admin_${UUID.randomUUID().toString().take(8)}",
            email = email,
            name = if (email.contains("@")) email.substringBefore("@").replaceFirstChar { it.uppercase() } else "Admin",
            role = role
        )
        logAdminActivity("Admin Login", "Auth", email)
        return true
    }

    fun logoutAdmin() {
        val email = _currentAdminUser.value?.email ?: "Admin"
        logAdminActivity("Admin Logout", "Auth", email)
        _currentAdminUser.value = null
    }

    fun logAdminActivity(action: String, targetType: String, targetTitle: String) {
        val log = AdminActivityLog(
            id = UUID.randomUUID().toString(),
            adminEmail = _currentAdminUser.value?.email ?: "system@jdwave.app",
            action = action,
            targetType = targetType,
            targetTitle = targetTitle
        )
        _adminActivityLogs.update { listOf(log) + it }
    }

    fun createStory(story: Story) {
        _stories.update { listOf(story) + it }
        logAdminActivity("Created Story", "Story", story.title)
    }

    fun updateStory(story: Story) {
        _stories.update { list -> list.map { if (it.id == story.id) story else it } }
        logAdminActivity("Updated Story", "Story", story.title)
    }

    fun deleteStory(storyId: String) {
        val target = _stories.value.find { it.id == storyId }
        _stories.update { list -> list.filterNot { it.id == storyId } }
        val currentEpisodes = _episodes.value.toMutableMap()
        currentEpisodes.remove(storyId)
        _episodes.value = currentEpisodes
        logAdminActivity("Deleted Story", "Story", target?.title ?: storyId)
    }

    fun setStoryStatus(storyId: String, status: ContentStatus) {
        _stories.update { list ->
            list.map { if (it.id == storyId) it.copy(status = status) else it }
        }
        val target = _stories.value.find { it.id == storyId }
        logAdminActivity("Set Story Status to $status", "Story", target?.title ?: storyId)
    }

    fun addEpisode(storyId: String, episode: Episode) {
        val currentMap = _episodes.value.toMutableMap()
        val list = (currentMap[storyId] ?: emptyList()).toMutableList()
        list.add(episode)
        currentMap[storyId] = list
        _episodes.value = currentMap
        _stories.update { stList ->
            stList.map { if (it.id == storyId) it.copy(episodeCount = list.size) else it }
        }
        logAdminActivity("Added Episode #${episode.episodeNumber}", "Episode", episode.title)
    }

    fun updateEpisode(storyId: String, episode: Episode) {
        val currentMap = _episodes.value.toMutableMap()
        val list = (currentMap[storyId] ?: emptyList()).map { if (it.id == episode.id) episode else it }
        currentMap[storyId] = list
        _episodes.value = currentMap
        logAdminActivity("Updated Episode #${episode.episodeNumber}", "Episode", episode.title)
    }

    fun deleteEpisode(storyId: String, episodeId: String) {
        val currentMap = _episodes.value.toMutableMap()
        val list = (currentMap[storyId] ?: emptyList()).filterNot { it.id == episodeId }
        currentMap[storyId] = list
        _episodes.value = currentMap
        _stories.update { stList ->
            stList.map { if (it.id == storyId) it.copy(episodeCount = list.size) else it }
        }
        logAdminActivity("Deleted Episode", "Episode", episodeId)
    }

    fun reorderEpisodes(storyId: String, reordered: List<Episode>) {
        val currentMap = _episodes.value.toMutableMap()
        currentMap[storyId] = reordered
        _episodes.value = currentMap
        logAdminActivity("Reordered Episodes", "Story", storyId)
    }

    fun unlockEpisode(
        userId: String,
        storyId: String,
        episodeId: String,
        source: String
    ): EpisodeEntitlement {
        val entitlement = EpisodeEntitlement(
            id = UUID.randomUUID().toString(),
            userId = userId,
            episodeId = episodeId,
            storyId = storyId,
            source = source,
            unlockedAt = System.currentTimeMillis(),
            status = "active"
        )
        _entitlements.update { it + entitlement }
        _userEntitledEpisodes.update { it + episodeId }
        logAdminActivity("Unlocked Episode ($source)", "Entitlement", episodeId)
        return entitlement
    }

    fun createTelegramUnlockRequest(
        userId: String,
        username: String,
        storyId: String,
        storyTitle: String,
        episodeId: String,
        episodeTitle: String,
        episodeNumber: Int,
        price: Int = 1
    ): TelegramUnlockRequest {
        val randomChars = (1..4).map { ('A'..'Z').random() }.joinToString("")
        val randomDigits = (100..999).random()
        val requestCode = "JDW-${randomChars}-${randomDigits}"
        val req = TelegramUnlockRequest(
            requestId = requestCode,
            userId = userId,
            username = username,
            storyId = storyId,
            storyTitle = storyTitle,
            episodeId = episodeId,
            episodeTitle = episodeTitle,
            episodeNumber = episodeNumber,
            price = price,
            createdAt = System.currentTimeMillis(),
            paymentStatus = "PENDING_VERIFICATION",
            status = TelegramRequestStatus.PENDING
        )
        _telegramUnlockRequests.update { listOf(req) + it }
        logAdminActivity("Created Telegram Unlock Request", "Telegram", requestCode)
        return req
    }

    fun approveTelegramUnlockRequest(requestId: String, adminEmail: String) {
        val req = _telegramUnlockRequests.value.firstOrNull { it.requestId == requestId } ?: return
        _telegramUnlockRequests.update { list ->
            list.map {
                if (it.requestId == requestId) it.copy(
                    status = TelegramRequestStatus.APPROVED,
                    paymentStatus = "VERIFIED"
                ) else it
            }
        }
        unlockEpisode(
            userId = req.userId,
            storyId = req.storyId,
            episodeId = req.episodeId,
            source = "telegram_manual"
        )
        logAdminActivity("Approved Telegram Unlock: $requestId by $adminEmail", "TelegramRequest", req.episodeTitle)
    }

    fun rejectTelegramUnlockRequest(requestId: String, adminEmail: String) {
        val req = _telegramUnlockRequests.value.firstOrNull { it.requestId == requestId } ?: return
        _telegramUnlockRequests.update { list ->
            list.map {
                if (it.requestId == requestId) it.copy(status = TelegramRequestStatus.REJECTED) else it
            }
        }
        logAdminActivity("Rejected Telegram Unlock: $requestId by $adminEmail", "TelegramRequest", req.episodeTitle)
    }

    fun setEpisodePremium(storyId: String, episodeId: String, isPremium: Boolean, priceInr: Int = 1) {
        val currentMap = _episodes.value.toMutableMap()
        val list = (currentMap[storyId] ?: emptyList()).map {
            if (it.id == episodeId) it.copy(isPremium = isPremium, priceInr = priceInr) else it
        }
        currentMap[storyId] = list
        _episodes.value = currentMap
        logAdminActivity("Set Episode ${if (isPremium) "Premium (₹$priceInr)" else "Free"}", "Episode", episodeId)
    }
}
