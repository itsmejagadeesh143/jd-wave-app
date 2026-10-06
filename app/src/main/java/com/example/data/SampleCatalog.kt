package com.example.data

import com.example.model.ContentStatus
import com.example.model.DailyFreeEpisodeConfig
import com.example.model.Episode
import com.example.model.Story
import java.util.Calendar
import java.util.TimeZone

object SampleCatalog {
    // Reliable audio streams
    private const val AUDIO_STREAM_1 = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
    private const val AUDIO_STREAM_2 = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
    private const val AUDIO_STREAM_3 = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    private const val AUDIO_STREAM_4 = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"

    // Unsplash covers
    private const val COVER_WARRIOR = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=600&auto=format&fit=crop&q=80"
    private const val COVER_JAY_SIMHA = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80"
    private const val COVER_DIVYA_NAAG = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80"
    private const val COVER_VAEKOR = "https://images.unsplash.com/photo-1514533450685-4493e01d1fdc?w=600&auto=format&fit=crop&q=80"
    private const val COVER_CHRONO = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80"
    private const val COVER_MIST = "https://images.unsplash.com/photo-1509114397022-ed747cca3f65?w=600&auto=format&fit=crop&q=80"
    private const val COVER_ECHOES = "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=600&auto=format&fit=crop&q=80"
    private const val COVER_VARANASI = "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?w=600&auto=format&fit=crop&q=80"
    private const val COVER_KYOTO = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=600&auto=format&fit=crop&q=80"
    private const val COVER_NOMAD = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&auto=format&fit=crop&q=80"

    fun getInitialStories(): List<Story> = listOf(
        Story(
            id = "story_warrior",
            title = "The Warrior: Amara Yoddhu",
            description = "In the blood-soaked hills of Deccan, an exiled prince rises with the cursed blade of the Sun God to reclaim his betrayed dynasty.",
            coverUrl = COVER_WARRIOR,
            bannerUrl = COVER_WARRIOR,
            genre = "Action & Mythology",
            author = "Vikramaditya Roy",
            rating = 4.9f,
            episodeCount = 20,
            totalListens = 3_420_000L,
            tags = listOf("Action", "Mythology", "Epic", "Revenge"),
            isFeatured = true,
            isTrending = true,
            isTopRated = true
        ),
        Story(
            id = "story_jay_simha",
            title = "Legend of Jay Simha",
            description = "The immortal roar of the Lion King echoes through centuries. When shadows threaten the realm of Avantika, the warrior king awakens.",
            coverUrl = COVER_JAY_SIMHA,
            genre = "Mythology",
            author = "Ananya Sen",
            rating = 4.8f,
            episodeCount = 18,
            totalListens = 2_150_000L,
            tags = listOf("Mythology", "War", "Honor"),
            isTrending = true,
            isPopular = true
        ),
        Story(
            id = "story_divya_naag",
            title = "Divya Naag: The Serpent Legacy",
            description = "Guarding the jewel of eternity beneath the subterranean kingdom of Nagaloka, young Aryan discovers he carries the venom of the ancient gods.",
            coverUrl = COVER_DIVYA_NAAG,
            genre = "Fantasy",
            author = "Devdutt Kashyap",
            rating = 4.7f,
            episodeCount = 15,
            totalListens = 1_840_000L,
            tags = listOf("Fantasy", "Supernatural", "Mystery"),
            isNewRelease = true,
            isEditorsPick = true
        ),
        Story(
            id = "story_vaekor",
            title = "Soul of Vaekor",
            description = "A grim dark fantasy world where necromancers harvest memories. A mute assassin must protect the last living soulstone.",
            coverUrl = COVER_VAEKOR,
            genre = "Dark Fantasy",
            author = "Marcus Vance",
            rating = 4.9f,
            episodeCount = 16,
            totalListens = 1_650_000L,
            tags = listOf("Dark Fantasy", "Magic", "Survival"),
            isTopRated = true,
            isPopular = true
        ),
        Story(
            id = "story_chrono_rider",
            title = "Chrono Rider: 2099",
            description = "Time loops, neon skylines, and illegal quantum accelerators. Kael races through fractured timelines to stop the total extinction of Neo-Tokyo.",
            coverUrl = COVER_CHRONO,
            genre = "Sci-Fi",
            author = "Ren Tanaka",
            rating = 4.8f,
            episodeCount = 14,
            totalListens = 1_420_000L,
            tags = listOf("Sci-Fi", "Cyberpunk", "Time Travel"),
            isNewRelease = true
        ),
        Story(
            id = "story_whispers_mist",
            title = "Whispers in the Mist",
            description = "A desolate Himalayan village shrouded in cursed fog where visitors vanish after dusk. An investigative journalist finds things better left buried.",
            coverUrl = COVER_MIST,
            genre = "Horror",
            author = "Rohan Sengupta",
            rating = 4.6f,
            episodeCount = 12,
            totalListens = 980_000L,
            tags = listOf("Horror", "Psychological", "Supernatural"),
            isEditorsPick = true
        ),
        Story(
            id = "story_midnight_echoes",
            title = "Midnight Echoes",
            description = "A cold case detective hears audio confessions from a telephone booth that was demolished forty years ago.",
            coverUrl = COVER_ECHOES,
            genre = "Thriller",
            author = "Clara Brooks",
            rating = 4.8f,
            episodeCount = 15,
            totalListens = 1_250_000L,
            tags = listOf("Thriller", "Crime", "Mystery"),
            isTrending = true
        ),
        Story(
            id = "story_varanasi",
            title = "Shadows of Varanasi",
            description = "On the sacred ghats of the Ganges, secret orders of Aghori mystics battle demonic incursions that human eyes cannot see.",
            coverUrl = COVER_VARANASI,
            genre = "Mystery",
            author = "Karan Sharma",
            rating = 4.7f,
            episodeCount = 16,
            totalListens = 890_000L,
            tags = listOf("Mystery", "Spiritual", "Occult"),
            isPopular = true
        ),
        Story(
            id = "story_kyoto",
            title = "Hearts in Kyoto",
            description = "A chance encounter beneath blooming cherry blossoms between an ambitious ceramic artist and a reclusive sound designer turns into an unforgettable bond.",
            coverUrl = COVER_KYOTO,
            genre = "Romance",
            author = "Mei Takahashi",
            rating = 4.9f,
            episodeCount = 14,
            totalListens = 2_400_000L,
            tags = listOf("Romance", "Drama", "Heartwarming"),
            isTopRated = true
        ),
        Story(
            id = "story_nomad",
            title = "The Galactic Nomad",
            description = "Far beyond the Orion Spur, an interstellar scavenger discovers an alien distress beacon containing the blueprint of the cosmos.",
            coverUrl = COVER_NOMAD,
            genre = "Adventure",
            author = "Zane Walker",
            rating = 4.6f,
            episodeCount = 18,
            totalListens = 1_120_000L,
            tags = listOf("Adventure", "Space", "Sci-Fi"),
            isEditorsPick = true
        )
    )

