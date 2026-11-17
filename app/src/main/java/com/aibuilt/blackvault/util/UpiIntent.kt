package com.aibuilt.blackvault.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object UpiIntent {

    private val vpaRegex = Regex("^[A-Za-z0-9._-]{2,64}@[A-Za-z]{2,64}$")

    fun isValidVpa(value: String): Boolean = vpaRegex.matches(value.trim())

    fun buildUri(vpa: String, amount: String): Uri = Uri.parse("upi://pay").buildUpon()
        .appendQueryParameter("pa", vpa.trim())
        .appendQueryParameter("pn", "BLACKVAULT")
        .appendQueryParameter("am", amount)
        .appendQueryParameter("cu", "USD")
        .appendQueryParameter("tn", "BLACKVAULT")
        .build()

    fun openUpi(context: Context, vpa: String, amount: String = "10000"): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, buildUri(vpa, amount))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        } catch (e: Exception) {
            false
        }
    }
}
