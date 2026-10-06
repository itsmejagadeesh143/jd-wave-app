package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.JDWaveRepository.EpisodeAccessStatus
import com.example.model.Episode
import com.example.model.PlaybackProgress
import com.example.model.Story
import com.example.ui.components.EpisodeRowItem
import com.example.ui.components.StoryCard
import com.example.ui.theme.AmberWave
import com.example.ui.theme.CyanWave
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.TextMutedDark

/**
 * Story Detail Screen:
 * Top: Large cinematic cover artwork with back and favorite action
 * Below:
 * Story title
 * Author, Genre, Rating, Episode count
 * Action buttons:
 * ▶ Continue / Play, ❤️ Follow, ↗ Share
 * Description with expandable "Read more"
 * Episodes tab (All published episodes visible, clear badges, paid ₹1 [Unlock], free [Play], unlocked [Play])
 * Guest conversion bottom sheet on locked episode click
 * Telegram Request modal with JDW formatted code
 */
@Composable
fun StoryDetailsScreen(
    story: Story,
    episodes: List<Episode>,
    recommendedStories: List<Story>,
    isFavorite: Boolean,
    activeEpisodeId: String?,
    isPlaying: Boolean,
    playbackProgressMap: Map<String, PlaybackProgress>,
    isDownloaded: (String) -> Boolean,
    getEpisodeAccessStatus: (Episode) -> EpisodeAccessStatus = { EpisodeAccessStatus.FREE_DEFAULT },
    isGuestUser: Boolean = false,
    onBack: () -> Unit,
    onPlayEpisode: (Episode) -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleDownload: (Episode) -> Unit = {},
    onUnlockDirectInr: (Episode) -> Unit = {},
    onRequestTelegramUnlock: (Episode, onCodeGenerated: (String) -> Unit) -> Unit = { _, _ -> },
    onPromptGuestToSignIn: () -> Unit = {},
    onRecommendedClick: (Story) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isFollowing by remember { mutableStateOf(false) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var unlockTargetEpisode by remember { mutableStateOf<Episode?>(null) }
    var showGuestConversionDialog by remember { mutableStateOf(false) }
    var generatedTelegramCode by remember { mutableStateOf<String?>(null) }

    val partiallyListenedEp = episodes.firstOrNull { ep ->
        val prog = playbackProgressMap[ep.id]
        prog != null && !prog.completed && prog.positionSec > 10
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .testTag("story_details_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Hero Artwork with Navigation Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                AsyncImage(
                    model = story.bannerUrl.ifEmpty { story.coverUrl },
                    contentDescription = story.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Cinematic Dark Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0x99000000),
                                    Color(0x44000000),
                                    DeepObsidian
                                )
                            )
                        )
                )

                // Navigation Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 36.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0x990A0D14))
                            .testTag("story_details_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0x990A0D14))
                            .testTag("story_details_fav_btn")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) AmberWave else Color.White
                        )
                    }
                }
            }
        }

        // Story Title & Metadata
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = CyanWave,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = story.genre.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp,
                            color = DeepObsidian,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(DarkSurfaceVariant, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AmberWave,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "%.1f".format(story.rating),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Text(
                        text = "• ${story.episodeCount} Episodes",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "By ${story.author} • ${story.language}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyanWave
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons: ▶ Continue / Play, ❤️ Follow, ↗ Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val firstEp = episodes.firstOrNull()
                    val targetPlayEp = partiallyListenedEp ?: firstEp
                    Button(
                        onClick = {
                            if (targetPlayEp != null) {
                                val status = getEpisodeAccessStatus(targetPlayEp)
                                if (status != EpisodeAccessStatus.LOCKED_PREMIUM) {
                                    onPlayEpisode(targetPlayEp)
                                } else {
                                    if (isGuestUser) {
                                        showGuestConversionDialog = true
                                    } else {
                                        unlockTargetEpisode = targetPlayEp
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanWave,
                            contentColor = DeepObsidian
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("story_details_play_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (partiallyListenedEp != null) "Continue Ep ${partiallyListenedEp.episodeNumber}" else "▶ Play Episode 1",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Follow Button
                    OutlinedButton(
                        onClick = { isFollowing = !isFollowing },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isFollowing) AmberWave else Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isFollowing) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isFollowing) "Following" else "Follow")
                    }

                    // Share Button
                    OutlinedButton(
                        onClick = {},
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        modifier = Modifier.weight(0.8f)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Description with "Read more" toggle
                Column {
                    Text(
                        text = story.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1),
                        maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 3,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isDescriptionExpanded) "Show less" else "Read more...",
                        color = CyanWave,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { isDescriptionExpanded = !isDescriptionExpanded }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Tabs: Episodes vs Details
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = CyanWave,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = CyanWave
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Episodes (${episodes.size})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Recommended", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Episode list: all published visible, clear status badges
                items(episodes) { episode ->
                    val isPlayingThis = episode.id == activeEpisodeId && isPlaying
                    val prog = playbackProgressMap[episode.id]
                    val accessStatus = getEpisodeAccessStatus(episode)
                    val isUnlocked = accessStatus != EpisodeAccessStatus.LOCKED_PREMIUM

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 5.dp)
                    ) {
                        EpisodeRowItem(
                            episode = episode,
                            isPlayingThis = isPlayingThis,
                            accessStatus = accessStatus,
                            progressPercent = prog?.progressPercent,
                            isDownloaded = isDownloaded(episode.id),
                            onPlayClick = {
                                if (isUnlocked) {
                                    onPlayEpisode(episode)
                                } else {
                                    if (isGuestUser) {
                                        showGuestConversionDialog = true
                                    } else {
                                        unlockTargetEpisode = episode
                                        generatedTelegramCode = null
                                    }
                                }
                            },
                            onUnlockClick = {
                                if (isGuestUser) {
                                    showGuestConversionDialog = true
                                } else {
                                    unlockTargetEpisode = episode
                                    generatedTelegramCode = null
                                }
                            },
                            onDownloadClick = {
                                onToggleDownload(episode)
                            }
                        )
                    }
                }
            }
            1 -> {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Listeners Also Enjoyed",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            items(recommendedStories) { rec ->
                                StoryCard(
                                    story = rec,
                                    onClick = { onRecommendedClick(rec) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Guest Conversion Bottom Dialog
    if (showGuestConversionDialog) {
        AlertDialog(
            onDismissRequest = { showGuestConversionDialog = false },
            title = {
                Text("Create your free JD WAVE account", fontWeight = FontWeight.Black, color = Color.White)
            },
            text = {
                Column {
                    Text(
                        text = "Unlock premium episodes and keep your listening history synced across all your devices.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Guests can freely browse and listen to all Daily 6 AM Free episodes.",
                        color = TextMutedDark,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showGuestConversionDialog = false
                        onPromptGuestToSignIn()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = DeepObsidian),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("guest_modal_create_account_btn")
                ) {
                    Text("Create Account / Sign In", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showGuestConversionDialog = false },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("guest_modal_browse_btn")
                ) {
                    Text("Continue Browsing", color = Color.White)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Paid Episode Unlock Dialog (Instant ₹1 + Telegram Unlock)
    if (unlockTargetEpisode != null) {
        val targetEp = unlockTargetEpisode!!
        AlertDialog(
            onDismissRequest = {
                unlockTargetEpisode = null
                generatedTelegramCode = null
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = AmberWave)
                    Text("Unlock Episode ${targetEp.episodeNumber}", fontWeight = FontWeight.Black, color = Color.White)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "${story.title} • ${targetEp.title} (${targetEp.formattedDuration()})",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanWave
                    )

                    // Instant ₹1 Unlock
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Instant Quick Unlock", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                    Text("UPI / GPay / PhonePe", color = TextMutedDark, fontSize = 11.sp)
                                }
                                Text("₹${targetEp.priceInr}", fontWeight = FontWeight.Black, color = AmberWave, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    onUnlockDirectInr(targetEp)
                                    unlockTargetEpisode = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AmberWave, contentColor = DeepObsidian),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("direct_inr_unlock_btn_${targetEp.id}")
                            ) {
                                Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pay ₹${targetEp.priceInr} & Unlock Instantly", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Telegram Request
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = CyanWave, modifier = Modifier.size(16.dp))
                                Text("Need manual unlock?", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Message JD WAVE on Telegram (@JDWaveSupport) with your unique request code.",
                                color = TextMutedDark,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            if (generatedTelegramCode == null) {
                                OutlinedButton(
                                    onClick = {
                                        onRequestTelegramUnlock(targetEp) { code ->
                                            generatedTelegramCode = "JDW-${code.take(6).uppercase()}-${targetEp.episodeNumber}"
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("generate_telegram_code_btn")
                                ) {
                                    Text("Generate Request Code", color = CyanWave, fontSize = 12.sp)
                                }
                            } else {
                                Surface(
                                    color = AmberWave.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("REQUEST CODE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMutedDark)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = generatedTelegramCode!!,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.5.sp,
                                            color = AmberWave
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Send to Telegram admin to approve.", fontSize = 10.sp, color = Color(0xFFCBD5E1))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        unlockTargetEpisode = null
                        generatedTelegramCode = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = Color.White)
                ) {
                    Text("Close")
                }
            },
            containerColor = DarkSurface
        )
    }
}
