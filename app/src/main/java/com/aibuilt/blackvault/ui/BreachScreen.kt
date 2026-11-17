package com.aibuilt.blackvault.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aibuilt.blackvault.data.Settings
import com.aibuilt.blackvault.ui.theme.AlertRed
import com.aibuilt.blackvault.ui.theme.AmberWarn
import com.aibuilt.blackvault.ui.theme.MatrixGreen
import com.aibuilt.blackvault.ui.theme.VaultBorder
import com.aibuilt.blackvault.util.Feedback
import com.aibuilt.blackvault.viewmodel.VaultViewModel

@Composable
fun BreachScreen(
    vm: VaultViewModel,
    onFinished: (String) -> Unit,
    onAbort: () -> Unit
) {
    val lines by vm.breachLines.collectAsState()
    val progress by vm.breachProgress.collectAsState()
    val trace by vm.traceBlocked.collectAsState()
    val finishedId by vm.finishedRecordId.collectAsState()
    val settingsState by vm.settings.collectAsState(initial = Settings())
    val context = LocalContext.current
    val listState = rememberLazyListState()

    BackHandler { onAbort() }

    LaunchedEffect(Unit) {
        if (settingsState.soundEnabled) {
            Feedback.pulse(context, 45)
        }
    }

    LaunchedEffect(lines.size) {
        if (lines.isNotEmpty()) {
            listState.animateScrollToItem(lines.size)
        }
    }

    LaunchedEffect(finishedId) {
        val id = finishedId
        if (id != null) {
            if (settingsState.soundEnabled) {
                Feedback.pulse(context, 120)
            }
            onFinished(id)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "BREACH IN PROGRESS",
                    color = MatrixGreen,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = MatrixGreen,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MatrixGreen,
                trackColor = Color(0xFF0A2A15)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "TRACE ATTEMPTS BLOCKED: $trace",
                color = AmberWarn,
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Black, RoundedCornerShape(6.dp))
                    .border(1.dp, VaultBorder, RoundedCornerShape(6.dp))
                    .padding(10.dp)
            ) {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items(lines) { line ->
                        Text(
                            text = line,
                            color = if (line.contains("BREACHED")) {
                                MatrixGreen
                            } else {
                                MatrixGreen.copy(alpha = 0.82f)
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "> ",
                                color = MatrixGreen,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            BlinkingCursor()
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onAbort,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, AlertRed),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed)
            ) {
                Text(
                    text = "ABORT",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(Modifier.width(4.dp))
        }
    }
}
