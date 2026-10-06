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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.Story
import com.example.ui.components.StoryCard
import com.example.ui.theme.AmberWave
import com.example.ui.theme.CyanWave
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.TextMutedDark

/**
 * Explore Screen:
 * Top Search Bar trigger
 * Trending Searches
 * Genres Category Cards (Action, Fantasy, Mythology, Thriller, Horror, Romance, Sci-Fi, Adventure, Mystery)
 * Popular Stories
 * New Releases
 */
@Composable
fun ExploreScreen(
    stories: List<Story>,
    initialCategory: String? = null,
    onStoryClick: (Story) -> Unit,
    onSearchClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedGenre by remember { mutableStateOf(initialCategory ?: "All") }
    val genres = listOf(
        "All",
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

    val trendingSearches = listOf("The Warrior", "Jay Simha", "Aghori", "Time Travel", "Divya Naag")
    val popularStories = stories.filter { it.isPopular }
    val newReleases = stories.filter { it.isNewRelease }
    val filteredStories = if (selectedGenre == "All") {
        stories
    } else {
        stories.filter { it.genre.contains(selectedGenre, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Top Search Bar Trigger
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Explore Stories",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
                Text(
                    text = "Discover Telugu audio serials by genre, mood, or release",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMutedDark
                )
                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                        .clickable(onClick = onSearchClick)
                        .testTag("explore_search_trigger"),
                    color = DarkSurface
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CyanWave, modifier = Modifier.size(20.dp))
                        Text(text = "Search stories, episodes, authors...", color = TextMutedDark, fontSize = 14.sp)
                    }
                }
            }
        }

        // Trending Searches Pills
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "TRENDING SEARCHES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = CyanWave
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(trendingSearches) { query ->
                        Surface(
                            color = DarkSurfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                                .clickable { onSearchClick() }
                        ) {
                            Text(
                                text = query,
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Genres Category Cards
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Browse By Genre",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(genres) { g ->
                        val isSelected = selectedGenre == g
                        Surface(
                            color = if (isSelected) CyanWave else DarkSurfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    if (isSelected) CyanWave else DarkBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedGenre = g }
                                .testTag("genre_tab_$g")
                        ) {
                            Text(
                                text = g,
                                color = if (isSelected) DeepObsidian else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Filtered Stories Row
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = if (selectedGenre == "All") "All Stories" else "$selectedGenre Serials",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(filteredStories) { story ->
                        StoryCard(story = story, onClick = { onStoryClick(story) })
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Popular Stories Section
        if (popularStories.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Popular Stories",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(popularStories) { story ->
                            StoryCard(story = story, onClick = { onStoryClick(story) })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // New Releases Section
        if (newReleases.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "New Releases",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(newReleases) { story ->
                            StoryCard(story = story, onClick = { onStoryClick(story) })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
