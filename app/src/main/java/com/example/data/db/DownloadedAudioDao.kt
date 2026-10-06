package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedAudioDao {
    @Query("SELECT * FROM downloaded_audio_tracks ORDER BY downloadedAt DESC")
    fun getAllDownloadedTracks(): Flow<List<DownloadedAudioEntity>>

    @Query("SELECT * FROM downloaded_audio_tracks WHERE downloadStatus = 'DOWNLOADED' ORDER BY downloadedAt DESC")
    fun getCompletedDownloads(): Flow<List<DownloadedAudioEntity>>

    @Query("SELECT * FROM downloaded_audio_tracks WHERE episodeId = :episodeId LIMIT 1")
    fun getTrackByEpisodeId(episodeId: String): Flow<DownloadedAudioEntity?>

    @Query("SELECT * FROM downloaded_audio_tracks WHERE episodeId = :episodeId LIMIT 1")
    suspend fun getTrackByEpisodeIdOnce(episodeId: String): DownloadedAudioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(track: DownloadedAudioEntity)

    @Query("UPDATE downloaded_audio_tracks SET downloadStatus = :status, progressPercent = :progress WHERE episodeId = :episodeId")
    suspend fun updateDownloadProgress(episodeId: String, status: String, progress: Float)

    @Query("UPDATE downloaded_audio_tracks SET downloadStatus = 'DOWNLOADED', progressPercent = 1.0, fileSizeMb = :fileSizeMb, downloadedAt = :downloadedAt WHERE episodeId = :episodeId")
    suspend fun markDownloadCompleted(episodeId: String, fileSizeMb: Float, downloadedAt: Long)

    @Query("DELETE FROM downloaded_audio_tracks WHERE episodeId = :episodeId")
    suspend fun deleteTrack(episodeId: String)

    @Query("SELECT COUNT(*) FROM downloaded_audio_tracks WHERE episodeId = :episodeId AND downloadStatus = 'DOWNLOADED'")
    suspend fun isTrackDownloaded(episodeId: String): Int

    @Query("DELETE FROM downloaded_audio_tracks")
    suspend fun clearAllDownloads()
}
