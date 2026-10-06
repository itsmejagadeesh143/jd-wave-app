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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Bookmark
import com.example.model.DownloadRecord
import com.example.model.ListeningHistoryItem
import com.example.model.Story
import com.example.ui.theme.AmberWave
import com.example.ui.theme.CyanWave
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.TextMutedDark

/**
 * Clean Library Screen:
 * Sections: Favorites, Following, Recently Played, Completed
 * Also supports Vault Downloads and Bookmarks.
 * Clean empty states for all sections.
 */
@Composable
fun LibraryScreen(
    stories: List<Story>,
    favoriteStoryIds: Set<String> = emptySet(),
    favorites: Set<String> = favoriteStoryIds,
    history: List<ListeningHistoryItem> = emptyList(),
    bookmarks: List<Bookmark> = emptyList(),
    downloads: List<DownloadRecord> = emptyList(),
    isGuestUser: Boolean = false,
    onStoryClick: (Story) -> Unit = {},
    onPlayHistoryItem: (ListeningHistoryItem) -> Unit = {},
    onPlayDownload: (DownloadRecord) -> Unit = {},
    onDeleteDownload: (String) -> Unit = {},
    onSeekBookmark: (Bookmark) -> Unit = {},
    onDeleteBookmark: (String) -> Unit = {},
    onClearHistory: () -> Unit = {},
    onPromptSignIn: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeFavorites = if (favoriteStoryIds.isNotEmpty()) favoriteStoryIds else favorites
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Favorites", "Following", "Recently Played", "Completed", "Vault Downloads")

    val favoriteStories = stories.filter { activeFavorites.contains(it.id) }
    val followingStories = stories.filter { it.isTrending || it.isEditorsPick }.take(4)
    val completedHistory = history.filter { it.positionSec >= it.durationSec * 0.95f }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .testTag("library_screen")
    ) {
        // Library Header
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)) {
            Text(
                text = "Library",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                color = Color.White
            )
            Text(
                text = "Your saved Telugu audio stories, following list, and offline vault",
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
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        val count = when (index) {
                            0 -> favoriteStories.size
                            1 -> followingStories.size
                            2 -> history.size
                            3 -> completedHistory.size
                            4 -> downloads.size
                            else -> 0
                        }
                        Text(
                            text = "$title ($count)",
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
                    // Favorites
                    if (favoriteStories.isEmpty()) {
                        item {
                            EmptyLibraryView(
                                icon = Icons.Default.Favorite,
                                title = "No favorites yet",
                                subtitle = "Explore stories and save your favorites here."
                            )
                        }
                    } else {
                        items(favoriteStories) { story ->
                            LibraryStoryCard(story = story, onClick = { onStoryClick(story) })
                        }
                    }
                }
                1 -> {
                    // Following
                    if (followingStories.isEmpty()) {
                        item {
                            EmptyLibraryView(
                                icon = Icons.Default.CheckCircle,
                                title = "Not following any serials yet",
                                subtitle = "Follow stories from the Story Detail page to get notified on new releases."
                            )
                        }
                    } else {
                        items(followingStories) { story ->
                            LibraryStoryCard(story = story, onClick = { onStoryClick(story) })
                        }
                    }
                }
                2 -> {
                    // Recently Played
                    if (history.isEmpty()) {
                        item {
                            EmptyLibraryView(
                                icon = Icons.Default.History,
                                title = "No listening history yet",
                                subtitle = "Audio episodes you listen to will automatically appear here."
                            )
                        }
                    } else {
                        items(history) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clickable { onPlayHistoryItem(item) }
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
                                            .clip(RoundedCornerShape(8.dp))
                                    ) {
                                        AsyncImage(
                                            model = item.story.coverUrl,
                                            contentDescription = item.story.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.story.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Ep ${item.episode.episodeNumber}: ${item.episode.title}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyanWave
                                        )
                                    }
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = CyanWave)
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Completed
                    if (completedHistory.isEmpty()) {
                        item {
                            EmptyLibraryView(
                                icon = Icons.Default.CheckCircle,
                                title = "No completed episodes yet",
                                subtitle = "Episodes you finish listening to will appear in this archive."
                            )
                        }
                    } else {
                        items(completedHistory) { item ->
                            LibraryStoryCard(story = item.story, onClick = { onStoryClick(item.story) })
                        }
                    }
                }
                4 -> {
                    // Vault Downloads
                    if (downloads.isEmpty()) {
                        item {
                            EmptyLibraryView(
                                icon = Icons.Default.DownloadDone,
                                title = "No offline downloads yet",
                                subtitle = "Downloaded audio is securely encrypted in your app vault."
                            )
                        }
                    } else {
                        items(downloads) { dl ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clickable { onPlayDownload(dl) }
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
                                            .clip(RoundedCornerShape(8.dp))
                                    ) {
                                        AsyncImage(
                                            model = dl.coverUrl,
                                            contentDescription = dl.storyTitle,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = dl.episodeTitle,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${dl.storyTitle} • Ep ${dl.episodeNumber}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyanWave
                                        )
                                    }
                                    IconButton(onClick = { onDeleteDownload(dl.episodeId) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = TextMutedDark)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryStoryCard(
    story: Story,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable(onClick = onClick)
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
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                AsyncImage(
                    model = story.coverUrl,
                    contentDescription = story.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${story.genre} • By ${story.author}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanWave
                )
                Text(
                    text = "${story.episodeCount} Episodes",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark
                )
            }
        }
    }
}

@Composable
fun EmptyLibraryView(
    icon: ImageVector,
    title: String,
    subtitle: String
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
                    Icon(imageVector = icon, contentDescription = null, tint = CyanWave, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, color = TextMutedDark, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(horizontal = 24.dp))
        }
    }
}
