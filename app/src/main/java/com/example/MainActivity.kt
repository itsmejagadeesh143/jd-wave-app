package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyFreeEpisodeConfig
import com.example.model.Episode
import com.example.model.Story
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.admin.AdminLoginScreen
import com.example.ui.components.FullPlayerModal
import com.example.ui.components.JDWaveTopBar
import com.example.ui.components.MiniPlayer
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.MyEpisodesScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SignInScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StoryDetailsScreen
import com.example.ui.theme.AmberWave
import com.example.ui.theme.CyanWave
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.credentials.CredentialManager
import com.example.auth.GoogleAuthService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = JDWaveApplication.instance
            val repository = app.repository
            val player = app.player
            val downloadVault = app.downloadVault

            val themeMode by repository.themeMode.collectAsState()
            val stories by repository.stories.collectAsState()
            val episodesMap by repository.episodes.collectAsState()
            val favorites by repository.favorites.collectAsState()
            val bookmarks by repository.bookmarks.collectAsState()
            val history by repository.history.collectAsState()
            val playbackProgressMap by repository.playbackProgress.collectAsState()
            val downloadsMap by downloadVault.downloadsFlow.collectAsState()

            val audioDownloadService = app.audioDownloadService
            val roomDownloads by audioDownloadService.allDownloadedTracks.collectAsState(initial = emptyList())
            val playerState by player.playerState.collectAsState()

            val activeDownloadsList = if (roomDownloads.isNotEmpty()) {
                roomDownloads.map { entity ->
                    com.example.model.DownloadRecord(
                        id = "dl_${entity.episodeId}",
                        storyId = entity.storyId,
                        episodeId = entity.episodeId,
                        storyTitle = entity.storyTitle,
                        episodeTitle = entity.episodeTitle,
                        episodeNumber = entity.episodeNumber,
                        coverUrl = entity.coverUrl,
                        durationSec = entity.durationSec,
                        encryptedFileReference = entity.encryptedFilePath,
                        status = when (entity.downloadStatus) {
                            "DOWNLOADED" -> com.example.model.DownloadStatus.DOWNLOADED
                            "PAUSED" -> com.example.model.DownloadStatus.PAUSED
                            "FAILED" -> com.example.model.DownloadStatus.FAILED
                            else -> com.example.model.DownloadStatus.DOWNLOADING
                        },
                        progress = entity.progressPercent,
                        fileSizeMb = entity.fileSizeMb,
                        downloadedAt = entity.downloadedAt
                    )
                }
            } else {
                downloadsMap.values.toList()
            }

            val defaultSpeed by repository.defaultSpeed.collectAsState()
            val autoNext by repository.autoNext.collectAsState()
            val wifiOnly by repository.wifiOnlyDownloads.collectAsState()
            val voiceBoost by repository.voiceBoost.collectAsState()
            val silenceSkip by repository.silenceSkip.collectAsState()
            val smartVolume by repository.smartVolume.collectAsState()
            val gestureControls by repository.gestureControls.collectAsState()

            val currentAdminUser by repository.currentAdminUser.collectAsState()
            val adminLogs by repository.adminActivityLogs.collectAsState()
            val adminAnalytics by repository.adminAnalytics.collectAsState()
            val telegramRequests by repository.telegramUnlockRequests.collectAsState()
            val userEntitledEpisodes by repository.userEntitledEpisodes.collectAsState()
            val dailyFreeConfig by repository.dailyFreeConfig.collectAsState()

            val firestoreRepository = app.firestoreRepository

            var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
            // Guest mode: defaults to true so any user can instantly explore without friction
            var isGuestSession by remember { mutableStateOf(true) }
            var customUser by remember { mutableStateOf<Pair<String, String>?>(null) }
            var promptSignInSheet by remember { mutableStateOf(false) }

            DisposableEffect(Unit) {
                val listener = FirebaseAuth.AuthStateListener { auth ->
                    currentUser = auth.currentUser
                    if (auth.currentUser != null) {
                        isGuestSession = false
                        firestoreRepository.syncUserProfile(auth.currentUser!!)
                    }
                }
                Firebase.auth.addAuthStateListener(listener)
                onDispose {
                    Firebase.auth.removeAuthStateListener(listener)
                }
            }

            LaunchedEffect(currentUser?.uid) {
                val user = currentUser
                if (user != null) {
                    launch {
                        firestoreRepository.observeFavorites().collect { favList ->
                            favList.forEach { storyId ->
                                if (!repository.isFavorite(storyId)) {
                                    repository.toggleFavorite(storyId)
                                }
                            }
                        }
                    }
                    launch {
                        firestoreRepository.observeBookmarks().collect { bmList ->
                            bmList.forEach { bm ->
                                repository.addBookmark(
                                    com.example.model.Bookmark(
                                        id = bm.storyId + "_" + bm.episodeId + "_" + bm.positionMs,
                                        userId = currentUser?.uid ?: "guest_user",
                                        storyId = bm.storyId,
                                        episodeId = bm.episodeId,
                                        episodeNumber = 1,
                                        storyTitle = "",
                                        episodeTitle = "",
                                        positionSec = (bm.positionMs / 1000L).toInt(),
                                        note = bm.note
                                    )
                                )
                            }
                        }
                    }
                }
            }

            MyApplicationTheme(themeMode = themeMode) {
                val isGuest = (currentUser == null && customUser == null)
                val activeUserName = currentUser?.displayName ?: customUser?.first ?: "Guest Listener"
                val activeUserEmail = currentUser?.email ?: customUser?.second ?: "Guest Mode • Sign in to save library"

                if (promptSignInSheet && isGuest) {
                    SignInScreen(
                        onAuthSuccess = {
                            currentUser = Firebase.auth.currentUser
                            currentUser?.let { firestoreRepository.syncUserProfile(it) }
                            promptSignInSheet = false
                            isGuestSession = false
                        },
                        onEmailAuthSuccess = { name, email ->
                            customUser = Pair(name, email)
                            promptSignInSheet = false
                            isGuestSession = false
                        },
                        onContinueAsGuest = {
                            promptSignInSheet = false
                            isGuestSession = true
                        }
                    )
                } else {
                    JDWaveAppRoot(
                        userName = activeUserName,
                        userEmail = activeUserEmail,
                        isGuestUser = isGuest,
                        dailyFreeConfig = dailyFreeConfig,
                        getEpisodeAccessStatus = { ep -> repository.getEpisodeAccessStatus(ep) },
                        onPromptGuestToSignIn = {
                            promptSignInSheet = true
                        },
                        onSignOut = {
                            if (currentUser != null) {
                                val cm = CredentialManager.create(this@MainActivity)
                                GoogleAuthService.signOut(
                                    context = this@MainActivity,
                                    credentialManager = cm,
                                    onSignOutComplete = {
                                        currentUser = null
                                        customUser = null
                                        isGuestSession = true
                                    },
                                    scope = lifecycleScope
                                )
                            } else {
                                customUser = null
                                isGuestSession = true
                            }
                        },
                        telegramRequests = telegramRequests,
                        userEntitledEpisodes = userEntitledEpisodes,
                        onConfigureDailyFree = { cfg -> repository.setDailyFreeEpisodeConfig(cfg) },
                        onApproveTelegramRequest = { reqId ->
                            repository.approveTelegramUnlockRequest(reqId, currentAdminUser?.email ?: "admin@jdwave.app")
                        },
                        onRejectTelegramRequest = { reqId ->
                            repository.rejectTelegramUnlockRequest(reqId, currentAdminUser?.email ?: "admin@jdwave.app")
                        },
                        onUnlockDirectInr = { ep ->
                            repository.unlockEpisode(
                                userId = currentUser?.uid ?: "user_default",
                                storyId = ep.storyId,
                                episodeId = ep.id,
                                source = "upi_inr_1"
                            )
                        },
                        onRequestTelegramUnlock = { ep, onCodeGenerated ->
                            val req = repository.createTelegramUnlockRequest(
                                userId = currentUser?.uid ?: "user_default",
                                username = activeUserName,
                                storyId = ep.storyId,
                                storyTitle = stories.firstOrNull { it.id == ep.storyId }?.title ?: "Telugu Audio Drama",
                                episodeId = ep.id,
                                episodeTitle = ep.title,
                                episodeNumber = ep.episodeNumber,
                                price = ep.priceInr
                            )
                            onCodeGenerated(req.requestId)
                        },
                        stories = stories,
                        episodesMap = episodesMap,
                        favorites = favorites,
                        bookmarks = bookmarks,
                        history = history,
                        playbackProgressMap = playbackProgressMap,
                        downloads = activeDownloadsList,
                        playerState = playerState,
                        defaultSpeed = defaultSpeed,
                        autoNext = autoNext,
                        wifiOnly = wifiOnly,
                        voiceBoost = voiceBoost,
                        silenceSkip = silenceSkip,
                        smartVolume = smartVolume,
                        gestureControls = gestureControls,
                        currentAdminUser = currentAdminUser,
                        adminLogs = adminLogs,
                        adminAnalytics = adminAnalytics,
                        downloadVault = downloadVault,
                        onPlayEpisode = { story, episode ->
                            lifecycleScope.launch {
                                val startPos = repository.getInitialPlaybackPosition(episode.id)
                                val allEps = episodesMap[story.id] ?: emptyList()
                                player.playEpisode(
                                    story = story,
                                    episode = episode,
                                    startFromPositionSec = startPos,
                                    isFavorite = repository.isFavorite(story.id),
                                    allEpisodesInStory = allEps
                                )
                            }
                        },
                        onTogglePlayPause = { player.togglePlayPause() },
                        onSeekFraction = { player.seekToFraction(it) },
                        onRewind15 = { player.rewind15() },
                        onForward30 = { player.forward30() },
                        onPreviousEpisode = { player.playPreviousEpisode() },
                        onNextEpisode = { player.playNextEpisode() },
                        onSetSpeed = { player.setPlaybackSpeed(it) },
                        onSetSleepTimer = { player.setSleepTimerMinutes(it) },
                        onCancelSleepTimer = { player.cancelSleepTimer() },
                        onToggleFavorite = { player.toggleFavorite() },
                        onToggleDownload = { player.toggleDownload() },
                        onDownloadEpisode = { story, episode ->
                            lifecycleScope.launch {
                                if (downloadVault.isEpisodeDownloaded(episode.id)) {
                                    audioDownloadService.deleteDownload(episode.id)
                                    downloadVault.deleteDownload(episode.id)
                                } else {
                                    audioDownloadService.startDownload(story, episode)
                                    downloadVault.startDownload(story, episode)
                                }
                            }
                        },
                        onAddBookmark = { player.addBookmark(it) },
                        onDeleteBookmark = { repository.deleteBookmark(it) },
                        onDeleteDownload = {
                            audioDownloadService.deleteDownload(it)
                            downloadVault.deleteDownload(it)
                        },
                        onClearHistory = { repository.clearHistory() },
                        onClearQueue = { player.clearQueue() },
                        onRemoveFromQueue = { player.removeFromQueue(it) },
                        onToggleCarMode = { player.setCarMode(!playerState.isCarMode) },
                        onCancelAutoNext = { player.cancelAutoNextCountdown() },
                        onPlayNextNow = { player.playNextEpisode() },
                        onSetTheme = { repository.setThemeMode(it) },
                        onSetDefaultSpeed = { repository.setDefaultSpeed(it) },
                        onToggleAutoNext = {
                            repository.setAutoNext(it)
                            player.setAutoNextEnabled(it)
                        },
                        onToggleWifiOnly = { repository.setWifiOnlyDownloads(it) },
                        onToggleVoiceBoost = { repository.setVoiceBoost(it) },
                        onToggleSilenceSkip = { repository.setSilenceSkip(it) },
                        onToggleSmartVolume = { repository.setSmartVolume(it) },
                        onToggleGestures = { repository.setGestureControls(it) },
                        onLoginAdmin = { email, role -> repository.loginAdmin(email, role) },
                        onLogoutAdmin = { repository.logoutAdmin() },
                        onCreateStory = { repository.createStory(it) },
                        onUpdateStory = { repository.updateStory(it) },
                        onDeleteStory = { repository.deleteStory(it) },
                        onSetStoryStatus = { id, status -> repository.setStoryStatus(id, status) },
                        onAddAdminEpisode = { storyId, ep -> repository.addEpisode(storyId, ep) },
                        onDeleteAdminEpisode = { storyId, epId -> repository.deleteEpisode(storyId, epId) },
                        onReorderAdminEpisodes = { storyId, list -> repository.reorderEpisodes(storyId, list) },
                        onSetEpisodePremium = { storyId, epId, isPrem, pr -> repository.setEpisodePremium(storyId, epId, isPrem, pr) }
                    )
                }
            }
        }
    }
}

