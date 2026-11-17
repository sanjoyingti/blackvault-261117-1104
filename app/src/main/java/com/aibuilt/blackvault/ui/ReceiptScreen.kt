package com.aibuilt.blackvault.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aibuilt.blackvault.data.BreachRecord
import com.aibuilt.blackvault.data.formatTimestamp
import com.aibuilt.blackvault.ui.theme.AlertRed
import com.aibuilt.blackvault.ui.theme.AmberWarn
import com.aibuilt.blackvault.ui.theme.DimText
import com.aibuilt.blackvault.ui.theme.MatrixGreen
import com.aibuilt.blackvault.ui.theme.VaultBorder
import com.aibuilt.blackvault.util.UpiIntent

@Composable
fun ReceiptScreen(
    record: BreachRecord,
    onViewLog: () -> Unit,
    onNewBreach: () -> Unit
) {
    val context = LocalContext.current
    var vpa by remember { mutableStateOf("") }
    var upiMessage by remember { mutableStateOf<String?>(null) }
    val vpaValid = remember(vpa) { UpiIntent.isValidVpa(vpa) }

    GridBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            SectionLabel("BLACKVAULT - SETTLEMENT RECEIPT")
            Spacer(Modifier.height(12.dp))

            TerminalPanel(
                modifier = Modifier.fillMaxWidth(),
                borderColor = MatrixGreen
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MatrixGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "DEPOSIT CONFIRMED",
                        color = MatrixGreen,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = record.amount,
                    color = MatrixGreen,
                    style = MaterialTheme.typography.displayLarge
                )
                SectionLabel("CREDITED TO TARGET ACCOUNT")

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = VaultBorder)
                Spacer(Modifier.height(10.dp))

                ReceiptRow("BANK", record.bankName, MatrixGreen)
                ReceiptRow("REGION", record.country, DimText)
                ReceiptRow("ACCOUNT", record.maskedAccount, MatrixGreen)
                ReceiptRow("TXN ID", record.txId, MatrixGreen)
                ReceiptRow("TIMESTAMP", formatTimestamp(record.timestamp), DimText)
                ReceiptRow("STATUS", "SETTLED", MatrixGreen)

                Spacer(Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(MatrixGreen.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                        .border(1.dp, MatrixGreen, RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = MatrixGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "UNTRACEABLE \u2014 0 LOGS LEFT",
                        color = MatrixGreen,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onViewLog,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, MatrixGreen),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MatrixGreen)
                ) {
                    Text("VIEW LOG", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = onNewBreach,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatrixGreen,
                        contentColor = Color.Black
                    )
                ) {
                    Text("NEW BREACH", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(Modifier.height(20.dp))

            TerminalPanel(
                modifier = Modifier.fillMaxWidth(),
                borderColor = AmberWarn.copy(alpha = 0.55f)
            ) {
                Text(
                    text = "REAL RAILS",
                    color = AmberWarn,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                SectionLabel("GENUINE PAYMENT - OPENS YOUR UPI APP")

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = vpa,
                    onValueChange = {
                        vpa = it.trim().take(70)
                        upiMessage = null
                    },
                    label = { Text("YOUR UPI ID (VPA)") },
                    placeholder = { Text("name@bank", color = DimText) },
                    singleLine = true,
                    isError = vpa.isNotEmpty() && !vpaValid,
                    modifier = Modifier.fillMaxWidth(),
                    colors = vaultFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                if (vpa.isNotEmpty() && !vpaValid) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "INVALID UPI ID FORMAT",
                        color = AlertRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (!vpaValid) {
                            upiMessage = "Enter a valid UPI ID first"
                        } else {
                            val opened = UpiIntent.openUpi(context, vpa, "10000")
                            upiMessage = if (opened) null else "No UPI app installed"
                        }
                    },
                    enabled = vpaValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberWarn,
                        contentColor = Color.Black,
                        disabledContainerColor = Color(0xFF2A2008),
                        disabledContentColor = DimText
                    )
                ) {
                    Text("OPEN UPI PAYMENT", style = MaterialTheme.typography.labelLarge)
                }

                upiMessage?.let { message ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = message,
                        color = if (message == "No UPI app installed") AlertRed else AmberWarn,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Real rails = genuine transfer from your own funding account. On-device breach is a simulation.",
                    color = DimText,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = DimText,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = value,
            color = valueColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
