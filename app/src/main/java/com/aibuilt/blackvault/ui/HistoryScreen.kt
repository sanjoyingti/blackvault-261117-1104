package com.aibuilt.blackvault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.aibuilt.blackvault.data.BreachRecord
import com.aibuilt.blackvault.data.formatTimestamp
import com.aibuilt.blackvault.ui.theme.AlertRed
import com.aibuilt.blackvault.ui.theme.AmberWarn
import com.aibuilt.blackvault.ui.theme.DimText
import com.aibuilt.blackvault.ui.theme.MatrixGreen
import com.aibuilt.blackvault.ui.theme.VaultBorder
import com.aibuilt.blackvault.ui.theme.VaultPanel
import com.aibuilt.blackvault.ui.theme.VaultPanelAlt

@Composable
fun HistoryScreen(
    history: List<BreachRecord>,
    onOpen: (String) -> Unit,
    onDelete: (String) -> Unit,
    onClearAll: () -> Unit,
    onBack: () -> Unit
) {
    var pendingDelete by remember { mutableStateOf<BreachRecord?>(null) }
    var showClearAll by remember { mutableStateOf(false) }

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
                    text = "BREACH LOG",
                    color = MatrixGreen,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.weight(1f))
                TextButton(
                    onClick = { showClearAll = true },
                    enabled = history.isNotEmpty()
                ) {
                    Text(
                        text = "CLEAR ALL",
                        color = if (history.isNotEmpty()) AlertRed else DimText,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Text(
                text = "${history.size} RECORD(S) - TAP TO OPEN, LONG-PRESS OR TAP THE BIN TO DELETE",
                color = DimText,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(10.dp))

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = DimText,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "NO BREACHES RECORDED",
                            color = DimText,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "RUN YOUR FIRST BREACH FROM THE DASHBOARD",
                            color = DimText.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(history, key = { it.id }) { record ->
                        HistoryRow(
                            record = record,
                            onClick = { onOpen(record.id) },
                            onDeleteClick = { pendingDelete = record },
                            onLongClick = { pendingDelete = record }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    pendingDelete?.let { record ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            containerColor = VaultPanelAlt,
            titleContentColor = MatrixGreen,
            textContentColor = DimText,
            title = { Text("DELETE RECORD?", style = MaterialTheme.typography.titleMedium) },
            text = {
                Text(
                    text = "${record.bankName}\n${record.maskedAccount}\n${formatTimestamp(record.timestamp)}",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(record.id)
                    pendingDelete = null
                }) {
                    Text("DELETE", color = AlertRed, style = MaterialTheme.typography.labelMedium)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("CANCEL", color = DimText, style = MaterialTheme.typography.labelMedium)
                }
            }
        )
    }

    if (showClearAll) {
        AlertDialog(
            onDismissRequest = { showClearAll = false },
            containerColor = VaultPanelAlt,
            titleContentColor = AlertRed,
            textContentColor = DimText,
            title = { Text("CLEAR ALL LOGS?", style = MaterialTheme.typography.titleMedium) },
            text = {
                Text(
                    text = "This wipes every recorded breach from this device. It cannot be undone.",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onClearAll()
                    showClearAll = false
                }) {
                    Text("CLEAR ALL", color = AlertRed, style = MaterialTheme.typography.labelMedium)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAll = false }) {
                    Text("CANCEL", color = DimText, style = MaterialTheme.typography.labelMedium)
                }
            }
        )
    }
}

@Composable
private fun HistoryRow(
    record: BreachRecord,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(VaultPanel, RoundedCornerShape(6.dp))
            .border(1.dp, VaultBorder, RoundedCornerShape(6.dp))
            .pointerInput(record.id) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick() }
                )
            }
            .padding(start = 12.dp, end = 4.dp, top = 10.dp, bottom = 10.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = record.bankName,
                    color = MatrixGreen,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "\u2713",
                    color = AmberWarn,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = record.maskedAccount,
                color = DimText,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = formatTimestamp(record.timestamp),
                color = DimText.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelSmall
            )
        }
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = record.amount,
                color = MatrixGreen,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(Modifier.height(2.dp))
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete this record",
                    tint = AlertRed,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
