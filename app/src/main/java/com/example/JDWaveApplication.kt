package com.example

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.example.data.JDWaveRepository
import com.example.data.PlaybackDataStoreRepository
import com.example.player.JDWavePlayer
import com.example.service.AudioDownloadService
import com.example.storage.DownloadVault

class JDWaveApplication : Application() {
    lateinit var downloadVault: DownloadVault
        private set
    lateinit var audioDownloadService: AudioDownloadService
        private set
    lateinit var playbackDataStore: PlaybackDataStoreRepository
        private set
    val firestoreRepository: com.example.data.JDWaveFirestoreRepository by lazy {
        com.example.data.JDWaveFirestoreRepository(this)
    }
    lateinit var repository: JDWaveRepository
        private set
    lateinit var player: JDWavePlayer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        if (com.google.firebase.FirebaseApp.getApps(this).isEmpty()) {
            try {
                com.google.firebase.FirebaseApp.initializeApp(this)
            } catch (_: Exception) {
                try {
                    com.google.firebase.FirebaseApp.initializeApp(
                        this,
                        com.google.firebase.FirebaseOptions.Builder()
                            .setApplicationId("com.aistudio.jdwave.qrvptk")
                            .setProjectId("gen-lang-client-0890002483")
                            .setApiKey("fake-key-for-init")
                            .build()
                    )
                } catch (_: Exception) {}
            }
        }

        downloadVault = DownloadVault(this)
        audioDownloadService = AudioDownloadService(this)
        playbackDataStore = PlaybackDataStoreRepository(this)
        repository = JDWaveRepository(this, playbackDataStore)
        player = JDWavePlayer(
            context = this,
            downloadVault = downloadVault,
            audioDownloadService = audioDownloadService,
            onProgressSaved = { storyId, episodeId, positionSec, durationSec, completed ->
                repository.saveProgress(storyId, episodeId, positionSec, durationSec, completed)
                try {
                    if (com.google.firebase.FirebaseApp.getApps(this).isNotEmpty() &&
                        com.google.firebase.Firebase.auth.currentUser != null
                    ) {
                        firestoreRepository.savePlaybackProgress(
                            storyId = storyId,
                            episodeId = episodeId,
                            positionMs = positionSec * 1000L,
                            durationMs = durationSec * 1000L,
                            completed = completed
                        )
                    }
                } catch (_: Exception) {}
            }
        )

        player.onToggleFavoriteRequested = { storyId ->
            repository.toggleFavorite(storyId)
        }
        player.onAddBookmarkRequested = { bookmark ->
            repository.addBookmark(bookmark)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        player.release()
    }

    companion object {
        lateinit var instance: JDWaveApplication
            private set
    }
}
