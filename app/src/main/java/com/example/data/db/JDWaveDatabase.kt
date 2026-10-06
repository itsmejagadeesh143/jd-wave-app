package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DownloadedAudioEntity::class],
    version = 1,
    exportSchema = false
)
abstract class JDWaveDatabase : RoomDatabase() {
    abstract fun downloadedAudioDao(): DownloadedAudioDao

    companion object {
        @Volatile
        private var INSTANCE: JDWaveDatabase? = null

        fun getInstance(context: Context): JDWaveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JDWaveDatabase::class.java,
                    "jd_wave_local.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
