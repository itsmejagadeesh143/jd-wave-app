package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_audio_tracks")
data class DownloadedAudioEntity(
    @PrimaryKey
    val episodeId: String,
    val storyId: String,
    val storyTitle: String,
    val episodeTitle: String,
    val episodeNumber: Int,
    val coverUrl: String,
    val durationSec: Int,
    val encryptedFilePath: String,
    val downloadStatus: String, // "DOWNLOADING", "DOWNLOADED", "PAUSED", "FAILED"
    val progressPercent: Float = 0f,
    val fileSizeMb: Float = 0f,
    val downloadedAt: Long = System.currentTimeMillis()
)
