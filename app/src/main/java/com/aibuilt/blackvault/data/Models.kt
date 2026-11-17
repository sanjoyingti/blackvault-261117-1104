package com.aibuilt.blackvault.data

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val TARGET_AMOUNT = "\$10,000.00"

@Serializable
data class BreachRecord(
    val id: String,
    val bankName: String,
    val country: String,
    val accountNumber: String,
    val code: String,
    val txId: String,
    val timestamp: Long,
    val amount: String = TARGET_AMOUNT,
    val logsLeft: Int = 0
) {
    val maskedAccount: String
        get() = if (accountNumber.length <= 4) {
            "*".repeat(4) + accountNumber
        } else {
            "*".repeat(accountNumber.length - 4) + accountNumber.takeLast(4)
        }
}

fun formatTimestamp(timestamp: Long): String =
    SimpleDateFormat("dd MMM yyyy  HH:mm:ss", Locale.US).format(Date(timestamp))

data class Settings(
    val splashEnabled: Boolean = true,
    val soundEnabled: Boolean = true
)
