package com.example.util

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticFeedbackHelper(private val context: Context) {
    var isHapticsEnabled: Boolean = true

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun vibrateForPhase(phaseName: String) {
        if (!isHapticsEnabled) return
        val v = vibrator ?: return
        if (!v.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                when (phaseName) {
                    "INHALE" -> {
                        // Rising double pulse
                        val timings = longArrayOf(0, 70, 60, 110)
                        val amplitudes = intArrayOf(0, 120, 0, 190)
                        v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    }
                    "HOLD_IN" -> {
                        // Crisp single tap
                        v.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                    "EXHALE" -> {
                        // Smooth calming exhale pulse
                        val timings = longArrayOf(0, 160)
                        val amplitudes = intArrayOf(0, 100)
                        v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    }
                    "HOLD_OUT" -> {
                        // Gentle subtle tick
                        v.vibrate(VibrationEffect.createOneShot(30, 80))
                    }
                    "COMPLETE" -> {
                        // Celebratory rhythm
                        val timings = longArrayOf(0, 80, 80, 120, 80, 200)
                        val amplitudes = intArrayOf(0, 140, 0, 180, 0, 240)
                        v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    }
                    else -> {
                        v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                when (phaseName) {
                    "INHALE" -> v.vibrate(longArrayOf(0, 70, 60, 110), -1)
                    "HOLD_IN" -> v.vibrate(50)
                    "EXHALE" -> v.vibrate(150)
                    "HOLD_OUT" -> v.vibrate(30)
                    "COMPLETE" -> v.vibrate(longArrayOf(0, 80, 80, 120, 80, 200), -1)
                    else -> v.vibrate(50)
                }
            }
        } catch (e: Exception) {
            // Ignore if device restriction or permission issue
        }
    }
}
