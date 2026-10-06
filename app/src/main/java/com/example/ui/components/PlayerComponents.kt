package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.player.PlayerState
import com.example.ui.theme.AmberWave
import com.example.ui.theme.CyanWave
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.TextMutedDark

/**
 * Persistent Mini Player Bar displayed above bottom navigation.
 * Remains visible across Home, Explore, My Episodes, Library, Profile.
 * Clicking opens Full Audio Player. Swipe down dismisses when appropriate.
 */
@Composable
fun MiniPlayer(
    playerState: PlayerState,
    onOpenFullPlayer: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onDismissSwipe: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val story = playerState.currentStory ?: return
    val episode = playerState.currentEpisode ?: return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .shadow(12.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onOpenFullPlayer)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount > 25f && onDismissSwipe != null) {
                        onDismissSwipe()
                    }
                }
            }
            .testTag("mini_player_bar"),
        color = DarkSurface,
        tonalElevation = 6.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Cover Thumbnail
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    AsyncImage(
                        model = episode.coverUrl.ifEmpty { story.coverUrl },
                        contentDescription = story.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Story & Episode title
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = story.title.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        ),
                        color = CyanWave,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Ep ${episode.episodeNumber}: ${episode.title}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Play / Pause button
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier.testTag("mini_player_play_pause")
                ) {
                    if (playerState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = CyanWave,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CyanWave),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                                tint = DeepObsidian,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Next Episode Button
                IconButton(
                    onClick = onNext,
                    modifier = Modifier.testTag("mini_player_next")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Episode",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Slim progress bar along bottom of mini player
            LinearProgressIndicator(
                progress = { playerState.progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = CyanWave,
                trackColor = DarkBorder
            )
        }
    }
}

