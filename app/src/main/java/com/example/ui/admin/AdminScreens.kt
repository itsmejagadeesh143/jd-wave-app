package com.example.ui.admin

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import com.example.model.AdminActivityLog
import com.example.model.AdminAnalyticsData
import com.example.model.AdminRole
import com.example.model.AdminUser
import com.example.model.ContentStatus
import com.example.model.DailyFreeEpisodeConfig
import com.example.model.Episode
import com.example.model.Story
import com.example.model.TelegramRequestStatus
import com.example.model.TelegramUnlockRequest
import com.example.ui.theme.AmberWave
import com.example.ui.theme.CyanWave
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.TextMutedDark
import java.util.UUID

@Composable
fun AdminLoginScreen(
    onLoginSuccess: (email: String, role: AdminRole) -> Unit,
    onBackToApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var emailInput by remember { mutableStateOf("admin@jdwave.app") }
    var passwordInput by remember { mutableStateOf("WaveAdmin2026!") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isAuthenticating by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(24.dp)
            .testTag("admin_login_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onBackToApp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .testTag("admin_back_to_app_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to public app",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(AmberWave),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "JD WAVE ADMIN CONSOLE",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = Color.White
            )
            Text(
                text = "Protected Content & Audio Publishing Platform",
                style = MaterialTheme.typography.bodySmall,
                color = TextMutedDark
            )
            Spacer(modifier = Modifier.height(28.dp))
            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                label = { Text("Admin Email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_email_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanWave,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                )
            )
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_password_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanWave,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                )
            )
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFF43F5E),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (emailInput.isNotBlank() && passwordInput.length >= 6) {
                        isAuthenticating = true
                        onLoginSuccess(emailInput, AdminRole.SUPER_ADMIN)
                    } else {
                        errorMessage = "Please enter valid admin credentials."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("admin_login_submit_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberWave,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isAuthenticating) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                } else {
                    Text("ACCESS ADMIN PORTAL", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Default Super Admin: admin@jdwave.app",
                style = MaterialTheme.typography.labelSmall,
                color = CyanWave
            )
        }
    }
}

/**
 * Full Admin Dashboard Portal
 */
@Composable
fun AdminDashboardScreen(
    adminUser: AdminUser,
    stories: List<Story>,
    episodesMap: Map<String, List<Episode>>,
    activityLogs: List<AdminActivityLog>,
    analytics: AdminAnalyticsData,
    dailyFreeConfig: DailyFreeEpisodeConfig? = null,
    onConfigureDailyFree: (DailyFreeEpisodeConfig) -> Unit = {},
    telegramRequests: List<TelegramUnlockRequest> = emptyList(),
    onApproveTelegramRequest: (String) -> Unit = {},
    onRejectTelegramRequest: (String) -> Unit = {},
    onCreateStory: (Story) -> Unit,
    onUpdateStory: (Story) -> Unit,
    onDeleteStory: (String) -> Unit,
    onSetStoryStatus: (String, ContentStatus) -> Unit,
    onAddEpisode: (String, Episode) -> Unit,
    onDeleteEpisode: (String, String) -> Unit,
    onReorderEpisodes: (String, List<Episode>) -> Unit,
    onSetEpisodePremium: (storyId: String, episodeId: String, isPremium: Boolean, price: Int) -> Unit = { _, _, _, _ -> },
    onLogout: () -> Unit,
    onBackToPublicApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Overview", "Daily Free (6 AM)", "Stories", "Upload Audio", "Telegram Unlocks", "Analytics", "Storage", "Activity Logs")

    var showCreateStoryDialog by remember { mutableStateOf(false) }
    var editingStory by remember { mutableStateOf<Story?>(null) }
    var manageEpisodesStory by remember { mutableStateOf<Story?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .testTag("admin_dashboard_screen")
    ) {
        // Admin Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackToPublicApp) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Return to App", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(text = "JD WAVE • Admin Portal", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = AmberWave)
                    Text(text = "${adminUser.email} (${adminUser.role})", style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { selectedTab = 3 },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberWave, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_upload_episode_header_btn")
                ) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload Episode", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showCreateStoryDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_new_story_quick_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Story", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onLogout) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                }
            }
        }

        // Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurfaceVariant,
            contentColor = CyanWave,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = AmberWave
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(text = title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                AdminOverviewTab(
                    stories = stories,
                    episodesMap = episodesMap,
                    analytics = analytics,
                    activityLogs = activityLogs,
                    onNavigateToTab = { selectedTab = it },
                    onOpenNewStory = { showCreateStoryDialog = true }
                )
            }
            1 -> {
                AdminDailyFreeTab(
                    stories = stories,
                    episodesMap = episodesMap,
                    currentConfig = dailyFreeConfig,
                    onSaveConfig = onConfigureDailyFree
                )
            }
            2 -> {
                AdminStoriesTab(
                    stories = stories,
                    onEditStory = { editingStory = it },
                    onDeleteStory = onDeleteStory,
                    onToggleStatus = onSetStoryStatus,
                    onManageEpisodes = { manageEpisodesStory = it }
                )
            }
            3 -> {
                AdminUploadAudioTab(
                    stories = stories,
                    onAddEpisode = onAddEpisode
                )
            }
            4 -> {
                AdminTelegramRequestsTab(
                    requests = telegramRequests,
                    onApprove = onApproveTelegramRequest,
                    onReject = onRejectTelegramRequest
                )
            }
            5 -> {
                AdminAnalyticsTab(analytics = analytics, stories = stories)
            }
            6 -> {
                AdminStorageTab(analytics = analytics, stories = stories, episodesMap = episodesMap)
            }
            7 -> {
                AdminActivityLogsTab(logs = activityLogs)
            }
        }
    }

    if (showCreateStoryDialog) {
        StoryEditorDialog(
            story = null,
            onDismiss = { showCreateStoryDialog = false },
            onSave = { newStory ->
                onCreateStory(newStory)
                showCreateStoryDialog = false
            }
        )
    }

    if (editingStory != null) {
        StoryEditorDialog(
            story = editingStory,
            onDismiss = { editingStory = null },
            onSave = { updated ->
                onUpdateStory(updated)
                editingStory = null
            }
        )
    }

    if (manageEpisodesStory != null) {
        val st = manageEpisodesStory!!
        val epList = episodesMap[st.id] ?: emptyList()
        EpisodeManagerDialog(
            story = st,
            episodes = epList,
            onDismiss = { manageEpisodesStory = null },
            onAddEpisode = { ep -> onAddEpisode(st.id, ep) },
            onDeleteEpisode = { epId -> onDeleteEpisode(st.id, epId) },
            onReorder = { reordered -> onReorderEpisodes(st.id, reordered) },
            onSetEpisodePremium = { epId, isPrem, pr -> onSetEpisodePremium(st.id, epId, isPrem, pr) }
        )
    }
}