    fun generateEpisodesForStory(story: Story): List<Episode> {
        val audioUrls = listOf(AUDIO_STREAM_1, AUDIO_STREAM_2, AUDIO_STREAM_3, AUDIO_STREAM_4)
        val episodeTitles = listOf(
            "The Awakening Call",
            "Shadows in the Foothills",
            "The Oath of Iron",
            "Whispers of Betrayal",
            "The Forbidden Sanctuary",
            "Blood at Twilight",
            "Echoes of the Ancients",
            "The Hidden Gate",
            "Trials of the Desolate Plains",
            "A Spark in the Abyss",
            "The Venomous Truth",
            "Siege of the Citadel",
            "Broken Alliances",
            "The Celestial Convergence",
            "The Final Reckoning",
            "Rebirth from Ashes",
            "The Golden Mirage",
            "Veil of the Void",
            "Honor and Sacrifice",
            "Dawn of the New Era"
        )
        return (1..story.episodeCount).map { epNum ->
            val title = episodeTitles.getOrElse(epNum - 1) { "Episode $epNum: The Journey Continues" }
            val audio = audioUrls[(epNum - 1) % audioUrls.size]
            val durationSec = 1800 + (epNum * 73) % 900 // Between 30m and 45m
            // First 2 episodes are always Free for all listeners and guests.
            // Episode 3 and onwards are Premium (unless unlocked or unlocked via Daily Free 6 AM IST)
            val isPremiumEp = epNum > 2
            Episode(
                id = "ep_${story.id}_$epNum",
                storyId = story.id,
                episodeNumber = epNum,
                title = title,
                description = "Episode $epNum of ${story.title}. Discover pivotal revelations and high-stakes drama as the narrative reaches critical turning points.",
                audioUrl = audio,
                durationSec = durationSec,
                coverUrl = story.coverUrl,
                releaseDate = "2026-09-%02d".format((epNum % 28) + 1),
                status = ContentStatus.PUBLISHED,
                isPremium = isPremiumEp,
                priceInr = if (isPremiumEp) 1 else 0
            )
        }
    }

    /**
     * Helper to compute today's date key in Indian Standard Time (Asia/Kolkata, UTC+5:30)
     */
    fun getIstDateKey(epochMs: Long = System.currentTimeMillis()): String {
        val istTz = TimeZone.getTimeZone("Asia/Kolkata")
        val cal = Calendar.getInstance(istTz).apply { timeInMillis = epochMs }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return "%04d-%02d-%02d".format(year, month, day)
    }

    /**
     * Calculates the exact 6:00 AM IST epoch millisecond for a given IST date.
     */
    fun get6AmIstEpochMs(dateKey: String): Long {
        val parts = dateKey.split("-")
        if (parts.size != 3) return 0L
        val year = parts[0].toIntOrNull() ?: 2026
        val month = (parts[1].toIntOrNull() ?: 1) - 1
        val day = parts[2].toIntOrNull() ?: 1
        val istTz = TimeZone.getTimeZone("Asia/Kolkata")
        val cal = Calendar.getInstance(istTz).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