/**
 * Immersive Full-Screen Audio Player:
 * Back button, More button, Large artwork, Story & Episode titles,
 * Current time / Duration, 10s Rewind, Play/Pause, 10s Forward, Next/Previous,
 * Speed selector (0.75x, 1x, 1.25x, 1.5x, 2x), Sleep Timer, Episodes Queue, Favorite, Share
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerModal(
    playerState: PlayerState,
    onDismiss: () -> Unit,
    onPlayPause: () -> Unit,
    onSeekFraction: (Float) -> Unit,
    onRewind15: () -> Unit,
    onForward30: () -> Unit,
    onPreviousEpisode: () -> Unit,
    onNextEpisode: () -> Unit,
    onSetSpeed: (Float) -> Unit,
    onSetSleepTimer: (Int?) -> Unit,
    onCancelSleepTimer: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleDownload: () -> Unit,
    onAddBookmark: (note: String) -> Unit,
    onClearQueue: () -> Unit,
    onRemoveFromQueue: (String) -> Unit,
    onToggleCarMode: () -> Unit,
    onCancelAutoNext: () -> Unit,
    onPlayNextNow: () -> Unit
) {
    val story = playerState.currentStory ?: return
    val episode = playerState.currentEpisode ?: return
    var showSpeedSheet by remember { mutableStateOf(false) }
    var showSleepSheet by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }
    var showBookmarkDialog by remember { mutableStateOf(false) }
    var bookmarkNoteInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .testTag("full_player_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Back & More options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("full_player_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Close player",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "NOW PLAYING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        ),
                        color = CyanWave
                    )
                    Text(
                        text = story.title,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = { showQueueSheet = true },
                    modifier = Modifier.testTag("full_player_more_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = "Queue & Episodes",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Large Cinematic Artwork
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                AsyncImage(
                    model = episode.coverUrl.ifEmpty { story.coverUrl },
                    contentDescription = episode.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Episode & Story Metadata
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Episode ${episode.episodeNumber}: ${episode.title}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${story.title} • ${story.genre} • ${story.language}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMutedDark,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Seek Progress Bar & Timestamps
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = playerState.progressFraction,
                    onValueChange = onSeekFraction,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = CyanWave,
                        activeTrackColor = CyanWave,
                        inactiveTrackColor = DarkBorder
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = playerState.formatCurrentTime(),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFCBD5E1)
                    )
                    Text(
                        text = playerState.formatDuration(),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMutedDark
                    )
                }
            }

            // Primary Playback Controls: Previous, 10s Rewind, Play/Pause, 10s Forward, Next
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousEpisode) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous episode",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                IconButton(onClick = onRewind15) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "Rewind 10 seconds",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                // Large Play / Pause button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(CyanWave)
                        .clickable(onClick = onPlayPause)
                        .testTag("full_player_play_pause_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    if (playerState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = DeepObsidian,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                            tint = DeepObsidian,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
                IconButton(onClick = onForward30) {
                    Icon(
                        imageVector = Icons.Default.Forward10,
                        contentDescription = "Forward 10 seconds",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = onNextEpisode) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next episode",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // Bottom Feature Controls: Speed, Sleep Timer, Episodes, Favorite, Share
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Playback speed
                Text(
                    text = "${playerState.playbackSpeed}x",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = CyanWave,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceVariant)
                        .clickable { showSpeedSheet = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("speed_btn")
                )
                // Sleep Timer
                IconButton(onClick = { showSleepSheet = true }) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Sleep timer",
                        tint = if (playerState.sleepTimerRemainingSec != null) AmberWave else Color(0xFFCBD5E1)
                    )
                }
                // Bookmark Position
                IconButton(onClick = { showBookmarkDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = "Bookmark",
                        tint = Color(0xFFCBD5E1)
                    )
                }
                // Favorite
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (playerState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (playerState.isFavorite) AmberWave else Color(0xFFCBD5E1)
                    )
                }
                // Download Toggle
                IconButton(onClick = onToggleDownload) {
                    Icon(
                        imageVector = if (playerState.isDownloaded) Icons.Default.DownloadDone else Icons.Default.Download,
                        contentDescription = "Download",
                        tint = if (playerState.isDownloaded) CyanWave else Color(0xFFCBD5E1)
                    )
                }
            }
        }
    }

    // Speed Bottom Sheet
    if (showSpeedSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSpeedSheet = false },
            containerColor = DarkSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Playback Speed",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(14.dp))
                val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
                speeds.forEach { spd ->
                    val isSelected = (playerState.playbackSpeed == spd)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSetSpeed(spd)
                                showSpeedSheet = false
                            }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${spd}x",
                            color = if (isSelected) CyanWave else Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Text("Active", color = CyanWave, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Sleep Timer Bottom Sheet
    if (showSleepSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSleepSheet = false },
            containerColor = DarkSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Sleep Timer",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(14.dp))
                val timers = listOf(
                    Pair("10 Minutes", 10),
                    Pair("20 Minutes", 20),
                    Pair("30 Minutes", 30),
                    Pair("45 Minutes", 45),
                    Pair("60 Minutes", 60),
                    Pair("End of Episode", 999)
                )
                timers.forEach { (label, mins) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSetSleepTimer(mins)
                                showSleepSheet = false
                            }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(text = label, color = Color.White)
                    }
                }
                if (playerState.sleepTimerRemainingSec != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            onCancelSleepTimer()
                            showSleepSheet = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberWave, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Turn Off Sleep Timer", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Queue / Episodes Sheet
    if (showQueueSheet) {
        ModalBottomSheet(
            onDismissRequest = { showQueueSheet = false },
            containerColor = DarkSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Next Up in Queue",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "${playerState.queue.size} upcoming",
                        color = CyanWave,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                if (playerState.queue.isEmpty()) {
                    Text(
                        text = "Current episode: Ep ${episode.episodeNumber} - ${episode.title}\nNo additional episodes queued.",
                        color = TextMutedDark,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.height(260.dp)) {
                        items(playerState.queue) { queueItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Ep ${queueItem.episode.episodeNumber}",
                                    fontWeight = FontWeight.Bold,
                                    color = CyanWave,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = queueItem.episode.title,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = queueItem.episode.formattedDuration(),
                                    color = TextMutedDark,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Bookmark Dialog
    if (showBookmarkDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showBookmarkDialog = false },
            title = { Text("Bookmark Position", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column {
                    Text(
                        text = "Save timestamp ${playerState.formatCurrentTime()} with an optional note:",
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = bookmarkNoteInput,
                        onValueChange = { bookmarkNoteInput = it },
                        placeholder = { Text("e.g. Crucial plot twist") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddBookmark(bookmarkNoteInput)
                        bookmarkNoteInput = ""
                        showBookmarkDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = DeepObsidian)
                ) {
                    Text("Save Bookmark", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showBookmarkDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = Color.White)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = DarkSurface
        )
    }
}
