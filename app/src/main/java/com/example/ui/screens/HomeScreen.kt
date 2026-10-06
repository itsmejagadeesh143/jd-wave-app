package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.DailyFreeEpisodeConfig
import com.example.model.ListeningHistoryItem
import com.example.model.Story
import com.example.ui.components.FeaturedStoryCard
import com.example.ui.components.StoryCard
import com.example.ui.theme.AmberWave
import com.example.ui.theme.AmberWaveSubtle
import com.example.ui.theme.CyanWave
import com.example.ui.theme.CyanWaveSubtle
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.TextMutedDark
import java.util.Calendar
import java.util.TimeZone

/**
 * Premium Cinematic Home Screen for JD WAVE:
 * - Top greeting: "Good Evening 👋" -> "What do you want to listen to?"
 * - Search bar trigger
 * - TODAY'S FREE CARD (Normal day) or FRIDAY FREE FEST (on Fridays)
 * - Large horizontal Hero Featured Story Card
 * - Sections: Continue Listening, Trending, New Releases, Popular Stories,
 *   Featured Stories, Recommended For You, Recently Added, Genres
 */
@Composable
fun HomeScreen(
    stories: List<Story>,
    history: List<ListeningHistoryItem>,
    dailyFreeConfig: DailyFreeEpisodeConfig? = null,
    onStoryClick: (Story) -> Unit,
    onPlayHistoryItem: (ListeningHistoryItem) -> Unit,
    onPlayDailyFree: (storyId: String, episodeId: String) -> Unit = { _, _ -> },
    onCategoryClick: (String) -> Unit,
    onSearchClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val featuredStory = stories.find { it.isFeatured } ?: stories.firstOrNull()
    val trendingStories = stories.filter { it.isTrending }
    val topRatedStories = stories.filter { it.isTopRated }
    val newReleases = stories.filter { it.isNewRelease }
    val popularStories = stories.filter { it.isPopular }
    val recommendedStories = stories.shuffled().take(6)
    val recentlyAddedStories = stories.sortedByDescending { it.createdAt }.take(6)

    // IST Day & Hour calculation
    val istCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"))
    val isFriday = istCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
    val currentHourIst = istCal.get(Calendar.HOUR_OF_DAY)
    val isPast6AmIst = currentHourIst >= 6

    // Dynamic Greeting based on IST time
    val greeting = when (currentHourIst) {
        in 5..11 -> "Good Morning 👋"
        in 12..16 -> "Good Afternoon 👋"
        in 17..21 -> "Good Evening 👋"
        else -> "Good Night 🌙"
    }

    val genres = listOf(
        "Action",
        "Fantasy",
        "Mythology",
        "Thriller",
        "Horror",
        "Romance",
        "Sci-Fi",
        "Adventure",
        "Mystery"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_scroll"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Top Greeting & Search Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                Text(
                    text = "What do you want to listen to?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar Trigger
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                        .clickable(onClick = onSearchClick)
                        .testTag("home_search_bar_trigger"),
                    color = DarkSurface
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = CyanWave,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Search stories, episodes...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMutedDark
                        )
                    }
                }
            }
        }

        // Card 1: TODAY'S FREE CARD or FRIDAY FREE FEST
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                if (isFriday) {
                    // FRIDAY FREE FEST CARD
                    FridayFreeFestCard(
                        usedCount = 2,
                        totalCount = 5,
                        onChooseEpisode = {
                            if (dailyFreeConfig != null && dailyFreeConfig.active) {
                                onPlayDailyFree(dailyFreeConfig.storyId, dailyFreeConfig.episodeId)
                            } else {
                                featuredStory?.let { onStoryClick(it) }
                            }
                        }
                    )
                } else {
                    // DAILY FREE CARD
                    DailyFreeCard(
                        dailyConfig = dailyFreeConfig,
                        isPast6Am = isPast6AmIst,
                        onChooseEpisode = {
                            if (dailyFreeConfig != null && dailyFreeConfig.active) {
                                onPlayDailyFree(dailyFreeConfig.storyId, dailyFreeConfig.episodeId)
                            } else {
                                featuredStory?.let { onStoryClick(it) }
                            }
                        }
                    )
                }
            }
        }

        // Hero Featured Story
        if (featuredStory != null) {
            item {
                Box(modifier = Modifier.padding(16.dp)) {
                    FeaturedStoryCard(
                        story = featuredStory,
                        onListenClick = { onStoryClick(featuredStory) },
                        onCardClick = { onStoryClick(featuredStory) }
                    )
                }
            }
        }

        // Continue Listening Section
        if (history.isNotEmpty()) {
            item {
                HomeSectionHeader(title = "Continue Listening", actionText = null)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(history.take(6)) { item ->
                        ContinueListeningCard(
                            historyItem = item,
                            onClick = { onPlayHistoryItem(item) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Trending Stories
        if (trendingStories.isNotEmpty()) {
            item {
                HomeSectionHeader(
                    title = "Trending",
                    actionText = "See All",
                    onActionClick = { onCategoryClick("Trending") }
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(trendingStories) { story ->
                        StoryCard(story = story, onClick = { onStoryClick(story) })
                    }
                }
                Spacer(modifier = Modifier.height(22.dp))
            }
        }

        // New Releases
        if (newReleases.isNotEmpty()) {
            item {
                HomeSectionHeader(
                    title = "New Releases",
                    actionText = "See All",
                    onActionClick = { onCategoryClick("New Releases") }
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(newReleases) { story ->
                        StoryCard(story = story, onClick = { onStoryClick(story) })
                    }
                }
                Spacer(modifier = Modifier.height(22.dp))
            }
        }

        // Popular Stories
        if (popularStories.isNotEmpty()) {
            item {
                HomeSectionHeader(
                    title = "Popular Stories",
                    actionText = "See All",
                    onActionClick = { onCategoryClick("Popular") }
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(popularStories) { story ->
                        StoryCard(story = story, onClick = { onStoryClick(story) })
                    }
                }
                Spacer(modifier = Modifier.height(22.dp))
            }
        }

        // Recommended For You
        if (recommendedStories.isNotEmpty()) {
            item {
                HomeSectionHeader(
                    title = "Recommended For You",
                    actionText = "See All",
                    onActionClick = { onCategoryClick("All") }
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(recommendedStories) { story ->
                        StoryCard(story = story, onClick = { onStoryClick(story) })
                    }
                }
                Spacer(modifier = Modifier.height(22.dp))
            }
        }

        // Recently Added
        if (recentlyAddedStories.isNotEmpty()) {
            item {
                HomeSectionHeader(
                    title = "Recently Added",
                    actionText = "See All",
                    onActionClick = { onCategoryClick("All") }
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(recentlyAddedStories) { story ->
                        StoryCard(story = story, onClick = { onStoryClick(story) })
                    }
                }
                Spacer(modifier = Modifier.height(22.dp))
            }
        }

        // Genres Section
        item {
            HomeSectionHeader(title = "Genres", actionText = null)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(genres) { cat ->
                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
                            .clickable { onCategoryClick(cat) }
                            .testTag("home_genre_chip_$cat")
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Clean Section Header with Title and "See All"
 */
@Composable
fun HomeSectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp
            ),
            color = Color.White
        )
        if (actionText != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onActionClick)
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = CyanWave
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = CyanWave,
                    modifier = Modifier.height(16.dp)
                )
            }
        }
    }
}

