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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aibuilt.blackvault.data.BankDatabase
import com.aibuilt.blackvault.data.TARGET_AMOUNT
import com.aibuilt.blackvault.ui.theme.AlertRed
import com.aibuilt.blackvault.ui.theme.AmberWarn
import com.aibuilt.blackvault.ui.theme.DimText
import com.aibuilt.blackvault.ui.theme.MatrixGreen
import com.aibuilt.blackvault.ui.theme.VaultBorder
import com.aibuilt.blackvault.ui.theme.VaultPanelAlt
import com.aibuilt.blackvault.viewmodel.VaultViewModel

@Composable
fun DashboardScreen(
    vm: VaultViewModel,
    onInitiate: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit
) {
    val account by vm.accountNumber.collectAsState()
    val code by vm.code.collectAsState()
    val stealth by vm.stealth.collectAsState()
    val liveIp by vm.ip.collectAsState()
    val trace by vm.traceBlocked.collectAsState()
    val error by vm.validationError.collectAsState()

    val detected = remember(code) { BankDatabase.detect(code) }
    val codeValid = remember(code) { BankDatabase.isValidCode(code) }
    val canInitiate = account.isNotEmpty() && codeValid

    GridBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MatrixGreen,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "BLACKVAULT",
                    color = MatrixGreen,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onHistory) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = "History",
                        tint = MatrixGreen
                    )
                }
                IconButton(onClick = onSettings) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MatrixGreen
                    )
                }
            }

            SectionLabel("STEALTH ENGINE v3.1 - TARGET ACQUISITION")
            Spacer(Modifier.height(14.dp))

            TerminalPanel(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = AmberWarn,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    SectionLabel("TARGET AMOUNT (LOCKED)")
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = TARGET_AMOUNT,
                    color = MatrixGreen,
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(Modifier.height(2.dp))
                SectionLabel("IMMUTABLE - HARD-CODED IN ENGINE")
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = account,
                onValueChange = { vm.setAccountNumber(it) },
                label = { Text("ACCOUNT NUMBER") },
                placeholder = { Text("0000000000", color = DimText) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = vaultFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = code,
                onValueChange = { vm.setCode(it) },
                label = { Text("IFSC CODE / US ROUTING NUMBER") },
                placeholder = { Text("HDFC0001234  |  021000021", color = DimText) },
                singleLine = true,
                isError = code.isNotEmpty() && !codeValid,
                modifier = Modifier.fillMaxWidth(),
                colors = vaultFieldColors(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    keyboardType = KeyboardType.Ascii
                )
            )

            if (code.isNotEmpty() && !codeValid) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "INVALID: 4 LETTERS + 0 + 6 CHARS (IFSC) OR 9 DIGITS (ROUTING)",
                    color = AlertRed,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            error?.let { message ->
                Spacer(Modifier.height(4.dp))
                Text(
                    text = message,
                    color = AlertRed,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(12.dp))

            TerminalPanel(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (detected != null) VaultBorder else AmberWarn.copy(alpha = 0.5f)
            ) {
                SectionLabel("AUTO BANK DETECTION")
                Spacer(Modifier.height(4.dp))
                Text(
                    text = detected?.name ?: "UNKNOWN ENTITY",
                    color = if (detected != null) MatrixGreen else AmberWarn,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = if (detected != null) {
                        "COUNTRY: ${detected.country}  |  CODE: ${detected.code}"
                    } else {
                        "TYPE AN IFSC PREFIX OR A 9-DIGIT ROUTING NUMBER"
                    },
                    color = DimText,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(12.dp))

            TerminalPanel(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "STEALTH MODE",
                        color = MatrixGreen,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = if (stealth.allEnabled) "ALL SYSTEMS ON" else "DEGRADED",
                        color = if (stealth.allEnabled) MatrixGreen else AmberWarn,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(Modifier.height(4.dp))
                StealthToggle("IP SPOOFING (7-HOP TOR CHAIN)", stealth.ipSpoofing) {
                    vm.setIpSpoofing(it)
                }
                StealthToggle("MAC RANDOMISER", stealth.macRandomiser) {
                    vm.setMacRandomiser(it)
                }
                StealthToggle("DNS LEAK BLOCKER", stealth.dnsLeakBlocker) {
                    vm.setDnsLeakBlocker(it)
                }
                StealthToggle("TRACE SCRUBBER", stealth.traceScrubber) {
                    vm.setTraceScrubber(it)
                }
                StealthToggle("ANTI-FORENSICS", stealth.antiForensics) {
                    vm.setAntiForensics(it)
                }
            }

            Spacer(Modifier.height(12.dp))

            TerminalPanel(
                modifier = Modifier.fillMaxWidth(),
                borderColor = MatrixGreen.copy(alpha = 0.35f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SectionLabel("LIVE ORIGIN IP")
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier
                            .size(6.dp)
                            .background(MatrixGreen)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "ROTATING",
                        color = MatrixGreen,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = liveIp,
                    color = MatrixGreen,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "TRACE ATTEMPTS BLOCKED: $trace",
                    color = if (trace > 0) AmberWarn else DimText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onInitiate,
                enabled = canInitiate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreen,
                    contentColor = Color.Black,
                    disabledContainerColor = Color(0xFF0C2A16),
                    disabledContentColor = DimText
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = if (canInitiate) Color.Black else DimText,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "INITIATE BREACH",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(Modifier.height(8.dp))
            SectionLabel(
                text = if (canInitiate) {
                    "ALL SYSTEMS GREEN - READY TO RUN"
                } else {
                    "ENTER ACCOUNT NUMBER + VALID IFSC OR ROUTING TO ARM"
                }
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun StealthToggle(
    label: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    val labelColor by animateColorAsState(
        targetValue = if (checked) MatrixGreen else DimText,
        label = "stealthLabelColor"
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Box(
            Modifier
                .size(6.dp)
                .background(labelColor)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            color = labelColor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            modifier = Modifier.scale(0.8f),
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
