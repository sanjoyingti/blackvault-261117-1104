package com.aibuilt.blackvault.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aibuilt.blackvault.ui.theme.AlertRed
import com.aibuilt.blackvault.ui.theme.DimText
import com.aibuilt.blackvault.ui.theme.MatrixGreen
import com.aibuilt.blackvault.ui.theme.VaultBorder
import com.aibuilt.blackvault.ui.theme.VaultPanel

@Composable
fun GridBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier.background(Color.Black)) {
        Canvas(Modifier.fillMaxSize()) {
            val step = 36.dp.toPx()
            val grid = MatrixGreen.copy(alpha = 0.07f)
            var x = 0f
            while (x <= size.width) {
                drawLine(grid, Offset(x, 0f), Offset(x, size.height), 1f)
                x += step
            }
            var y = 0f
            while (y <= size.height) {
                drawLine(grid, Offset(0f, y), Offset(size.width, y), 1f)
                y += step
            }
            val scan = MatrixGreen.copy(alpha = 0.035f)
            var sy = 0f
            val lineHeight = 4.dp.toPx()
            while (sy <= size.height) {
                drawLine(scan, Offset(0f, sy), Offset(size.width, sy), 1f)
                sy += lineHeight
            }
        }
        content()
    }
}

@Composable
fun TerminalPanel(
    modifier: Modifier = Modifier,
    borderColor: Color = VaultBorder,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(VaultPanel, RoundedCornerShape(6.dp))
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(14.dp),
        content = content
    )
}

@Composable
fun SectionLabel(text: String, color: Color = DimText) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable
fun BlinkingCursor(color: Color = MatrixGreen, width: Int = 9, height: Int = 15) {
    val transition = rememberInfiniteTransition(label = "cursor")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(520), RepeatMode.Reverse),
        label = "cursorAlpha"
    )
    Box(
        Modifier
            .width(width.dp)
            .height(height.dp)
            .background(color.copy(alpha = alpha))
    )
}

@Composable
fun vaultFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MatrixGreen,
    unfocusedTextColor = MatrixGreen,
    disabledTextColor = DimText,
    cursorColor = MatrixGreen,
    focusedBorderColor = MatrixGreen,
    unfocusedBorderColor = MatrixGreen.copy(alpha = 0.35f),
    errorBorderColor = AlertRed,
    focusedLabelColor = MatrixGreen,
    unfocusedLabelColor = DimText,
    errorLabelColor = AlertRed,
    focusedContainerColor = VaultPanel,
    unfocusedContainerColor = VaultPanel,
    errorContainerColor = VaultPanel
)