/**
 * Dedicated Card: TODAY'S FREE EPISODE
 */
@Composable
fun DailyFreeCard(
    dailyConfig: DailyFreeEpisodeConfig?,
    isPast6Am: Boolean,
    onChooseEpisode: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, AmberWave.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
            .clickable(onClick = onChooseEpisode)
            .testTag("daily_free_card"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AmberWaveSubtle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Daily Free",
                    tint = AmberWave,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "🎁 TODAY'S FREE EPISODE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = AmberWave
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isPast6Am) "1 Free Episode Available" else "Choose 1 Episode FREE",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = if (isPast6Am) {
                        dailyConfig?.let { "${it.storyTitle} • Ep ${it.episodeNumber}" } ?: "Unlocked today for everyone"
                    } else {
                        "Available from 6:00 AM IST"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Button(
                onClick = onChooseEpisode,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberWave,
                    contentColor = DeepObsidian
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isPast6Am) "Play Free" else "Choose →",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * Dedicated Card: FRIDAY FREE FEST (Every Friday)
 */
@Composable
fun FridayFreeFestCard(
    usedCount: Int = 3,
    totalCount: Int = 5,
    onChooseEpisode: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, CoralFestBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onChooseEpisode)
            .testTag("friday_free_fest_card"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x33F43F5E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Friday Free Fest",
                            tint = Color(0xFFF43F5E),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "🔥 FRIDAY FREE FEST",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = Color(0xFFF43F5E)
                        )
                        Text(
                            text = "Unlock 5 Episodes FREE",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                Button(
                    onClick = onChooseEpisode,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF43F5E),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Choose Episode", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Indicators: ● ● ● ○ ○
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (i in 1..totalCount) {
                        val isUsed = i <= usedCount
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isUsed) Color(0xFFF43F5E) else DarkBorder)
                        )
                    }
                }
                Text(
                    text = "$usedCount of $totalCount used today",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

val CoralFestBorder = Color(0x66F43F5E)

/**
 * Continue Listening Card:
 * Story Artwork, Story Title, Episode Number, Playback Progress, Duration, Continue Button
 */
@Composable
fun ContinueListeningCard(
    historyItem: ListeningHistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(260.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("continue_listening_card_${historyItem.episode.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    AsyncImage(
                        model = historyItem.story.coverUrl,
                        contentDescription = historyItem.story.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = historyItem.story.title.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = CyanWave,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Episode ${historyItem.episode.episodeNumber}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${formatSecs(historyItem.positionSec)} / ${formatSecs(historyItem.durationSec)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Thin Progress Indicator
            LinearProgressIndicator(
                progress = { historyItem.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = CyanWave,
                trackColor = DarkBorder
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    color = CyanWaveSubtle,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = CyanWave,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Continue",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanWave
                        )
                    }
                }
            }
        }
    }
}

private fun formatSecs(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return "%02d:%02d".format(m, s)
}
