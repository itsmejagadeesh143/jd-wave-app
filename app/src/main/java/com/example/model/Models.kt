package com.example.model

enum class ContentStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED
}

data class Story(
    val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val bannerUrl: String = "",
    val genre: String,
    val author: String,
    val rating: Float = 4.8f,
    val episodeCount: Int = 10,
    val totalListens: Long = 120_000L,
    val tags: List<String> = emptyList(),
    val language: String = "Telugu",
    val status: ContentStatus = ContentStatus.PUBLISHED,
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isTopRated: Boolean = false,
    val isNewRelease: Boolean = false,
    val isPopular: Boolean = false,
    val isEditorsPick: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class Episode(
    val id: String,
    val storyId: String,
    val episodeNumber: Int,
    val title: String,
    val description: String,
    val audioUrl: String,
    val durationSec: Int,
    val coverUrl: String = "",
    val releaseDate: String = "2026-10-01",
    val status: ContentStatus = ContentStatus.PUBLISHED,
    val isPremium: Boolean = false,
    val priceInr: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun formattedDuration(): String {
        val minutes = durationSec / 60
        val seconds = durationSec % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}

/**
 * Daily Free Episode Configuration (determined by server-side time/data at 6:00 AM IST)
 * dateKey format: "YYYY-MM-DD" in Indian Standard Time (UTC+5:30)
 */
data class DailyFreeEpisodeConfig(
    val dateKey: String, // e.g. "2026-10-06"
    val storyId: String,
    val episodeId: String,
    val storyTitle: String = "",
    val episodeTitle: String = "",
    val episodeNumber: Int = 1,
    val unlockTimeUtcEpochMs: Long = 0L,
    val active: Boolean = true
)

data class PlaybackProgress(
    val storyId: String,
    val episodeId: String,
    val positionSec: Int,
    val durationSec: Int,
    val completed: Boolean = false,
    val lastPlayedAt: Long = System.currentTimeMillis()
) {
    val progressPercent: Float
        get() = if (durationSec > 0) (positionSec.toFloat() / durationSec).coerceIn(0f, 1f) else 0f
}

data class Bookmark(
    val id: String,
    val userId: String = "guest_user",
    val storyId: String,
    val episodeId: String,
    val episodeNumber: Int = 1,
    val storyTitle: String = "",
    val episodeTitle: String = "",
    val positionSec: Int,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun formattedTimestamp(): String {
        val minutes = positionSec / 60
        val seconds = positionSec % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}

enum class DownloadStatus {
    DOWNLOADING,
    DOWNLOADED,
    PAUSED,
    FAILED
}

data class DownloadRecord(
    val id: String,
    val userId: String = "guest_user",
    val storyId: String,
    val episodeId: String,
    val storyTitle: String,
    val episodeTitle: String,
    val episodeNumber: Int,
    val coverUrl: String,
    val durationSec: Int,
    val encryptedFileReference: String,
    val status: DownloadStatus = DownloadStatus.DOWNLOADING,
    val progress: Float = 0f,
    val fileSizeMb: Float = 14.5f,
    val downloadedAt: Long = System.currentTimeMillis()
)

data class QueueItem(
    val id: String,
    val story: Story,
    val episode: Episode,
    val addedAt: Long = System.currentTimeMillis()
)

data class ListeningHistoryItem(
    val id: String,
    val story: Story,
    val episode: Episode,
    val positionSec: Int,
    val durationSec: Int,
    val lastPlayedAt: Long = System.currentTimeMillis()
) {
    val progressPercent: Float
        get() = if (durationSec > 0) (positionSec.toFloat() / durationSec).coerceIn(0f, 1f) else 0f
}

enum class AdminRole {
    SUPER_ADMIN,
    CONTENT_ADMIN,
    EDITOR
}

data class AdminUser(
    val id: String,
    val email: String,
    val name: String,
    val role: AdminRole,
    val lastLoginAt: Long = System.currentTimeMillis()
)

data class AdminActivityLog(
    val id: String,
    val adminEmail: String,
    val action: String,
    val targetType: String,
    val targetTitle: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AdminAnalyticsData(
    val totalPlays: Long = 1_482_300L,
    val uniqueListeners: Long = 349_210L,
    val completedEpisodes: Long = 982_140L,
    val avgListeningTimeMin: Int = 38,
    val totalDownloads: Long = 184_520L,
    val totalFavorites: Long = 215_900L,
    val audioStorageMb: Float = 8420.5f,
    val imageStorageMb: Float = 620.2f,
    val totalAudioFiles: Int = 184,
    val totalImages: Int = 76
)

enum class TelegramRequestStatus {
    PENDING,
    APPROVED,
    REJECTED,
    EXPIRED
}

data class TelegramUnlockRequest(
    val requestId: String,
    val userId: String,
    val username: String,
    val storyId: String,
    val storyTitle: String,
    val episodeId: String,
    val episodeTitle: String,
    val episodeNumber: Int,
    val price: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val paymentStatus: String = "PENDING_VERIFICATION",
    val status: TelegramRequestStatus = TelegramRequestStatus.PENDING
)

data class EpisodeEntitlement(
    val id: String,
    val userId: String,
    val episodeId: String,
    val storyId: String,
    val source: String, // "telegram_manual", "upi_inr_1", "admin_grant", "daily_free"
    val unlockedAt: Long = System.currentTimeMillis(),
    val status: String = "active"
)
