package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class AudioChimePlayer {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val sampleRate = 22050 // Lightweight and high fidelity for pure sine chimes

    var isSoundEnabled: Boolean = true

    // Pre-allocated tracks for each phase
    private val tracks = mutableMapOf<String, AudioTrack>()

    init {
        scope.launch {
            try {
                preloadTrack("INHALE", generateToneBuffer(528.0, 600, 50))
                preloadTrack("HOLD_IN", generateToneBuffer(639.0, 450, 30))
                preloadTrack("EXHALE", generateToneBuffer(432.0, 750, 60))
                preloadTrack("HOLD_OUT", generateToneBuffer(396.0, 400, 30))
                preloadTrack("PREPARE", generateToneBuffer(528.0, 350, 40))
                preloadTrack("COMPLETE", generateChordBuffer(1100))
            } catch (e: Exception) {
                // Ignore initialization failures gracefully
            }
        }
    }

    private fun preloadTrack(key: String, buffer: ShortArray) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            synchronized(tracks) {
                tracks[key] = track
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    /**
     * Plays the preloaded chime instantly without any allocation or delay
     */
    fun playChimeForPhase(phaseName: String) {
        if (!isSoundEnabled) return

        scope.launch {
            try {
                val track = synchronized(tracks) { tracks[phaseName] }
                if (track != null && track.state == AudioTrack.STATE_INITIALIZED) {
                    track.stop()
                    track.setPlaybackHeadPosition(0)
                    track.play()
                }
            } catch (e: Exception) {
                // Non-blocking fail-safe
            }
        }
    }

    private fun generateToneBuffer(frequency: Double, durationMs: Int, attackMs: Int): ShortArray {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        val attackSamples = (sampleRate * (attackMs / 1000.0)).toInt().coerceAtLeast(1)
        val decayConstant = 3.6 / numSamples

        for (i in 0 until numSamples) {
            val time = i.toDouble() / sampleRate
            val fundamental = sin(2.0 * PI * frequency * time)
            val overtone = 0.3 * sin(2.0 * PI * (frequency * 2.01) * time)
            val wave = fundamental + overtone

            val envelope = if (i < attackSamples) {
                i.toDouble() / attackSamples
            } else {
                exp(-decayConstant * (i - attackSamples))
            }

            val sampleVal = (wave * envelope * 0.45 * Short.MAX_VALUE).toInt()
            buffer[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateChordBuffer(durationMs: Int): ShortArray {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        val decayConstant = 3.0 / numSamples

        val f1 = 432.0
        val f2 = 540.0
        val f3 = 648.0

        for (i in 0 until numSamples) {
            val time = i.toDouble() / sampleRate
            val wave = (0.4 * sin(2.0 * PI * f1 * time) +
                    0.3 * sin(2.0 * PI * f2 * time) +
                    0.3 * sin(2.0 * PI * f3 * time))

            val attack = (i.toDouble() / (sampleRate * 0.06)).coerceAtMost(1.0)
            val decay = exp(-decayConstant * i)
            val sampleVal = (wave * attack * decay * 0.5 * Short.MAX_VALUE).toInt()
            buffer[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    fun release() {
        synchronized(tracks) {
            tracks.values.forEach {
                try {
                    it.stop()
                    it.release()
                } catch (e: Exception) {
                    // ignore
                }
            }
            tracks.clear()
        }
    }
}
