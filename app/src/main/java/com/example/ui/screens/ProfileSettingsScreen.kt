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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWave
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.CyanWave
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.TextMutedDark

/**
 * Premium Profile & Settings Screen:
 * Top:
 * Profile avatar, Name, Email
 * Then:
 * My Episodes
 * Favorites
 * Following
 * Listening History
 * Notifications
 * Devices
 * Settings
 * Logout button at bottom
 * Guest Mode: clean non-restricted look with simple sign-in CTA
 */
@Composable
fun ProfileSettingsScreen(
    currentTheme: AppThemeMode,
    defaultSpeed: Float,
    autoNext: Boolean,
    wifiOnly: Boolean,
    voiceBoost: Boolean,
    silenceSkip: Boolean,
    smartVolume: Boolean,
    gestureControls: Boolean,
    userName: String = "JD WAVE Member",
    userEmail: String = "Synced with Firebase",
    isGuestUser: Boolean = false,
    onSignOut: () -> Unit = {},
    onPromptGuestToSignIn: () -> Unit = {},
    onNavigateMyEpisodes: () -> Unit = {},
    onNavigateFavorites: () -> Unit = {},
    onNavigateHistory: () -> Unit = {},
    onSetTheme: (AppThemeMode) -> Unit,
    onSetSpeed: (Float) -> Unit,
    onToggleAutoNext: (Boolean) -> Unit,
    onToggleWifiOnly: (Boolean) -> Unit,
    onToggleVoiceBoost: (Boolean) -> Unit,
    onToggleSilenceSkip: (Boolean) -> Unit,
    onToggleSmartVolume: (Boolean) -> Unit,
    onToggleGestures: (Boolean) -> Unit,
    onOpenAdminPortal: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .testTag("profile_settings_screen"),
        contentPadding = PaddingValues(bottom = 120.dp, top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        // User Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(if (isGuestUser) DarkSurfaceVariant else CyanWave),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isGuestUser) Color.White else DeepObsidian,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isGuestUser) AmberWave else CyanWave
                        )
                    }
                }
                if (isGuestUser) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurfaceVariant)
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Guest Listener Session",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )
                        Button(
                            onClick = onPromptGuestToSignIn,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = DeepObsidian),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Sign In Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Quick Navigation Menu items
        item {
            Text(
                text = "MY CONTENT",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = CyanWave
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProfileMenuItem(icon = Icons.Default.Headphones, title = "My Episodes", subtitle = "Purchased, Daily Free & Telegram unlocks", onClick = onNavigateMyEpisodes)
            ProfileMenuItem(icon = Icons.Default.Favorite, title = "Favorites", subtitle = "Your saved Telugu audio dramas", onClick = onNavigateFavorites)
            ProfileMenuItem(icon = Icons.Default.History, title = "Listening History", subtitle = "Track your recent playback progress", onClick = onNavigateHistory)
            ProfileMenuItem(icon = Icons.Default.Notifications, title = "Notifications", subtitle = "Daily unlocks, releases and announcements", onClick = {})
            ProfileMenuItem(icon = Icons.Default.DeviceHub, title = "Devices", subtitle = "Manage playback sync on active sessions", onClick = {})
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Playback Engine & Audio Settings
        item {
            Text(
                text = "AUDIO ENGINE SETTINGS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = AmberWave
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SettingToggleRow(
                        title = "Auto-Play Next Episode",
                        subtitle = "Automatically transition to next audio track",
                        checked = autoNext,
                        onCheckedChange = onToggleAutoNext
                    )
                    SettingToggleRow(
                        title = "Wi-Fi Only Streaming",
                        subtitle = "Stream high-definition audio over Wi-Fi",
                        checked = wifiOnly,
                        onCheckedChange = onToggleWifiOnly
                    )
                    SettingToggleRow(
                        title = "Dialog & Voice Boost",
                        subtitle = "Enhance narrative clarity and speech frequencies",
                        checked = voiceBoost,
                        onCheckedChange = onToggleVoiceBoost
                    )
                    SettingToggleRow(
                        title = "Smart Volume Normalization",
                        subtitle = "Even out loud and quiet audio drama dynamics",
                        checked = smartVolume,
                        onCheckedChange = onToggleSmartVolume
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Administration Console Access
        item {
            Text(
                text = "MANAGEMENT",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = TextMutedDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                    .clickable {
                        if (isGuestUser) onPromptGuestToSignIn() else onOpenAdminPortal()
                    },
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AmberWave.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = AmberWave, modifier = Modifier.size(20.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Admin Dashboard Portal", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        Text(text = "Story publishing, daily free episode, Telegram unlock approvals", style = MaterialTheme.typography.bodySmall, color = CyanWave)
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Account Sign Out / Sign In
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isGuestUser) onPromptGuestToSignIn() else onSignOut()
                    }
                    .testTag("sign_out_button"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isGuestUser) CyanWave.copy(alpha = 0.15f) else Color(0x33F43F5E)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isGuestUser) "Sign In / Create Account" else "Sign Out of Account",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isGuestUser) CyanWave else Color(0xFFF43F5E)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // App Version
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "JD WAVE • v2.4 (Cinematic Audio Edition)", fontSize = 11.sp, color = TextMutedDark)
                Text(text = "Protected Telugu Serialized Drama", fontSize = 10.sp, color = Color(0xFF475569))
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = CyanWave, modifier = Modifier.size(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Text(text = subtitle, color = TextMutedDark, fontSize = 11.sp)
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMutedDark, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Color.White)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = TextMutedDark)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = DeepObsidian,
                checkedTrackColor = CyanWave,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = DarkSurfaceVariant
            )
        )
    }
}
