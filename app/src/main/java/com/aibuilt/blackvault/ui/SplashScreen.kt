package com.aibuilt.blackvault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aibuilt.blackvault.ui.theme.DimText
import com.aibuilt.blackvault.ui.theme.MatrixGreen
import kotlinx.coroutines.delay
import kotlin.random.Random

private val BOOT_LINES = listOf(
    "BLACKVAULT STEALTH ENGINE v3.1",
    "initialising crypto core...",
    "loading bank database...",
    "verifying network layer...",
    "STATUS: ONLINE"
)

private fun fakeChecksum(): String {
    val chars = "0123456789ABCDEF"
    return (1..40).map { chars[Random.nextInt(chars.length)] }.joinToString("")
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var visibleLines by remember { mutableIntStateOf(1) }
    var finished by remember { mutableStateOf(false) }
    val checksum = remember { fakeChecksum() }

    fun finishOnce() {
        if (!finished) {
            finished = true
            onFinished()
        }
    }

    LaunchedEffect(Unit) {
        delay(280L)
        for (index in 1 until BOOT_LINES.size) {
            delay(520L)
            visibleLines = index + 1
        }
        delay(900L)
        finishOnce()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth()
        ) {
            BOOT_LINES.take(visibleLines).forEachIndexed { index, line ->
                Text(
                    text = if (index == 0) line else "> $line",
                    color = if (index == BOOT_LINES.lastIndex) MatrixGreen else MatrixGreen.copy(alpha = 0.85f),
                    style = if (index == 0) {
                        MaterialTheme.typography.titleMedium
                    } else {
                        MaterialTheme.typography.bodyMedium
                    }
                )
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = "SHA-256 $checksum",
                color = DimText,
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "root@blackvault:~$",
                    color = MatrixGreen,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.width(6.dp))
                BlinkingCursor()
            }
        }

        TextButton(
            onClick = { finishOnce() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            Text(
                text = "SKIP",
                color = DimText,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
