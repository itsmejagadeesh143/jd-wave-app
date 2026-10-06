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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Episode
import com.example.model.ListeningHistoryItem
import com.example.model.Story
import com.example.ui.theme.AmberWave
import com.example.ui.theme.AmberWaveSubtle
import com.example.ui.theme.CyanWave
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.TextMutedDark

/**
 * Dedicated My Episodes Screen:
 * Sections:
 * 1. Purchased (₹1 instant unlocks)
 * 2. Free Unlocked (Daily 6 AM IST & catalog free)
 * 3. Telegram Unlocked (Community approved)
 * 4. Continue Listening
 *
 * Each item clearly shows: Story, Episode, Unlock type, Play button
 */
@Composable
fun MyEpisodesScreen(
    stories: List<Story>,
    episodesMap: Map<String, List<Episode>>,
    userEntitledEpisodes: Set<String>,
    history: List<ListeningHistoryItem>,
    onPlayEpisode: (Story, Episode) -> Unit,
    onBrowseCatalog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All Unlocked", "Purchased", "Daily Free", "Telegram", "Continue Listening")

    // Flatten all episodes across stories
    val allEpisodes = mutableListOf<Pair<Story, Episode>>()
    stories.forEach { story ->
        val eps = episodesMap[story.id] ?: emptyList()
        eps.forEach { ep ->
            allEpisodes.add(Pair(story, ep))
        }
    }

    // Filter by entitlement category
    val purchasedEpisodes = allEpisodes.filter { (_, ep) ->
        userEntitledEpisodes.contains(ep.id)
    }

    val freeEpisodes = allEpisodes.filter { (_, ep) ->
        !ep.isPremium
    }

    val telegramEpisodes = allEpisodes.filter { (_, ep) ->
        userEntitledEpisodes.contains(ep.id)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .testTag("my_episodes_screen")
    ) {
        // Header
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)) {
            Text(
                text = "My Episodes",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                color = Color.White
            )
            Text(
                text = "Your library of unlocked and recently listened audio drama episodes",
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedDark
            )
        }

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = CyanWave,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CyanWave
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) CyanWave else TextMutedDark
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // All Unlocked (Purchased + Free)
                    val combined = (purchasedEpisodes + freeEpisodes).distinctBy { it.second.id }
                    if (combined.isEmpty()) {
                        item {
                            EmptyEpisodesState(
                                message = "No unlocked episodes yet",
                                cta = "Browse Stories",
                                onAction = onBrowseCatalog
                            )
                        }
                    } else {
                        items(combined) { (story, episode) ->
                            val isPurchased = userEntitledEpisodes.contains(episode.id)
                            MyEpisodeCard(
                                story = story,
                                episode = episode,
                                unlockType = if (isPurchased) "PURCHASED ₹1" else "FREE EPISODE",
                                badgeColor = if (isPurchased) AmberWave else EmeraldSuccess,
                                onPlay = { onPlayEpisode(story, episode) }
                            )
                        }
                    }
                }
                1 -> {
                    // Purchased
                    if (purchasedEpisodes.isEmpty()) {
                        item {
                            EmptyEpisodesState(
                                message = "No purchased episodes yet",
                                cta = "Unlock ₹1 Episodes",
                                onAction = onBrowseCatalog
                            )
                        }
                    } else {
                        items(purchasedEpisodes) { (story, episode) ->
                            MyEpisodeCard(
                                story = story,
                                episode = episode,
                                unlockType = "PURCHASED ₹1",
                                badgeColor = AmberWave,
                                onPlay = { onPlayEpisode(story, episode) }
                            )
                        }
                    }
                }
                2 -> {
                    // Daily Free
                    val dailyList = freeEpisodes.take(15)
                    if (dailyList.isEmpty()) {
                        item {
                            EmptyEpisodesState(
                                message = "No free episodes currently active",
                                cta = "Explore Catalog",
                                onAction = onBrowseCatalog
                            )
                        }
                    } else {
                        items(dailyList) { (story, episode) ->
                            MyEpisodeCard(
                                story = story,
                                episode = episode,
                                unlockType = "FREE UNLOCKED",
                                badgeColor = EmeraldSuccess,
                                onPlay = { onPlayEpisode(story, episode) }
                            )
                        }
                    }
                }
                3 -> {
                    // Telegram Unlocked
                    if (telegramEpisodes.isEmpty()) {
                        item {
                            EmptyEpisodesState(
                                message = "No Telegram unlocks approved yet",
                                cta = "Request Telegram Unlock",
                                onAction = onBrowseCatalog
                            )
                        }
                    } else {
                        items(telegramEpisodes) { (story, episode) ->
                            MyEpisodeCard(
                                story = story,
                                episode = episode,
                                unlockType = "TELEGRAM UNLOCKED",
                                badgeColor = CyanWave,
                                onPlay = { onPlayEpisode(story, episode) }
                            )
                        }
                    }
                }
                4 -> {
                    // Continue Listening
                    if (history.isEmpty()) {
                        item {
                            EmptyEpisodesState(
                                message = "No listening history yet",
                                cta = "Start Listening",
                                onAction = onBrowseCatalog
                            )
                        }
                    } else {
                        items(history) { item ->
                            MyEpisodeCard(
                                story = item.story,
                                episode = item.episode,
                                unlockType = "CONTINUE",
                                badgeColor = CyanWave,
                                onPlay = { onPlayEpisode(item.story, item.episode) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyEpisodeCard(
    story: Story,
    episode: Episode,
    unlockType: String,
    badgeColor: Color,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable(onClick = onPlay)
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                AsyncImage(
                    model = episode.coverUrl.ifEmpty { story.coverUrl },
                    contentDescription = story.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = badgeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = unlockType,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${episode.formattedDuration()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Ep ${episode.episodeNumber}: ${episode.title}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanWave,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                color = CyanWave,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.clickable(onClick = onPlay)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = DeepObsidian,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Play",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepObsidian
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyEpisodesState(
    message: String,
    cta: String,
    onAction: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceVariant,
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        tint = CyanWave,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = message, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Unlocked episodes and audio tracks will appear here.", color = TextMutedDark, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = DeepObsidian),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = cta, fontWeight = FontWeight.Bold)
            }
        }
    }
}