@Composable
fun JDWaveAppRoot(
    userName: String = "JD WAVE Member",
    userEmail: String = "Synced with Firebase",
    isGuestUser: Boolean = false,
    dailyFreeConfig: DailyFreeEpisodeConfig? = null,
    getEpisodeAccessStatus: (Episode) -> com.example.data.JDWaveRepository.EpisodeAccessStatus,
    onPromptGuestToSignIn: () -> Unit = {},
    onSignOut: () -> Unit = {},
    stories: List<Story>,
    episodesMap: Map<String, List<Episode>>,
    favorites: Set<String>,
    bookmarks: List<com.example.model.Bookmark>,
    history: List<com.example.model.ListeningHistoryItem>,
    playbackProgressMap: Map<String, com.example.model.PlaybackProgress>,
    downloads: List<com.example.model.DownloadRecord>,
    playerState: com.example.player.PlayerState,
    defaultSpeed: Float,
    autoNext: Boolean,
    wifiOnly: Boolean,
    voiceBoost: Boolean,
    silenceSkip: Boolean,
    smartVolume: Boolean,
    gestureControls: Boolean,
    currentAdminUser: com.example.model.AdminUser?,
    adminLogs: List<com.example.model.AdminActivityLog>,
    adminAnalytics: com.example.model.AdminAnalyticsData,
    telegramRequests: List<com.example.model.TelegramUnlockRequest> = emptyList(),
    userEntitledEpisodes: Set<String> = emptySet(),
    onConfigureDailyFree: (DailyFreeEpisodeConfig) -> Unit = {},
    onApproveTelegramRequest: (String) -> Unit = {},
    onRejectTelegramRequest: (String) -> Unit = {},
    onUnlockDirectInr: (Episode) -> Unit = {},
    onRequestTelegramUnlock: (Episode, onCodeGenerated: (String) -> Unit) -> Unit = { _, _ -> },
    downloadVault: com.example.storage.DownloadVault,
    onPlayEpisode: (Story, Episode) -> Unit,
    onTogglePlayPause: () -> Unit,
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
    onDownloadEpisode: (Story, Episode) -> Unit = { _, _ -> },
    onAddBookmark: (String) -> Unit,
    onDeleteBookmark: (String) -> Unit,
    onDeleteDownload: (String) -> Unit,
    onClearHistory: () -> Unit,
    onClearQueue: () -> Unit,
    onRemoveFromQueue: (String) -> Unit,
    onToggleCarMode: () -> Unit,
    onCancelAutoNext: () -> Unit,
    onPlayNextNow: () -> Unit,
    onSetTheme: (com.example.ui.theme.AppThemeMode) -> Unit,
    onSetDefaultSpeed: (Float) -> Unit,
    onToggleAutoNext: (Boolean) -> Unit,
    onToggleWifiOnly: (Boolean) -> Unit,
    onToggleVoiceBoost: (Boolean) -> Unit,
    onToggleSilenceSkip: (Boolean) -> Unit,
    onToggleSmartVolume: (Boolean) -> Unit,
    onToggleGestures: (Boolean) -> Unit,
    onLoginAdmin: (String, com.example.model.AdminRole) -> Boolean,
    onLogoutAdmin: () -> Unit,
    onCreateStory: (Story) -> Unit,
    onUpdateStory: (Story) -> Unit,
    onDeleteStory: (String) -> Unit,
    onSetStoryStatus: (String, com.example.model.ContentStatus) -> Unit,
    onAddAdminEpisode: (String, Episode) -> Unit,
    onDeleteAdminEpisode: (String, String) -> Unit,
    onReorderAdminEpisodes: (String, List<Episode>) -> Unit,
    onSetEpisodePremium: (storyId: String, episodeId: String, isPremium: Boolean, price: Int) -> Unit = { _, _, _, _ -> }
) {
    var showSplash by remember { mutableStateOf(true) }
    var selectedBottomTab by remember { mutableIntStateOf(0) }
    var selectedStoryDetail by remember { mutableStateOf<Story?>(null) }
    var isSearchOpen by remember { mutableStateOf(false) }
    var isFullPlayerOpen by remember { mutableStateOf(false) }
    var isAdminPortalOpen by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var exploreInitialCategory by remember { mutableStateOf<String?>(null) }

    if (showSplash) {
        SplashScreen(onTimeout = { showSplash = false })
        return
    }

    // Comprehensive Back Navigation Handler
    BackHandler(
        enabled = isFullPlayerOpen || isSearchOpen || selectedStoryDetail != null || isAdminPortalOpen || selectedBottomTab != 0
    ) {
        when {
            isFullPlayerOpen -> isFullPlayerOpen = false
            isSearchOpen -> isSearchOpen = false
            isAdminPortalOpen -> isAdminPortalOpen = false
            selectedStoryDetail != null -> selectedStoryDetail = null
            selectedBottomTab != 0 -> selectedBottomTab = 0
        }
    }

    // Admin Portal Mode
    if (isAdminPortalOpen) {
        if (currentAdminUser == null) {
            AdminLoginScreen(
                onLoginSuccess = { email, role ->
                    onLoginAdmin(email, role)
                },
                onBackToApp = { isAdminPortalOpen = false }
            )
        } else {
            AdminDashboardScreen(
                adminUser = currentAdminUser,
                stories = stories,
                episodesMap = episodesMap,
                activityLogs = adminLogs,
                analytics = adminAnalytics,
                dailyFreeConfig = dailyFreeConfig,
                onConfigureDailyFree = onConfigureDailyFree,
                telegramRequests = telegramRequests,
                onApproveTelegramRequest = onApproveTelegramRequest,
                onRejectTelegramRequest = onRejectTelegramRequest,
                onCreateStory = onCreateStory,
                onUpdateStory = onUpdateStory,
                onDeleteStory = onDeleteStory,
                onSetStoryStatus = onSetStoryStatus,
                onAddEpisode = onAddAdminEpisode,
                onDeleteEpisode = onDeleteAdminEpisode,
                onReorderEpisodes = onReorderAdminEpisodes,
                onSetEpisodePremium = onSetEpisodePremium,
                onLogout = onLogoutAdmin,
                onBackToPublicApp = { isAdminPortalOpen = false }
            )
        }
        return
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = { Text("JD WAVE Notifications", color = Color.White) },
            text = {
                Text(
                    "You're all caught up! Daily free episode unlocks every morning at 6:00 AM IST. Turn on alerts to never miss an update.",
                    color = Color(0xFFCBD5E1)
                )
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = Color.Black)
                ) {
                    Text("OK")
                }
            },
            containerColor = DarkSurface
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = DeepObsidian,
            topBar = {
                if (selectedStoryDetail == null && !isSearchOpen) {
                    JDWaveTopBar(
                        onSearchClick = { isSearchOpen = true },
                        onNotificationClick = { showNotificationsDialog = true },
                        onAdminClick = { isAdminPortalOpen = true },
                        isAdminLoggedIn = currentAdminUser != null
                    )
                }
            },
            bottomBar = {
                Column {
                    if (playerState.currentEpisode != null && !isFullPlayerOpen) {
                        MiniPlayer(
                            playerState = playerState,
                            onOpenFullPlayer = { isFullPlayerOpen = true },
                            onPlayPause = onTogglePlayPause,
                            onNext = onNextEpisode
                        )
                    }

                    NavigationBar(
                        containerColor = DarkSurface,
                        contentColor = Color.White,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = selectedBottomTab == 0 && selectedStoryDetail == null && !isSearchOpen,
                            onClick = {
                                selectedBottomTab = 0
                                selectedStoryDetail = null
                                isSearchOpen = false
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selectedBottomTab == 0) Icons.Default.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("Home", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CyanWave,
                                selectedTextColor = CyanWave,
                                indicatorColor = Color.Transparent
                            )
                        )
                        NavigationBarItem(
                            selected = selectedBottomTab == 1 && selectedStoryDetail == null && !isSearchOpen,
                            onClick = {
                                selectedBottomTab = 1
                                selectedStoryDetail = null
                                isSearchOpen = false
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selectedBottomTab == 1) Icons.Default.Explore else Icons.Outlined.Explore,
                                    contentDescription = "Explore"
                                )
                            },
                            label = { Text("Explore", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CyanWave,
                                selectedTextColor = CyanWave,
                                indicatorColor = Color.Transparent
                            )
                        )
                        NavigationBarItem(
                            selected = selectedBottomTab == 2 && selectedStoryDetail == null && !isSearchOpen,
                            onClick = {
                                selectedBottomTab = 2
                                selectedStoryDetail = null
                                isSearchOpen = false
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selectedBottomTab == 2) Icons.Default.Headphones else Icons.Outlined.Headphones,
                                    contentDescription = "My Episodes"
                                )
                            },
                            label = { Text("My Episodes", fontSize = 10.sp, maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CyanWave,
                                selectedTextColor = CyanWave,
                                indicatorColor = Color.Transparent
                            )
                        )
                        NavigationBarItem(
                            selected = selectedBottomTab == 3 && selectedStoryDetail == null && !isSearchOpen,
                            onClick = {
                                selectedBottomTab = 3
                                selectedStoryDetail = null
                                isSearchOpen = false
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selectedBottomTab == 3) Icons.Default.VideoLibrary else Icons.Outlined.VideoLibrary,
                                    contentDescription = "Library"
                                )
                            },
                            label = { Text("Library", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CyanWave,
                                selectedTextColor = CyanWave,
                                indicatorColor = Color.Transparent
                            )
                        )
                        NavigationBarItem(
                            selected = selectedBottomTab == 4 && selectedStoryDetail == null && !isSearchOpen,
                            onClick = {
                                selectedBottomTab = 4
                                selectedStoryDetail = null
                                isSearchOpen = false
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selectedBottomTab == 4) Icons.Default.Person else Icons.Outlined.Person,
                                    contentDescription = "Profile"
                                )
                            },
                            label = { Text("Profile", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CyanWave,
                                selectedTextColor = CyanWave,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    isSearchOpen -> {
                        SearchScreen(
                            stories = stories,
                            onBack = { isSearchOpen = false },
                            onStoryClick = { story ->
                                selectedStoryDetail = story
                                isSearchOpen = false
                            }
                        )
                    }
                    selectedStoryDetail != null -> {
                        val story = selectedStoryDetail!!
                        val epList = episodesMap[story.id] ?: emptyList()
                        val recs = stories.filterNot { it.id == story.id }.take(5)
                        StoryDetailsScreen(
                            story = story,
                            episodes = epList,
                            recommendedStories = recs,
                            isFavorite = favorites.contains(story.id),
                            activeEpisodeId = playerState.currentEpisode?.id,
                            isPlaying = playerState.isPlaying,
                            playbackProgressMap = playbackProgressMap,
                            isDownloaded = { epId -> downloadVault.isEpisodeDownloaded(epId) },
                            getEpisodeAccessStatus = getEpisodeAccessStatus,
                            isGuestUser = isGuestUser,
                            onBack = { selectedStoryDetail = null },
                            onPlayEpisode = { ep ->
                                onPlayEpisode(story, ep)
                                isFullPlayerOpen = true
                            },
                            onToggleFavorite = onToggleFavorite,
                            onToggleDownload = { ep -> onDownloadEpisode(story, ep) },
                            onUnlockDirectInr = onUnlockDirectInr,
                            onRequestTelegramUnlock = onRequestTelegramUnlock,
                            onPromptGuestToSignIn = onPromptGuestToSignIn,
                            onRecommendedClick = { selectedStoryDetail = it }
                        )
                    }
                    selectedBottomTab == 0 -> {
                        HomeScreen(
                            stories = stories,
                            history = history,
                            dailyFreeConfig = dailyFreeConfig,
                            onStoryClick = { selectedStoryDetail = it },
                            onPlayHistoryItem = { hist ->
                                onPlayEpisode(hist.story, hist.episode)
                                isFullPlayerOpen = true
                            },
                            onPlayDailyFree = { storyId, epId ->
                                val targetStory = stories.find { it.id == storyId }
                                val targetEp = (episodesMap[storyId] ?: emptyList()).find { it.id == epId }
                                if (targetStory != null && targetEp != null) {
                                    onPlayEpisode(targetStory, targetEp)
                                    isFullPlayerOpen = true
                                }
                            },
                            onCategoryClick = { cat ->
                                exploreInitialCategory = cat
                                selectedBottomTab = 1
                            }
                        )
                    }
                    selectedBottomTab == 1 -> {
                        ExploreScreen(
                            stories = stories,
                            initialCategory = exploreInitialCategory,
                            onStoryClick = { selectedStoryDetail = it }
                        )
                    }
                    selectedBottomTab == 2 -> {
                        MyEpisodesScreen(
                            stories = stories,
                            episodesMap = episodesMap,
                            userEntitledEpisodes = userEntitledEpisodes,
                            history = history,
                            onPlayEpisode = { st, ep ->
                                onPlayEpisode(st, ep)
                                isFullPlayerOpen = true
                            },
                            onBrowseCatalog = {
                                selectedBottomTab = 1
                            }
                        )
                    }
                    selectedBottomTab == 3 -> {
                        LibraryScreen(
                            stories = stories,
                            favoriteStoryIds = favorites,
                            history = history,
                            bookmarks = bookmarks,
                            downloads = downloads,
                            isGuestUser = isGuestUser,
                            onPromptSignIn = onPromptGuestToSignIn,
                            onStoryClick = { selectedStoryDetail = it },
                            onPlayHistoryItem = { hist ->
                                onPlayEpisode(hist.story, hist.episode)
                                isFullPlayerOpen = true
                            },
                            onPlayDownload = { dl ->
                                val story = stories.find { it.id == dl.storyId }
                                val epList = episodesMap[dl.storyId] ?: emptyList()
                                val ep = epList.find { it.id == dl.episodeId }
                                if (story != null && ep != null) {
                                    onPlayEpisode(story, ep)
                                    isFullPlayerOpen = true
                                }
                            },
                            onDeleteDownload = onDeleteDownload,
                            onSeekBookmark = { bm ->
                                val story = stories.find { it.id == bm.storyId }
                                val epList = episodesMap[bm.storyId] ?: emptyList()
                                val ep = epList.find { it.id == bm.episodeId }
                                if (story != null && ep != null) {
                                    onPlayEpisode(story, ep)
                                    onSeekFraction(if (ep.durationSec > 0) bm.positionSec.toFloat() / ep.durationSec else 0f)
                                    isFullPlayerOpen = true
                                }
                            },
                            onDeleteBookmark = onDeleteBookmark,
                            onClearHistory = onClearHistory
                        )
                    }
                    selectedBottomTab == 4 -> {
                        ProfileSettingsScreen(
                            currentTheme = com.example.ui.theme.AppThemeMode.DARK,
                            defaultSpeed = defaultSpeed,
                            autoNext = autoNext,
                            wifiOnly = wifiOnly,
                            voiceBoost = voiceBoost,
                            silenceSkip = silenceSkip,
                            smartVolume = smartVolume,
                            gestureControls = gestureControls,
                            userName = userName,
                            userEmail = userEmail,
                            isGuestUser = isGuestUser,
                            onSignOut = onSignOut,
                            onPromptGuestToSignIn = onPromptGuestToSignIn,
                            onSetTheme = onSetTheme,
                            onSetSpeed = onSetDefaultSpeed,
                            onToggleAutoNext = onToggleAutoNext,
                            onToggleWifiOnly = onToggleWifiOnly,
                            onToggleVoiceBoost = onToggleVoiceBoost,
                            onToggleSilenceSkip = onToggleSilenceSkip,
                            onToggleSmartVolume = onToggleSmartVolume,
                            onToggleGestures = onToggleGestures,
                            onOpenAdminPortal = { isAdminPortalOpen = true }
                        )
                    }
                }
            }
        }

        // Full Screen Player Modal with Slide-up Animation
        AnimatedVisibility(
            visible = isFullPlayerOpen && playerState.currentEpisode != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            FullPlayerModal(
                playerState = playerState,
                onDismiss = { isFullPlayerOpen = false },
                onPlayPause = onTogglePlayPause,
                onSeekFraction = onSeekFraction,
                onRewind15 = onRewind15,
                onForward30 = onForward30,
                onPreviousEpisode = onPreviousEpisode,
                onNextEpisode = onNextEpisode,
                onSetSpeed = onSetSpeed,
                onSetSleepTimer = onSetSleepTimer,
                onCancelSleepTimer = onCancelSleepTimer,
                onToggleFavorite = onToggleFavorite,
                onToggleDownload = onToggleDownload,
                onAddBookmark = onAddBookmark,
                onClearQueue = onClearQueue,
                onRemoveFromQueue = onRemoveFromQueue,
                onToggleCarMode = onToggleCarMode,
                onCancelAutoNext = onCancelAutoNext,
                onPlayNextNow = onPlayNextNow
            )
        }
    }
}
