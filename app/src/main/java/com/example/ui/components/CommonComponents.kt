package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.JDWaveRepository.EpisodeAccessStatus
import com.example.model.Episode
import com.example.model.Story
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

/**
 * Top Application Bar:
 * JD WAVE logo, Search icon, Notification bell, Admin portal / badge
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JDWaveTopBar(
    title: String = "JD WAVE",
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onAdminClick: () -> Unit = {},
    isAdminLoggedIn: Boolean = false,
    unreadNotificationsCount: Int = 2
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // JD WAVE stylized audio badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(CyanWave, Color(0xFF0284C7))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "JD WAVE",
                        tint = DeepObsidian,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.6.sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "TELUGU AUDIO STORIES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = CyanWave,
                        fontSize = 9.sp
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("top_bar_search_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search stories",
                    tint = Color.White
                )
            }
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier.testTag("top_bar_notifications_btn")
            ) {
                if (unreadNotificationsCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = AmberWave,
                                contentColor = DeepObsidian
                            ) {
                                Text("$unreadNotificationsCount", fontWeight = FontWeight.Bold)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White
                    )
                }
            }
            IconButton(
                onClick = onAdminClick,
                modifier = Modifier.testTag("top_bar_admin_btn")
            ) {
                if (isAdminLoggedIn) {
                    BadgedBox(
                        badge = {
                            Badge(containerColor = EmeraldSuccess, contentColor = Color.Black) {
                                Text("ADMIN")
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Admin Console",
                            tint = AmberWave
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Admin Portal Login",
                        tint = TextMutedDark
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DeepObsidian
        )
    )
}

/**
 * Animated micro-equalizer for currently playing audio items
 */
@Composable
fun AnimatedAudioWave(
    modifier: Modifier = Modifier,
    barCount: Int = 4,
    color: Color = CyanWave
) {
    val transition = rememberInfiniteTransition(label = "audio_wave")
    val heights = (0 until barCount).map { i ->
        transition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(350 + i * 140, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$i"
        )
    }

    Row(
        modifier = modifier.height(18.dp),
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        heights.forEach { anim ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight(anim.value)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(color)
            )
        }
    }
}

/**
 * Large Hero / Featured Story Card:
 * Cinematic artwork, subtle dark gradient, metadata badge, primary CTA
 */
@Composable
fun FeaturedStoryCard(
    story: Story,
    onListenClick: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onCardClick)
            .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
            .testTag("featured_story_card_${story.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = story.bannerUrl.ifEmpty { story.coverUrl },
                contentDescription = story.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Soft cinematic multi-stop gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x33000000),
                                Color(0x990A0D14),
                                Color(0xF50A0D14)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = AmberWave,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "FEATURED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = story.genre.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = CyanWave
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0x99000000), RoundedCornerShape(6.dp))
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
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.3.sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = story.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onListenClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanWave,
                            contentColor = DeepObsidian
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("listen_now_btn_${story.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Explore Story",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${story.episodeCount} Episodes • Free & Premium",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedDark
                    )
                }
            }
        }
    }
}

/**
 * Reusable Cinematic Story Card:
 * Consistent artwork aspect ratio, category, rating, status badges
 */
@Composable
fun StoryCard(
    story: Story,
    progressPercent: Float? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(145.dp)
            .clickable(onClick = onClick)
            .testTag("story_card_${story.id}")
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = story.coverUrl,
                    contentDescription = story.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Rating pill
                Surface(
                    color = Color(0xCC0A0D14),
                    shape = RoundedCornerShape(topStart = 8.dp),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AmberWave,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.5.dp))
                        Text(
                            text = "%.1f".format(story.rating),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                // Optional Status Badge in top left
                val badgeText = when {
                    story.isNewRelease -> "NEW"
                    story.isTrending -> "TRENDING"
                    story.isEditorsPick -> "EDITOR"
                    else -> null
                }
                if (badgeText != null) {
                    Surface(
                        color = if (badgeText == "NEW") CyanWave else AmberWave,
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
        if (progressPercent != null && progressPercent > 0f) {
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = CyanWave,
                trackColor = DarkBorder
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = story.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = story.genre,
                style = MaterialTheme.typography.labelSmall,
                color = CyanWave,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${story.episodeCount} eps",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark
            )
        }
    }
}

