package com.example.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity

/**
 * Foreground Audio Playback Service for background audio and lock-screen controls.
 */
class JDWaveAudioService : Service() {
    companion object {
        const val CHANNEL_ID = "jd_wave_audio_playback"
        const val NOTIFICATION_ID = 1001
        const val ACTION_PLAY = "com.example.jdwave.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.jdwave.ACTION_PAUSE"
        const val ACTION_NEXT = "com.example.jdwave.ACTION_NEXT"
        const val ACTION_PREV = "com.example.jdwave.ACTION_PREV"
        const val ACTION_STOP = "com.example.jdwave.ACTION_STOP"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val storyTitle = intent?.getStringExtra("story_title") ?: "JD WAVE Audio"
        val episodeTitle = intent?.getStringExtra("episode_title") ?: "Streaming Episode"
        val isPlaying = intent?.getBooleanExtra("is_playing", true) ?: true

        val notification = buildNotification(storyTitle, episodeTitle, isPlaying)
        startForeground(NOTIFICATION_ID, notification)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "JD WAVE Audio Streaming",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows now-playing audio story controls"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(storyTitle: String, episodeTitle: String, isPlaying: Boolean): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(storyTitle)
            .setContentText(episodeTitle)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(isPlaying)
            .build()
    }
}