/**
 * Admin Daily 6:00 AM IST Free Episode Configuration Tab
 */
@Composable
fun AdminDailyFreeTab(
    stories: List<Story>,
    episodesMap: Map<String, List<Episode>>,
    currentConfig: DailyFreeEpisodeConfig?,
    onSaveConfig: (DailyFreeEpisodeConfig) -> Unit
) {
    var selectedStoryId by remember { mutableStateOf(currentConfig?.storyId ?: (stories.firstOrNull()?.id ?: "")) }
    val episodesForStory = episodesMap[selectedStoryId] ?: emptyList()
    var selectedEpisodeId by remember { mutableStateOf(currentConfig?.episodeId ?: (episodesForStory.getOrNull(2)?.id ?: "")) }
    var selectedDateKey by remember { mutableStateOf(currentConfig?.dateKey ?: com.example.data.SampleCatalog.getIstDateKey()) }
    var isActive by remember { mutableStateOf(currentConfig?.active ?: true) }
    var saveSuccessMsg by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AmberWave.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AmberWave,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Daily Free Episode Scheduler",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Unlocks one episode at 6:00 AM IST automatically for all users (Guests & Members)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedDark
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Server Authoritative: Episode unlock is verified by server-side IST timestamp, independent of device clocks.",
                        color = CyanWave,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Current Active Banner
        if (currentConfig != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("CURRENTLY ACTIVE CONFIGURATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberWave)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${currentConfig.storyTitle} • Ep ${currentConfig.episodeNumber}: ${currentConfig.episodeTitle}",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Date: ${currentConfig.dateKey} (6:00 AM IST) • Status: ${if (currentConfig.active) "ACTIVE" else "DISABLED"}",
                            fontSize = 11.sp,
                            color = TextMutedDark
                        )
                    }
                }
            }
        }

        // Configuration Form
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Configure Free Daily Episode", fontWeight = FontWeight.Bold, color = Color.White)

                    OutlinedTextField(
                        value = selectedDateKey,
                        onValueChange = { selectedDateKey = it },
                        label = { Text("Date (YYYY-MM-DD in IST)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Select Story
                    Text("Select Story:", style = MaterialTheme.typography.bodySmall, color = TextMutedDark)
                    stories.forEach { st ->
                        val isSelected = selectedStoryId == st.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedStoryId = st.id
                                    val newEps = episodesMap[st.id] ?: emptyList()
                                    selectedEpisodeId = newEps.getOrNull(2)?.id ?: (newEps.firstOrNull()?.id ?: "")
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = if (isSelected) CyanWave else TextMutedDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = st.title, color = if (isSelected) Color.White else Color.Gray, fontSize = 13.sp)
                        }
                    }

                    // Select Episode
                    Text("Select Episode to Unlock:", style = MaterialTheme.typography.bodySmall, color = TextMutedDark)
                    episodesForStory.forEach { ep ->
                        val isSelected = selectedEpisodeId == ep.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedEpisodeId = ep.id }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isSelected) AmberWave else TextMutedDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ep ${ep.episodeNumber}: ${ep.title} (${if (ep.isPremium) "₹${ep.priceInr} Premium" else "Free"})",
                                color = if (isSelected) Color.White else Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Active Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable 6:00 AM IST Unlock", color = Color.White)
                        androidx.compose.material3.Switch(
                            checked = isActive,
                            onCheckedChange = { isActive = it }
                        )
                    }

                    if (saveSuccessMsg != null) {
                        Text(text = saveSuccessMsg!!, color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val story = stories.find { it.id == selectedStoryId }
                            val ep = episodesForStory.find { it.id == selectedEpisodeId }
                            val unlockEpoch = com.example.data.SampleCatalog.get6AmIstEpochMs(selectedDateKey)
                            val cfg = DailyFreeEpisodeConfig(
                                dateKey = selectedDateKey,
                                storyId = selectedStoryId,
                                episodeId = selectedEpisodeId,
                                storyTitle = story?.title ?: "",
                                episodeTitle = ep?.title ?: "",
                                episodeNumber = ep?.episodeNumber ?: 1,
                                unlockTimeUtcEpochMs = unlockEpoch,
                                active = isActive
                            )
                            onSaveConfig(cfg)
                            saveSuccessMsg = "Daily Free Episode saved! Unlocks at 6:00 AM IST for $selectedDateKey."
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberWave, contentColor = Color.Black)
                    ) {
                        Text("SAVE DAILY FREE SCHEDULE", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOverviewTab(
    stories: List<Story>,
    episodesMap: Map<String, List<Episode>>,
    analytics: AdminAnalyticsData,
    activityLogs: List<AdminActivityLog>,
    onNavigateToTab: (Int) -> Unit,
    onOpenNewStory: () -> Unit
) {
    val totalEpisodes = episodesMap.values.sumOf { it.size }
    val publishedStories = stories.count { it.status == ContentStatus.PUBLISHED }
    val draftStories = stories.count { it.status == ContentStatus.DRAFT }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "DASHBOARD OVERVIEW", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CyanWave)
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard(title = "Total Stories", value = "${stories.size}", subtitle = "$publishedStories Published • $draftStories Draft", modifier = Modifier.weight(1f))
                AdminMetricCard(title = "Total Episodes", value = "$totalEpisodes", subtitle = "Ready for Streaming", modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard(title = "Total Listens", value = "1.48M", subtitle = "+12% this week", modifier = Modifier.weight(1f))
                AdminMetricCard(title = "Offline Downloads", value = "184.5K", subtitle = "AES-256 Vaulted", modifier = Modifier.weight(1f))
            }
        }

        item {
            Text(text = "QUICK ACTIONS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = AmberWave)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onOpenNewStory,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = Color.Black)
                ) {
                    Text("+ New Story", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onNavigateToTab(3) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberWave, contentColor = Color.Black)
                ) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload Episode", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Text(text = "RECENT ADMIN ACTIONS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (activityLogs.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
                    Text(text = "No recent admin activity recorded.", modifier = Modifier.padding(16.dp), color = TextMutedDark)
                }
            }
        } else {
            items(activityLogs.take(5)) { log ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = log.action, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "${log.targetType}: ${log.targetTitle}", style = MaterialTheme.typography.bodySmall, color = CyanWave)
                        }
                        Text(text = log.adminEmail.substringBefore("@"), style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMetricCard(title: String, value: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = CyanWave)
        }
    }
}

