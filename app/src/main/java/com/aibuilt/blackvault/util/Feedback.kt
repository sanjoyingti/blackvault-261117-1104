package com.aibuilt.blackvault.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object Feedback {

    fun pulse(context: Context, durationMs: Long = 40, tone: Boolean = true) {
        if (tone) {
            beep()
        }
        vibrate(context, durationMs)
    }

    private fun beep() {
        try {
            val generator = ToneGenerator(AudioManager.STREAM_MUSIC, 55)
            generator.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
            Handler(Looper.getMainLooper()).postDelayed({
                runCatching { generator.release() }
            }, 240L)
        } catch (t: Throwable) {
            // device without a tone generator; ignore
        }
    }

    fun vibrate(context: Context, durationMs: Long = 40) {
        try {
            val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            }
        } catch (t: Throwable) {
            // vibration not available; ignore
        }
    }
}
