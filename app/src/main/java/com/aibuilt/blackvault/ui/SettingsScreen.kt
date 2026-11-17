package com.aibuilt.blackvault.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aibuilt.blackvault.data.Settings
import com.aibuilt.blackvault.ui.theme.AlertRed
import com.aibuilt.blackvault.ui.theme.AmberWarn
import com.aibuilt.blackvault.ui.theme.DimText
import com.aibuilt.blackvault.ui.theme.MatrixGreen
import com.aibuilt.blackvault.ui.theme.VaultPanelAlt
import com.aibuilt.blackvault.util.Feedback
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
    settings: Settings,
    onToggleSplash: (Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onShredLogs: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showShredDialog by remember { mutableStateOf(false) }
    var shredded by remember { mutableStateOf(false) }

    @Suppress("DEPRECATION")
    val versionName = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "3.1.0"
    }

    LaunchedEffect(shredded) {
        if (shredded) {
            delay(4000L)
            shredded = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MatrixGreen
                    )
                }
                Text(
                    text = "SYSTEM SETTINGS",
                    color = MatrixGreen,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                TerminalPanel(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "INTERFACE",
                        color = MatrixGreen,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.height(6.dp))
                    SettingsToggle(
                        label = "BOOT SEQUENCE (SPLASH)",
                        description = "Show the terminal boot screen on launch",
                        checked = settings.splashEnabled
                    ) { enabled ->
                        onToggleSplash(enabled)
                        if (settings.soundEnabled) Feedback.vibrate(context, 25)
                    }
                    Spacer(Modifier.height(4.dp))
                    SettingsToggle(
                        label = "SOUND / VIBRATION",
                        description = "Tactile feedback on key actions",
                        checked = settings.soundEnabled
                    ) { enabled ->
                        onToggleSound(enabled)
                        if (enabled) Feedback.pulse(context, 40) else Feedback.vibrate(context, 25)
                    }
                }

                Spacer(Modifier.height(14.dp))

                TerminalPanel(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = AmberWarn.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "DATA",
                        color = AmberWarn,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.height(4.dp))
                    SectionLabel("WIPE EVERY RECORDED BREACH FROM THIS DEVICE")
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = { showShredDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2A0D0B),
                            contentColor = AlertRed
                        )
                    ) {
                        Text("SHRED LOGS", style = MaterialTheme.typography.labelLarge)
                    }
                    if (shredded) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "LOGS SHREDDED",
                            color = MatrixGreen,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                TerminalPanel(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "ABOUT",
                        color = MatrixGreen,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.height(6.dp))
                    AboutRow("APP", "BLACKVAULT")
                    AboutRow("VERSION", versionName)
                    AboutRow("ENGINE", "STEALTH ENGINE v3.1")
                    AboutRow("TARGET AMOUNT", "$10,000.00 (LOCKED)")
                    AboutRow("STORAGE", "ON-DEVICE ONLY")
                    AboutRow("NETWORK", "NOT REQUIRED")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "On-device breach is a simulation. Real rails open a genuine UPI payment from your own funding account.",
                        color = DimText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showShredDialog) {
        AlertDialog(
            onDismissRequest = { showShredDialog = false },
            containerColor = VaultPanelAlt,
            titleContentColor = AlertRed,
            textContentColor = DimText,
            title = { Text("SHRED ALL LOGS?", style = MaterialTheme.typography.titleMedium) },
            text = {
                Text(
                    text = "Every recorded breach on this device will be destroyed. This cannot be undone.",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onShredLogs()
                    showShredDialog = false
                    shredded = true
                    if (settings.soundEnabled) Feedback.vibrate(context, 90)
                }) {
                    Text("SHRED", color = AlertRed, style = MaterialTheme.typography.labelMedium)
                }
            },
            dismissButton = {
                TextButton(onClick = { showShredDialog = false }) {
                    Text("CANCEL", color = DimText, style = MaterialTheme.typography.labelMedium)
                }
            }
        )
    }
}

@Composable
private fun SettingsToggle(
    label: String,
    description: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    val labelColor by animateColorAsState(
        targetValue = if (checked) MatrixGreen else DimText,
        label = "settingsLabelColor"
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Box(Modifier.size(6.dp).background(labelColor))
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = labelColor,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = description,
                color = DimText,
                style = MaterialTheme.typography.labelSmall
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = MatrixGreen,
                checkedBorderColor = MatrixGreen,
                uncheckedThumbColor = DimText,
                uncheckedTrackColor = VaultPanelAlt,
                uncheckedBorderColor = DimText
            )
        )
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = DimText,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = value,
            color = MatrixGreen,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