/**
 * Modern Episode Item Row:
 * Clear status badges:
 * 🎁 FREE
 * 🔓 UNLOCKED
 * 🔒 ₹1 [Unlock]
 * All episodes visible, clear duration, playback progress bar
 */
@Composable
fun EpisodeRowItem(
    episode: Episode,
    isPlayingThis: Boolean = false,
    accessStatus: EpisodeAccessStatus = EpisodeAccessStatus.FREE_DEFAULT,
    progressPercent: Float? = null,
    isDownloaded: Boolean = false,
    onPlayClick: () -> Unit,
    onUnlockClick: () -> Unit = {},
    onDownloadClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isLocked = accessStatus == EpisodeAccessStatus.LOCKED_PREMIUM

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = {
                if (isLocked) onUnlockClick() else onPlayClick()
            })
            .border(
                1.dp,
                if (isPlayingThis) CyanWave.copy(alpha = 0.5f) else DarkBorder,
                RoundedCornerShape(14.dp)
            )
            .testTag("episode_row_${episode.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlayingThis) DarkSurfaceVariant else DarkCard
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Play / Lock Status Indicator
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isPlayingThis -> CyanWave
                                isLocked -> AmberWaveSubtle
                                accessStatus == EpisodeAccessStatus.DAILY_FREE_UNLOCKED -> Color(0x2610B981)
                                else -> DarkSurfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isPlayingThis -> {
                            AnimatedAudioWave(barCount = 3, color = DeepObsidian)
                        }
                        isLocked -> {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked episode",
                                tint = AmberWave,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                        accessStatus == EpisodeAccessStatus.DAILY_FREE_UNLOCKED -> {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play daily free episode",
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play episode",
                                tint = CyanWave,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Episode Metadata
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "EPISODE ${episode.episodeNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            ),
                            color = if (isPlayingThis) CyanWave else Color(0xFFCBD5E1)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${episode.formattedDuration()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        // Dynamic Status Badge
                        when (accessStatus) {
                            EpisodeAccessStatus.FREE_DEFAULT -> {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0x2610B981)
                                ) {
                                    Text(
                                        text = "🎁 FREE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSuccess,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                    )
                                }
                            }
                            EpisodeAccessStatus.DAILY_FREE_UNLOCKED -> {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = AmberWaveSubtle
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = AmberWave,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "DAILY 6 AM FREE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AmberWave
                                        )
                                    }
                                }
                            }
                            EpisodeAccessStatus.ENTITLED -> {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0x2610B981)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LockOpen,
                                            contentDescription = null,
                                            tint = EmeraldSuccess,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "✓ UNLOCKED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldSuccess
                                        )
                                    }
                                }
                            }
                            EpisodeAccessStatus.LOCKED_PREMIUM -> {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = AmberWaveSubtle
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = AmberWave,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "🔒 ₹${episode.priceInr}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AmberWave
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = episode.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Download icon button if requested
                if (onDownloadClick != null) {
                    IconButton(
                        onClick = onDownloadClick,
                        modifier = Modifier.testTag("download_ep_btn_${episode.id}")
                    ) {
                        Icon(
                            imageVector = if (isDownloaded) Icons.Default.DownloadDone else Icons.Default.FileDownload,
                            contentDescription = if (isDownloaded) "Downloaded in Vault" else "Download to Vault",
                            tint = if (isDownloaded) CyanWave else TextMutedDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Action Button: Play vs Unlock
                if (isLocked) {
                    Button(
                        onClick = onUnlockClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberWave,
                            contentColor = DeepObsidian
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("unlock_btn_${episode.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Unlock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    IconButton(
                        onClick = onPlayClick,
                        modifier = Modifier.testTag("play_btn_${episode.id}")
                    ) {
                        Icon(
                            imageVector = if (isPlayingThis) Icons.Default.Headphones else Icons.Default.PlayArrow,
                            contentDescription = if (isPlayingThis) "Now streaming" else "Stream episode",
                            tint = if (isPlayingThis) AmberWave else CyanWave,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Progress bar if partially listened
            if (progressPercent != null && progressPercent > 0.02f) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progressPercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = CyanWave,
                    trackColor = DarkBorder
                )
            }
        }
    }
}