@Composable
fun AdminStoriesTab(
    stories: List<Story>,
    onEditStory: (Story) -> Unit,
    onDeleteStory: (String) -> Unit,
    onToggleStatus: (String, ContentStatus) -> Unit,
    onManageEpisodes: (Story) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf<ContentStatus?>(null) }

    val filtered = stories.filter { story ->
        val matchSearch = searchQuery.isBlank() || story.title.contains(searchQuery, true) || story.author.contains(searchQuery, true)
        val matchStatus = filterStatus == null || story.status == filterStatus
        matchSearch && matchStatus
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search catalog stories...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = if (filterStatus == null) CyanWave else DarkSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable { filterStatus = null }
                ) {
                    Text("All (${stories.size})", color = if (filterStatus == null) Color.Black else Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 12.sp)
                }
                Surface(
                    color = if (filterStatus == ContentStatus.PUBLISHED) AmberWave else DarkSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable { filterStatus = ContentStatus.PUBLISHED }
                ) {
                    Text("Published", color = if (filterStatus == ContentStatus.PUBLISHED) Color.Black else Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 12.sp)
                }
                Surface(
                    color = if (filterStatus == ContentStatus.DRAFT) Color.White else DarkSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable { filterStatus = ContentStatus.DRAFT }
                ) {
                    Text("Draft", color = if (filterStatus == ContentStatus.DRAFT) Color.Black else Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(filtered) { story ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = story.title, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                            Text(text = "${story.genre} • By ${story.author} • ${story.episodeCount} ep", style = MaterialTheme.typography.bodySmall, color = TextMutedDark)
                        }
                        Surface(
                            color = when (story.status) {
                                ContentStatus.PUBLISHED -> CyanWave
                                ContentStatus.DRAFT -> AmberWave
                                ContentStatus.ARCHIVED -> Color.Gray
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = story.status.name,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(onClick = { onManageEpisodes(story) }, shape = RoundedCornerShape(8.dp)) {
                            Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Episodes (${story.episodeCount})", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = { onEditStory(story) }, shape = RoundedCornerShape(8.dp)) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                val nextStatus = if (story.status == ContentStatus.PUBLISHED) ContentStatus.DRAFT else ContentStatus.PUBLISHED
                                onToggleStatus(story.id, nextStatus)
                            }
                        ) {
                            Icon(
                                imageVector = if (story.status == ContentStatus.PUBLISHED) Icons.Default.Pause else Icons.Default.Publish,
                                contentDescription = "Toggle publish",
                                tint = if (story.status == ContentStatus.PUBLISHED) AmberWave else CyanWave
                            )
                        }
                        IconButton(onClick = { onDeleteStory(story.id) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF43F5E))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminUploadAudioTab(
    stories: List<Story>,
    onAddEpisode: (String, Episode) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedStoryId by remember { mutableStateOf(stories.firstOrNull()?.id ?: "") }
    var episodeTitle by remember { mutableStateOf("") }
    var episodeNumberInput by remember { mutableStateOf("") }
    var isPremiumOption by remember { mutableStateOf(true) }
    var priceInrInput by remember { mutableStateOf("1") }
    var publishImmediately by remember { mutableStateOf(true) }

    // Selected file details
    var selectedAudioUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedFileSizeBytes by remember { mutableStateOf(0L) }
    var selectedFileFormat by remember { mutableStateOf("MP3") }
    var detectedDurationSec by remember { mutableIntStateOf(0) }

    // Upload state
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableFloatStateOf(0f) }
    var uploadJob by remember { mutableStateOf<Job?>(null) }
    var uploadedStoragePath by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Native Android file picker launcher for audio files
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedAudioUri = uri
            errorMessage = null
            successMessage = null
            uploadedStoragePath = null
            uploadProgress = 0f

            // Inspect metadata from ContentResolver
            try {
                var fileName = "audio_track"
                var fileSize = 0L
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: fileName
                        if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                    }
                }
                selectedFileName = fileName
                selectedFileSizeBytes = fileSize

                val ext = fileName.substringAfterLast('.', "mp3").uppercase()
                selectedFileFormat = ext

                // Auto-suggest episode title from file name if blank
                if (episodeTitle.isBlank()) {
                    val rawName = fileName.substringBeforeLast('.')
                    episodeTitle = rawName.replace('_', ' ').replace('-', ' ').replaceFirstChar { it.uppercase() }
                }

                // Auto-detect audio duration via MediaMetadataRetriever
                try {
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(context, uri)
                    val durationMsStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    val durationMs = durationMsStr?.toLongOrNull() ?: 0L
                    detectedDurationSec = if (durationMs > 0) (durationMs / 1000L).toInt() else 1800
                    retriever.release()
                } catch (_: Exception) {
                    detectedDurationSec = 1800 // default fallback ~30 mins
                }
            } catch (e: Exception) {
                errorMessage = "Could not read selected file: ${e.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    // Function to perform real binary upload to persistent app internal storage
    fun startUpload() {
        val uri = selectedAudioUri ?: run {
            errorMessage = "Please select an audio file first."
            return
        }

        val targetStory = stories.find { it.id == selectedStoryId }
        val targetEpNum = episodeNumberInput.toIntOrNull() ?: 1

        isUploading = true
        errorMessage = null
        successMessage = null
        uploadProgress = 0.05f

        uploadJob?.cancel()
        uploadJob = coroutineScope.launch {
            try {
                // Prepare secure storage directory
                val audioDir = File(context.filesDir, "uploaded_audio/$selectedStoryId")
                if (!audioDir.exists()) {
                    audioDir.mkdirs()
                }

                val safeExt = selectedFileName?.substringAfterLast('.', "mp3") ?: "mp3"
                val destFile = File(audioDir, "ep_${targetEpNum}_${System.currentTimeMillis()}.$safeExt")

                // Resumable/chunked binary stream copy with live progress calculation
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw IllegalStateException("Cannot open stream for selected audio file")

                val totalBytes = if (selectedFileSizeBytes > 0) selectedFileSizeBytes else 1024 * 1024L
                val outputStream = FileOutputStream(destFile)
                val buffer = ByteArray(64 * 1024) // 64 KB chunk size for smooth progress
                var bytesCopied = 0L

                inputStream.use { input ->
                    outputStream.use { output ->
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            if (!isActive) {
                                destFile.delete()
                                throw kotlinx.coroutines.CancellationException("Upload cancelled by Admin.")
                            }
                            output.write(buffer, 0, bytesRead)
                            bytesCopied += bytesRead
                            uploadProgress = (bytesCopied.toFloat() / totalBytes).coerceIn(0f, 0.98f)
                            delay(10) // Smooth UI progress rendering
                        }
                    }
                }

                uploadProgress = 1.0f
                uploadedStoragePath = destFile.absolutePath
                isUploading = false
                successMessage = "Audio file uploaded successfully! Ready to publish."
            } catch (e: kotlinx.coroutines.CancellationException) {
                isUploading = false
                uploadProgress = 0f
                errorMessage = "Upload was cancelled."
            } catch (e: Exception) {
                isUploading = false
                errorMessage = "Upload failed: ${e.localizedMessage ?: e.javaClass.simpleName}"
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_upload_audio_tab"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "MOBILE AUDIO UPLOADER & PUBLISHING",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AmberWave
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Direct Android phone binary upload (Internal Storage, Downloads, Google Drive). Supports MP3, M4A, AAC, WAV, OGG, FLAC.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMutedDark
                    )
                }
            }
        }

        // Story Selection
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "1. Select Target Story", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    stories.forEach { st ->
                        val isSelected = selectedStoryId == st.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedStoryId = st.id
                                    if (episodeNumberInput.isBlank()) {
                                        episodeNumberInput = (st.episodeCount + 1).toString()
                                    }
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = if (isSelected) CyanWave else TextMutedDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${st.title} (${st.episodeCount} eps)",
                                color = if (isSelected) Color.White else Color.Gray,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Native Android File Picker Trigger & File Card
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "2. Select Audio File from Device", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Clearly Visible "Upload Episode" / "Select Audio File" Button
                    Button(
                        onClick = {
                            // Native Android SAF picker supporting all audio types
                            audioPickerLauncher.launch(
                                arrayOf(
                                    "audio/*",
                                    "audio/mpeg",
                                    "audio/mp4",
                                    "audio/m4a",
                                    "audio/aac",
                                    "audio/wav",
                                    "audio/x-wav",
                                    "audio/ogg",
                                    "audio/flac"
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("admin_upload_episode_picker_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberWave, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedAudioUri == null) "Upload Episode (Choose Audio File)" else "Change Selected Audio File",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Selected File Info Card
                    if (selectedAudioUri != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedFileName ?: "Selected Audio File",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        maxLines = 1
                                    )
                                    Surface(color = CyanWave.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            text = selectedFileFormat,
                                            color = CyanWave,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                val sizeMb = "%.2f MB".format(selectedFileSizeBytes / (1024f * 1024f))
                                val durationText = "%02d:%02d".format(detectedDurationSec / 60, detectedDurationSec % 60)
                                Text(
                                    text = "Size: $sizeMb • Detected Duration: $durationText",
                                    fontSize = 12.sp,
                                    color = TextMutedDark
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Upload Progress Bar & Percentage
                                if (isUploading) {
                                    LinearProgressIndicator(
                                        progress = { uploadProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = CyanWave,
                                        trackColor = DarkBorder
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Uploading binary chunks... ${(uploadProgress * 100).toInt()}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyanWave
                                        )
                                        Button(
                                            onClick = {
                                                uploadJob?.cancel()
                                                isUploading = false
                                                errorMessage = "Upload cancelled."
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E), contentColor = Color.White),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Cancel", fontSize = 11.sp)
                                        }
                                    }
                                } else if (uploadedStoragePath == null) {
                                    Button(
                                        onClick = { startUpload() },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = Color.Black),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Start Audio Upload", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Real Error Display with Retry Action
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFFF43F5E).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = errorMessage!!,
                                    color = Color(0xFFF43F5E),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedButton(
                                    onClick = { startUpload() },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Retry Upload", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Success Feedback
                    if (successMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = successMessage!!,
                                    color = Color(0xFF10B981),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Episode Metadata & Publishing
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "3. Episode Details & Publishing", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = episodeNumberInput,
                            onValueChange = { episodeNumberInput = it },
                            label = { Text("Episode #") },
                            modifier = Modifier.width(100.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = episodeTitle,
                            onValueChange = { episodeTitle = it },
                            label = { Text("Episode Title") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Access Tier", color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = if (isPremiumOption) "Paid Episode (₹1)" else "Free Episode",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isPremiumOption) AmberWave else Color(0xFF10B981)
                            )
                        }
                        androidx.compose.material3.Switch(
                            checked = isPremiumOption,
                            onCheckedChange = { isPremiumOption = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Publish Immediately", color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = if (publishImmediately) "Live in app immediately" else "Saved as Draft",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedDark
                            )
                        }
                        androidx.compose.material3.Switch(
                            checked = publishImmediately,
                            onCheckedChange = { publishImmediately = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Final Publish Button
                    Button(
                        onClick = {
                            val epNum = episodeNumberInput.toIntOrNull() ?: 1
                            val finalAudioUrl = uploadedStoragePath ?: (selectedAudioUri?.toString() ?: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")

                            val newEp = Episode(
                                id = "ep_${selectedStoryId}_$epNum",
                                storyId = selectedStoryId,
                                episodeNumber = epNum,
                                title = episodeTitle.ifBlank { "Episode $epNum" },
                                description = "Official audio drama episode $epNum for JD WAVE audience.",
                                audioUrl = finalAudioUrl,
                                durationSec = if (detectedDurationSec > 0) detectedDurationSec else 1800,
                                status = if (publishImmediately) ContentStatus.PUBLISHED else ContentStatus.DRAFT,
                                isPremium = isPremiumOption,
                                priceInr = if (isPremiumOption) (priceInrInput.toIntOrNull() ?: 1) else 0
                            )

                            onAddEpisode(selectedStoryId, newEp)
                            successMessage = "Episode uploaded successfully. Episode #$epNum is now available in JD WAVE!"

                            // Reset form fields
                            selectedAudioUri = null
                            selectedFileName = null
                            uploadedStoragePath = null
                            episodeTitle = ""
                            episodeNumberInput = ""
                            uploadProgress = 0f
                        },
                        enabled = (selectedAudioUri != null || uploadedStoragePath != null) && !isUploading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("admin_publish_episode_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PUBLISH EPISODE TO APP", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAnalyticsTab(analytics: AdminAnalyticsData, stories: List<Story>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(text = "AUDIENCE & PLAYBACK ANALYTICS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CyanWave)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard(title = "Total Plays", value = "1,482,300", subtitle = "Streaming & Offline", modifier = Modifier.weight(1f))
                AdminMetricCard(title = "Unique Listeners", value = "349,210", subtitle = "Active this month", modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard(title = "Completed Episodes", value = "982,140", subtitle = "66.2% completion rate", modifier = Modifier.weight(1f))
                AdminMetricCard(title = "Avg Listening Time", value = "38 min", subtitle = "Per session", modifier = Modifier.weight(1f))
            }
        }
        item {
            Text(text = "TOP PERFORMING AUDIO STORIES", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = AmberWave)
        }
        items(stories.take(5)) { story ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = story.title, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = "${story.genre} • %.1f".format(story.rating), style = MaterialTheme.typography.bodySmall, color = CyanWave)
                    }
                    Text(text = "%.1fM Plays".format(story.totalListens / 1_000_000f), fontWeight = FontWeight.Bold, color = AmberWave)
                }
            }
        }
    }
}

@Composable
fun AdminStorageTab(analytics: AdminAnalyticsData, stories: List<Story>, episodesMap: Map<String, List<Episode>>) {
    val totalAudioFiles = episodesMap.values.sumOf { it.size }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(text = "STORAGE & ASSETS BREAKDOWN", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CyanWave)
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Cloud Audio Storage", fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "8,420 MB allocated across $totalAudioFiles audio master files", color = TextMutedDark, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(14.dp))
                LinearProgressIndicator(progress = { 0.42f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = CyanWave)
                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "Encrypted In-App Download Protection", fontWeight = FontWeight.Bold, color = AmberWave)
                Text(text = "Clients download into AES-256 sandboxed vault files (.jdwave_enc) with zero public filesystem exposure.", color = TextMutedDark, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun AdminActivityLogsTab(logs: List<AdminActivityLog>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(text = "AUDIT ACTIVITY LOGS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CyanWave)
        }
        if (logs.isEmpty()) {
            item {
                Text(text = "No logs recorded yet.", color = TextMutedDark)
            }
        } else {
            items(logs) { log ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurface)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = log.action, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = log.adminEmail.substringBefore("@"), color = AmberWave, style = MaterialTheme.typography.labelSmall)
                        }
                        Text(text = "${log.targetType}: ${log.targetTitle}", style = MaterialTheme.typography.bodySmall, color = CyanWave)
                    }
                }
            }
        }
    }
}

@Composable
fun StoryEditorDialog(
    story: Story?,
    onDismiss: () -> Unit,
    onSave: (Story) -> Unit
) {
    var title by remember { mutableStateOf(story?.title ?: "") }
    var description by remember { mutableStateOf(story?.description ?: "") }
    var author by remember { mutableStateOf(story?.author ?: "") }
    var genre by remember { mutableStateOf(story?.genre ?: "Action & Mythology") }
    var coverUrl by remember { mutableStateOf(story?.coverUrl ?: "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=600&auto=format&fit=crop&q=80") }
    var status by remember { mutableStateOf(story?.status ?: ContentStatus.PUBLISHED) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (story == null) "Create New Story" else "Edit Story", color = Color.White) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Story Title") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Author / Narrator") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = genre, onValueChange = { genre = it }, label = { Text("Genre") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), maxLines = 4)
                }
                item {
                    OutlinedTextField(value = coverUrl, onValueChange = { coverUrl = it }, label = { Text("Cover Image URL") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Status:", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                        Surface(
                            color = if (status == ContentStatus.PUBLISHED) CyanWave else DarkSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { status = ContentStatus.PUBLISHED }
                        ) {
                            Text("Published", color = if (status == ContentStatus.PUBLISHED) Color.Black else Color.White, modifier = Modifier.padding(6.dp), fontSize = 11.sp)
                        }
                        Surface(
                            color = if (status == ContentStatus.DRAFT) AmberWave else DarkSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { status = ContentStatus.DRAFT }
                        ) {
                            Text("Draft", color = if (status == ContentStatus.DRAFT) Color.Black else Color.White, modifier = Modifier.padding(6.dp), fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalStory = story?.copy(
                        title = title,
                        description = description,
                        author = author,
                        genre = genre,
                        coverUrl = coverUrl,
                        status = status
                    ) ?: Story(
                        id = "story_${UUID.randomUUID().toString().take(6)}",
                        title = title,
                        description = description,
                        author = author,
                        genre = genre,
                        coverUrl = coverUrl,
                        status = status
                    )
                    onSave(finalStory)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = Color.Black)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        },
        containerColor = DarkSurface
    )
}

@Composable
fun EpisodeManagerDialog(
    story: Story,
    episodes: List<Episode>,
    onDismiss: () -> Unit,
    onAddEpisode: (Episode) -> Unit,
    onDeleteEpisode: (String) -> Unit,
    onReorder: (List<Episode>) -> Unit,
    onSetEpisodePremium: (episodeId: String, isPremium: Boolean, price: Int) -> Unit = { _, _, _ -> }
) {
    var showAddEp by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newEpNumber by remember { mutableStateOf("${episodes.size + 1}") }
    var newIsPremium by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Manage Episodes: ${story.title}", color = Color.White, maxLines = 1) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Total: ${episodes.size} episodes", color = TextMutedDark, style = MaterialTheme.typography.bodySmall)
                    Button(
                        onClick = { showAddEp = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ Episode", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (showAddEp) {
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            OutlinedTextField(value = newEpNumber, onValueChange = { newEpNumber = it }, label = { Text("Episode #") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(value = newTitle, onValueChange = { newTitle = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Premium (₹1):", color = Color.White, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                androidx.compose.material3.Switch(
                                    checked = newIsPremium,
                                    onCheckedChange = { newIsPremium = it }
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(onClick = { showAddEp = false }) { Text("Cancel") }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = {
                                        val epNum = newEpNumber.toIntOrNull() ?: (episodes.size + 1)
                                        val ep = Episode(
                                            id = "ep_${story.id}_$epNum",
                                            storyId = story.id,
                                            episodeNumber = epNum,
                                            title = newTitle.ifBlank { "Episode $epNum" },
                                            description = "Story episode $epNum",
                                            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                                            durationSec = 2100,
                                            status = ContentStatus.PUBLISHED,
                                            isPremium = newIsPremium,
                                            priceInr = if (newIsPremium) 1 else 0
                                        )
                                        onAddEpisode(ep)
                                        showAddEp = false
                                        newTitle = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = Color.Black)
                                ) {
                                    Text("Add")
                                }
                            }
                        }
                    }
                }
                LazyColumn(modifier = Modifier.height(280.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(episodes) { ep ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Ep ${ep.episodeNumber}: ${ep.title}", color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                                    Text(text = "${ep.formattedDuration()} • ${if (ep.isPremium) "₹${ep.priceInr} Premium" else "Free"}", color = if (ep.isPremium) AmberWave else Color(0xFF10B981), style = MaterialTheme.typography.labelSmall)
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            onSetEpisodePremium(ep.id, !ep.isPremium, 1)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (ep.isPremium) Icons.Default.Lock else Icons.Default.LockOpen,
                                            contentDescription = "Toggle Premium",
                                            tint = if (ep.isPremium) AmberWave else Color(0xFF10B981),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            val index = episodes.indexOf(ep)
                                            if (index > 0) {
                                                val mutable = episodes.toMutableList()
                                                val temp = mutable[index]
                                                mutable[index] = mutable[index - 1]
                                                mutable[index - 1] = temp
                                                onReorder(mutable)
                                            }
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Move up", tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            val index = episodes.indexOf(ep)
                                            if (index < episodes.size - 1) {
                                                val mutable = episodes.toMutableList()
                                                val temp = mutable[index]
                                                mutable[index] = mutable[index + 1]
                                                mutable[index + 1] = temp
                                                onReorder(mutable)
                                            }
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Move down", tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { onDeleteEpisode(ep.id) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF43F5E), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = Color.Black)) {
                Text("Done")
            }
        },
        containerColor = DarkSurface
    )
}

@Composable
fun AdminTelegramRequestsTab(
    requests: List<TelegramUnlockRequest>,
    onApprove: (requestId: String) -> Unit,
    onReject: (requestId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var filterStatus by remember { mutableStateOf<TelegramRequestStatus?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = requests.filter { req ->
        (filterStatus == null || req.status == filterStatus) &&
                (searchQuery.isBlank() ||
                        req.requestId.contains(searchQuery, ignoreCase = true) ||
                        req.username.contains(searchQuery, ignoreCase = true) ||
                        req.storyTitle.contains(searchQuery, ignoreCase = true) ||
                        req.episodeTitle.contains(searchQuery, ignoreCase = true))
    }

    val pendingCount = requests.count { it.status == TelegramRequestStatus.PENDING }
    val approvedCount = requests.count { it.status == TelegramRequestStatus.APPROVED }
    val rejectedCount = requests.count { it.status == TelegramRequestStatus.REJECTED }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_telegram_requests_tab"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CyanWave.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = CyanWave,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Telegram Manual Episode Unlock System",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Entitlement pipeline for Telegram-based user requests (@JDWaveSupport)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMutedDark
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricChipCard(title = "Pending Requests", value = "$pendingCount", color = AmberWave, modifier = Modifier.weight(1f))
                MetricChipCard(title = "Approved Unlocks", value = "$approvedCount", color = Color(0xFF10B981), modifier = Modifier.weight(1f))
                MetricChipCard(title = "Rejected", value = "$rejectedCount", color = Color(0xFFF43F5E), modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search code, user, or story...", fontSize = 12.sp, color = TextMutedDark) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
                FilterStatusChip(label = "All (${requests.size})", isSelected = filterStatus == null, onClick = { filterStatus = null })
                FilterStatusChip(label = "Pending", isSelected = filterStatus == TelegramRequestStatus.PENDING, onClick = { filterStatus = TelegramRequestStatus.PENDING })
                FilterStatusChip(label = "Approved", isSelected = filterStatus == TelegramRequestStatus.APPROVED, onClick = { filterStatus = TelegramRequestStatus.APPROVED })
            }
        }

        if (filtered.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(text = if (requests.isEmpty()) "No Telegram unlock requests found." else "No requests match the current filter.", color = TextMutedDark)
                    }
                }
            }
        } else {
            items(filtered) { req ->
                TelegramRequestAdminCard(
                    request = req,
                    onApprove = { onApprove(req.requestId) },
                    onReject = { onReject(req.requestId) }
                )
            }
        }
    }
}

