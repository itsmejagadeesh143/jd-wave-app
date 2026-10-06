package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.auth.GoogleAuthService
import com.example.ui.theme.AmberWave
import com.example.ui.theme.CyanWave
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.TextMutedDark

private val DarkCard = Color(0xFF131822)
private val SubtextGray = Color(0xFF94A3B8)

@Composable
fun SignInScreen(
    onAuthSuccess: () -> Unit,
    onEmailAuthSuccess: (name: String, email: String) -> Unit = { _, _ -> },
    onContinueAsGuest: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var selectedAuthTab by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var isRegPasswordVisible by remember { mutableStateOf(false) }

    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showTermsPrivacyDialog by remember { mutableStateOf(false) }
    var resetEmailInput by remember { mutableStateOf("") }
    var resetSuccessNotice by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepObsidian,
                        Color(0xFF0F172A),
                        Color(0xFF070A0F)
                    )
                )
            )
            .testTag("sign_in_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                CyanWave.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = CyanWave.copy(alpha = 0.15f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "JD WAVE Logo",
                            tint = CyanWave,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "JD WAVE",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Text(
                text = "Serialized Telugu Audio Stories & Dramas",
                style = MaterialTheme.typography.bodyMedium,
                color = SubtextGray,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            TabRow(
                selectedTabIndex = selectedAuthTab,
                containerColor = DarkSurfaceVariant,
                contentColor = CyanWave,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedAuthTab]),
                        color = AmberWave
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .fillMaxWidth()
            ) {
                Tab(
                    selected = selectedAuthTab == 0,
                    onClick = {
                        selectedAuthTab = 0
                        errorMessage = null
                    },
                    text = { Text("Quick Access", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedAuthTab == 1,
                    onClick = {
                        selectedAuthTab = 1
                        errorMessage = null
                    },
                    text = { Text("Email Login", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedAuthTab == 2,
                    onClick = {
                        selectedAuthTab = 2
                        errorMessage = null
                    },
                    text = { Text("Register", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            if (selectedAuthTab == 0) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FeatureHighlightRow(
                            icon = Icons.Default.CloudDone,
                            iconTint = CyanWave,
                            title = "Cloud Playback Sync",
                            subtitle = "Pick up right where you left off on any Android device"
                        )
                        FeatureHighlightRow(
                            icon = Icons.Default.Security,
                            iconTint = AmberWave,
                            title = "Daily Free Episode at 6:00 AM IST",
                            subtitle = "Automatic daily rotation of premium serialized drama"
                        )
                        FeatureHighlightRow(
                            icon = Icons.Default.Lock,
                            iconTint = Color(0xFF10B981),
                            title = "Private & Secure Streaming",
                            subtitle = "AES-256 encrypted offline sandbox audio protection"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        GoogleAuthService.onGoogleSignInClicked(
                            context = context,
                            credentialManager = credentialManager,
                            onAuthSuccess = {
                                isLoading = false
                                onAuthSuccess()
                            },
                            onAuthError = { err ->
                                isLoading = false
                                errorMessage = err
                            },
                            scope = coroutineScope,
                            onAuthCancelled = {
                                isLoading = false
                            }
                        )
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanWave,
                        contentColor = DeepObsidian
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("google_sign_in_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = DeepObsidian,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Continue with Google",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Continue as Guest Button (Prominent & Clear)
                OutlinedButton(
                    onClick = onContinueAsGuest,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("continue_as_guest_button")
                ) {
                    Text(
                        text = "Continue as Guest",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Browse stories, view details, and play free episodes without an account",
                    style = MaterialTheme.typography.labelSmall,
                    color = SubtextGray,
                    textAlign = TextAlign.Center
                )
            }

            if (selectedAuthTab == 1) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Sign in to JD WAVE",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        OutlinedTextField(
                            value = loginEmail,
                            onValueChange = { loginEmail = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CyanWave) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_login_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanWave,
                                unfocusedBorderColor = DarkBorder,
                                focusedContainerColor = DarkSurfaceVariant,
                                unfocusedContainerColor = DarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CyanWave) },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = SubtextGray
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_password_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanWave,
                                unfocusedBorderColor = DarkBorder,
                                focusedContainerColor = DarkSurfaceVariant,
                                unfocusedContainerColor = DarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showForgotPasswordDialog = true },
                                modifier = Modifier.testTag("forgot_password_btn")
                            ) {
                                Text("Forgot Password?", color = AmberWave, fontSize = 12.sp)
                            }
                        }
                        Button(
                            onClick = {
                                if (loginEmail.isBlank() || !loginEmail.contains("@")) {
                                    errorMessage = "Please enter a valid email address."
                                } else if (loginPassword.length < 6) {
                                    errorMessage = "Password must be at least 6 characters."
                                } else {
                                    errorMessage = null
                                    val name = loginEmail.substringBefore("@").replace(".", " ")
                                        .replaceFirstChar { it.uppercase() }
                                    onEmailAuthSuccess(name, loginEmail)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = DeepObsidian),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("email_login_submit_btn")
                        ) {
                            Text("Sign In with Email", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onContinueAsGuest,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Continue as Guest", fontSize = 13.sp)
                        }
                    }
                }
            }

            if (selectedAuthTab == 2) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Create JD WAVE Account",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CyanWave) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_name_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanWave,
                                unfocusedBorderColor = DarkBorder,
                                focusedContainerColor = DarkSurfaceVariant,
                                unfocusedContainerColor = DarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CyanWave) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_email_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanWave,
                                unfocusedBorderColor = DarkBorder,
                                focusedContainerColor = DarkSurfaceVariant,
                                unfocusedContainerColor = DarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Password (min 6 chars)") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CyanWave) },
                            trailingIcon = {
                                IconButton(onClick = { isRegPasswordVisible = !isRegPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isRegPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = SubtextGray
                                    )
                                }
                            },
                            visualTransformation = if (isRegPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_password_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanWave,
                                unfocusedBorderColor = DarkBorder,
                                focusedContainerColor = DarkSurfaceVariant,
                                unfocusedContainerColor = DarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = regConfirmPassword,
                            onValueChange = { regConfirmPassword = it },
                            label = { Text("Confirm Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AmberWave) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_confirm_password_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanWave,
                                unfocusedBorderColor = DarkBorder,
                                focusedContainerColor = DarkSurfaceVariant,
                                unfocusedContainerColor = DarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Button(
                            onClick = {
                                if (regName.isBlank()) {
                                    errorMessage = "Please enter your name."
                                } else if (regEmail.isBlank() || !regEmail.contains("@")) {
                                    errorMessage = "Please enter a valid email address."
                                } else if (regPassword.length < 6) {
                                    errorMessage = "Password must be at least 6 characters."
                                } else if (regPassword != regConfirmPassword) {
                                    errorMessage = "Passwords do not match."
                                } else {
                                    errorMessage = null
                                    onEmailAuthSuccess(regName, regEmail)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberWave, contentColor = DeepObsidian),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("reg_submit_btn")
                        ) {
                            Text("Create Account", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onContinueAsGuest,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Continue as Guest", fontSize = 13.sp)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = errorMessage != null) {
                errorMessage?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF43F5E).copy(alpha = 0.15f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFFF43F5E),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                                .testTag("auth_error_text")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Terms & Privacy Policy",
                    style = MaterialTheme.typography.labelSmall,
                    color = SubtextGray,
                    modifier = Modifier
                        .clickable { showTermsPrivacyDialog = true }
                        .padding(8.dp)
                        .testTag("terms_privacy_link")
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showForgotPasswordDialog = false
                resetSuccessNotice = false
            },
            title = { Text("Reset Your Password", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Enter your registered email address to receive password reset instructions.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = resetEmailInput,
                        onValueChange = { resetEmailInput = it },
                        placeholder = { Text("name@example.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanWave,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        )
                    )
                    if (resetSuccessNotice) {
                        Text(
                            text = "Reset instructions sent! Check your inbox.",
                            color = Color(0xFF10B981),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resetEmailInput.contains("@")) {
                            resetSuccessNotice = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = Color.Black)
                ) {
                    Text("Send Reset Link")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showForgotPasswordDialog = false
                    resetSuccessNotice = false
                }) {
                    Text("Close", color = Color.White)
                }
            },
            containerColor = DarkSurface
        )
    }

    if (showTermsPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showTermsPrivacyDialog = false },
            title = { Text("JD WAVE • Terms & Privacy", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "1. Audio Streaming License",
                        fontWeight = FontWeight.Bold,
                        color = AmberWave,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "JD WAVE provides licensed streaming access to Telugu audio stories and serialized productions. Audio is streamed directly within the client application.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "2. Entitlements & Daily Free Episodes",
                        fontWeight = FontWeight.Bold,
                        color = AmberWave,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Every day at 6:00 AM IST, one episode is unlocked freely for all listeners. Premium episodes can be unlocked via instant transaction or Telegram support verification (@JDWaveSupport).",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "3. Guest Access",
                        fontWeight = FontWeight.Bold,
                        color = AmberWave,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Guests can explore the full catalog, listen to free and daily unlocked episodes without submitting personal data. Account registration is required for persistent entitlements and cross-device sync.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTermsPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanWave, contentColor = Color.Black)
                ) {
                    Text("I Understand")
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
private fun FeatureHighlightRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = iconTint.copy(alpha = 0.15f),
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SubtextGray
            )
        }
    }
}
