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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
 * Modern Search Screen:
 * Search placeholder: "Search stories or episodes"
 * Supports searching: Story title, Episode title, Author, Genre, Episode number
 * Shows: Recent searches, Trending searches, Search results (both stories and published episodes, including locked)
 */
@Composable
fun SearchScreen(
    stories: List<Story>,
    episodesMap: Map<String, List<Episode>> = emptyMap(),
    onBack: () -> Unit,
    onStoryClick: (Story) -> Unit,
    onEpisodeClick: (Story, Episode) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val recentSearches = remember {
        mutableStateListOf("The Warrior", "Jay Simha", "Action", "Episode 3")
    }
    val trendingSearches = listOf("The Warrior", "Jay Simha", "Aghori", "Divya Naag", "Soul of Vaekor", "Time Travel")

    // Story Results
    val matchedStories = if (searchQuery.isBlank()) {
        emptyList()
    } else {
        stories.filter { story ->
            story.title.contains(searchQuery, ignoreCase = true) ||
                story.genre.contains(searchQuery, ignoreCase = true) ||
                story.author.contains(searchQuery, ignoreCase = true) ||
                story.tags.any { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    // Episode Results (searches across all stories)
    val matchedEpisodes = if (searchQuery.isBlank()) {
        emptyList()
    } else {
        val q = searchQuery.trim()
        val numMatch = q.filter { it.isDigit() }.toIntOrNull()
        val list = mutableListOf<Pair<Story, Episode>>()
        episodesMap.forEach { (storyId, eps) ->
            val story = stories.find { it.id == storyId }
            if (story != null) {
                eps.forEach { ep ->
                    val matchesTitle = ep.title.contains(q, ignoreCase = true)
                    val matchesDesc = ep.description.contains(q, ignoreCase = true)
                    val matchesEpNum = numMatch != null && ep.episodeNumber == numMatch
                    if (matchesTitle || matchesDesc || matchesEpNum) {
                        list.add(Pair(story, ep))
                    }
                }
            }
        }
        list
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .testTag("search_screen")
    ) {
        // Search Input Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("search_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_input_field"),
                placeholder = { Text("Search stories or episodes", color = TextMutedDark) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CyanWave)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = TextMutedDark)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanWave,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                )
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (searchQuery.isBlank()) {
                // Recent Searches
                if (recentSearches.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RECENT SEARCHES",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = CyanWave
                            )
                            Text(
                                text = "Clear",
                                fontSize = 12.sp,
                                color = TextMutedDark,
                                modifier = Modifier.clickable { recentSearches.clear() }
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    items(recentSearches) { query ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { searchQuery = query }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.History, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = query, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    item { Spacer(modifier = Modifier.height(18.dp)) }
                }

                // Trending Searches
                item {
                    Text(
                        text = "TRENDING NOW",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = AmberWave
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                items(trendingSearches) { trend ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { searchQuery = trend }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = AmberWave, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = trend, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                // Search Results
                item {
                    Text(
                        text = "Stories (${matchedStories.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = CyanWave
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (matchedStories.isEmpty() && matchedEpisodes.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No results found for \"$searchQuery\"", color = Color.White, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Try searching for story titles, episodes, or genres", color = TextMutedDark, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    items(matchedStories) { story ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { onStoryClick(story) },
                            colors = CardDefaults.cardColors(containerColor = DarkCard)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(8.dp))
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
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${story.genre} • By ${story.author} • ${story.episodeCount} eps",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMutedDark
                                    )
                                }
                            }
                        }
                    }

                    if (matchedEpisodes.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Episodes (${matchedEpisodes.size})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = AmberWave
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        items(matchedEpisodes) { (story, episode) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clickable { onEpisodeClick(story, episode) },
                                colors = CardDefaults.cardColors(containerColor = DarkSurface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DarkSurfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (episode.isPremium) {
                                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = AmberWave, modifier = Modifier.size(18.dp))
                                        } else {
                                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = CyanWave, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Ep ${episode.episodeNumber}: ${episode.title}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${story.title} • ${episode.formattedDuration()}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMutedDark
                                        )
                                    }
                                    Surface(
                                        color = if (episode.isPremium) AmberWave.copy(alpha = 0.2f) else Color(0x2610B981),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (episode.isPremium) "₹1" else "FREE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (episode.isPremium) AmberWave else Color(0xFF10B981),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
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