@Composable
private fun MetricChipCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(10.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontSize = 11.sp, color = TextMutedDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun FilterStatusChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) CyanWave else DarkSurface,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.Black else Color.White,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun TelegramRequestAdminCard(
    request: TelegramUnlockRequest,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), shape = RoundedCornerShape(12.dp), modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(color = AmberWave.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                    Text(text = request.requestId, fontSize = 13.sp, fontWeight = FontWeight.Black, color = AmberWave, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                val statusColor = when (request.status) {
                    TelegramRequestStatus.PENDING -> AmberWave
                    TelegramRequestStatus.APPROVED -> Color(0xFF10B981)
                    TelegramRequestStatus.REJECTED -> Color(0xFFF43F5E)
                    TelegramRequestStatus.EXPIRED -> TextMutedDark
                }
                Surface(color = statusColor.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                    Text(text = request.status.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Story: ${request.storyTitle}", fontWeight = FontWeight.SemiBold, color = Color.White)
            Text(text = "Episode: Ep ${request.episodeNumber} • ${request.episodeTitle}", color = CyanWave, fontSize = 12.sp)
            Text(text = "User: ${request.username} (${request.userId})", color = TextMutedDark, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(12.dp))
            if (request.status == TelegramRequestStatus.PENDING) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981), contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Approve & Unlock", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF43F5E)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reject", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
