package com.example.ui.viewmodel

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.BreathingExercise
import com.example.data.model.BreathingPhase
import com.example.data.model.PracticeSession
import com.example.data.repository.BreathworkRepository
import com.example.ui.components.DailyPracticeStat
import com.example.util.AudioChimePlayer
import com.example.util.HapticFeedbackHelper
import com.example.util.JalaliCalendarHelper
import com.example.util.JalaliDate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.ceil

data class BreathSessionUiState(
    val selectedExercise: BreathingExercise = BreathingExercise.PRESETS[0],
    val isSessionRunning: Boolean = false,
    val isPaused: Boolean = false,
    val currentPhase: BreathingPhase = BreathingPhase.PREPARE,
    val phaseTimeRemainingSec: Int = 3,
    val phaseProgress: Float = 0f, // 0.0 to 1.0 within current phase
    val currentCycle: Int = 1,
    val totalCycles: Int = 5,
    val sessionElapsedSeconds: Int = 0,
    val sessionTotalSeconds: Int = 0,
    val sessionRemainingSeconds: Int = 0,
    val isSoundEnabled: Boolean = true,
    val isHapticsEnabled: Boolean = true,
    val sessionJustCompleted: Boolean = false
) {
    fun getFormattedElapsed(): String {
        val m = sessionElapsedSeconds / 60
        val s = sessionElapsedSeconds % 60
        return String.format("%02d:%02d", m, s)
    }

    fun getFormattedRemaining(): String {
        val m = sessionRemainingSeconds / 60
        val s = sessionRemainingSeconds % 60
        return String.format("%02d:%02d", m, s)
    }

    fun getFormattedTotal(): String {
        val m = sessionTotalSeconds / 60
        val s = sessionTotalSeconds % 60
        return if (s == 0) "$m دقیقه" else String.format("%02d:%02d", m, s)
    }
}

class BreathworkViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = BreathworkRepository(database.sessionDao(), database.customExerciseDao())

    val audioPlayer = AudioChimePlayer()
    val hapticHelper = HapticFeedbackHelper(application)

    private val initialExercise = BreathingExercise.PRESETS[0]

    private val _sessionState = MutableStateFlow(
        BreathSessionUiState(
            selectedExercise = initialExercise,
            totalCycles = initialExercise.defaultCycles,
            sessionTotalSeconds = initialExercise.totalDurationSeconds,
            sessionRemainingSeconds = initialExercise.totalDurationSeconds
        )
    )
    val sessionState: StateFlow<BreathSessionUiState> = _sessionState.asStateFlow()

    val customExercises: StateFlow<List<BreathingExercise>> = repository.customExercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSessions: StateFlow<List<PracticeSession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalDurationSeconds: StateFlow<Int> = repository.totalDurationSeconds
        .combine(MutableStateFlow(0)) { total, _ -> total ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalSessionsCount: StateFlow<Int> = repository.totalSessionsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private var timerJob: Job? = null
    private var sessionStartTimeMs: Long = 0
    private var totalPausedDurationMs: Long = 0
    private var pauseStartMs: Long = 0

    init {
        audioPlayer.isSoundEnabled = true
        hapticHelper.isHapticsEnabled = true
    }

    fun selectExercise(exercise: BreathingExercise) {
        if (_sessionState.value.isSessionRunning) {
            stopSession(saveIfPracticed = false)
        }
        _sessionState.update {
            it.copy(
                selectedExercise = exercise,
                totalCycles = exercise.defaultCycles,
                currentCycle = 1,
                currentPhase = BreathingPhase.PREPARE,
                phaseProgress = 0f,
                phaseTimeRemainingSec = 3,
                sessionElapsedSeconds = 0,
                sessionTotalSeconds = exercise.totalDurationSeconds,
                sessionRemainingSeconds = exercise.totalDurationSeconds,
                isSessionRunning = false,
                isPaused = false,
                sessionJustCompleted = false
            )
        }
    }

    fun startSession(exercise: BreathingExercise? = null) {
        if (exercise != null) {
            selectExercise(exercise)
        }

        timerJob?.cancel()
        val currentEx = _sessionState.value.selectedExercise

        sessionStartTimeMs = SystemClock.elapsedRealtime()
        totalPausedDurationMs = 0
        pauseStartMs = 0

        _sessionState.update {
            it.copy(
                isSessionRunning = true,
                isPaused = false,
                currentCycle = 1,
                currentPhase = BreathingPhase.PREPARE,
                phaseTimeRemainingSec = 3,
                phaseProgress = 0f,
                sessionElapsedSeconds = 0,
                sessionTotalSeconds = currentEx.totalDurationSeconds,
                sessionRemainingSeconds = currentEx.totalDurationSeconds,
                sessionJustCompleted = false
            )
        }

        runSessionLoop(currentEx)
    }

    fun pauseSession() {
        if (!_sessionState.value.isPaused) {
            pauseStartMs = SystemClock.elapsedRealtime()
            _sessionState.update { it.copy(isPaused = true) }
        }
    }

    fun resumeSession() {
        if (_sessionState.value.isPaused) {
            if (pauseStartMs > 0) {
                totalPausedDurationMs += (SystemClock.elapsedRealtime() - pauseStartMs)
                pauseStartMs = 0
            }
            _sessionState.update { it.copy(isPaused = false) }
        }
    }

    fun stopSession(saveIfPracticed: Boolean = true) {
        timerJob?.cancel()
        val state = _sessionState.value

        if (saveIfPracticed && state.sessionElapsedSeconds >= 10) {
            recordCompletedSession(
                exercise = state.selectedExercise,
                durationSeconds = state.sessionElapsedSeconds,
                completedCycles = state.currentCycle
            )
        }

        _sessionState.update {
            it.copy(
                isSessionRunning = false,
                isPaused = false,
                currentPhase = BreathingPhase.PREPARE,
                phaseProgress = 0f,
                phaseTimeRemainingSec = 3
            )
        }
    }

    fun toggleSound() {
        val next = !_sessionState.value.isSoundEnabled
        audioPlayer.isSoundEnabled = next
        _sessionState.update { it.copy(isSoundEnabled = next) }
    }

    fun toggleHaptics() {
        val next = !_sessionState.value.isHapticsEnabled
        hapticHelper.isHapticsEnabled = next
        _sessionState.update { it.copy(isHapticsEnabled = next) }
    }

    fun saveCustomExercise(exercise: BreathingExercise) {
        viewModelScope.launch {
            repository.saveCustomExercise(exercise)
            selectExercise(exercise)
        }
    }

    fun deleteCustomExercise(exercise: BreathingExercise) {
        viewModelScope.launch {
            repository.deleteCustomExercise(exercise)
            if (_sessionState.value.selectedExercise.id == exercise.id) {
                selectExercise(BreathingExercise.PRESETS[0])
            }
        }
    }

    fun dismissSessionCompletedDialog() {
        _sessionState.update { it.copy(sessionJustCompleted = false) }
    }

    private fun runSessionLoop(exercise: BreathingExercise) {
        timerJob = viewModelScope.launch {
            // Step 1: Preparation countdown (3 seconds)
            executePreparation(3000L)

            // Step 2: Cycles loop
            val totalCycles = exercise.defaultCycles
            for (cycle in 1..totalCycles) {
                if (!isActive) break
                _sessionState.update { it.copy(currentCycle = cycle) }

                // 1. Inhale (دم)
                if (exercise.inhaleSeconds > 0) {
                    executePhase(BreathingPhase.INHALE, exercise.inhaleSeconds)
                }

                // 2. Hold after Inhale (حبس دم)
                if (exercise.holdInSeconds > 0 && isActive) {
                    executePhase(BreathingPhase.HOLD_IN, exercise.holdInSeconds)
                }

                // 3. Exhale (بازدم)
                if (exercise.exhaleSeconds > 0 && isActive) {
                    executePhase(BreathingPhase.EXHALE, exercise.exhaleSeconds)
                }

                // 4. Hold after Exhale (حبس بازدم)
                if (exercise.holdOutSeconds > 0 && isActive) {
                    executePhase(BreathingPhase.HOLD_OUT, exercise.holdOutSeconds)
                }
            }

            if (isActive) {
                // Completed session!
                audioPlayer.playChimeForPhase("COMPLETE")
                hapticHelper.vibrateForPhase("COMPLETE")

                val finalElapsed = _sessionState.value.sessionElapsedSeconds
                recordCompletedSession(
                    exercise = exercise,
                    durationSeconds = if (finalElapsed > 0) finalElapsed else exercise.totalDurationSeconds,
                    completedCycles = totalCycles
                )

                _sessionState.update {
                    it.copy(
                        isSessionRunning = false,
                        sessionJustCompleted = true,
                        currentPhase = BreathingPhase.PREPARE,
                        phaseProgress = 1f
                    )
                }
            }
        }
    }

    private suspend fun executePreparation(prepDurationMs: Long) {
        audioPlayer.playChimeForPhase("PREPARE")
        hapticHelper.vibrateForPhase("PREPARE")

        val start = SystemClock.elapsedRealtime()
        var pausedTotal = 0L

        while (true) {
            if (_sessionState.value.isPaused) {
                val pStart = SystemClock.elapsedRealtime()
                while (_sessionState.value.isPaused && viewModelScope.isActive) {
                    delay(50)
                }
                pausedTotal += (SystemClock.elapsedRealtime() - pStart)
            }

            val now = SystemClock.elapsedRealtime()
            val elapsed = (now - start - pausedTotal).coerceAtLeast(0)
            if (elapsed >= prepDurationMs) break

            val remainingSec = ceil((prepDurationMs - elapsed) / 1000.0).toInt().coerceAtLeast(1)
            val progress = (elapsed.toFloat() / prepDurationMs).coerceIn(0f, 1f)

            _sessionState.update {
                it.copy(
                    currentPhase = BreathingPhase.PREPARE,
                    phaseTimeRemainingSec = remainingSec,
                    phaseProgress = progress
                )
            }
            delay(25)
        }
    }

    /**
     * Executes a single phase with steady wall-clock time measurement.
     * Prevents any jitter, pause, or drift when audio/haptics play!
     */
    private suspend fun executePhase(phase: BreathingPhase, durationSeconds: Int) {
        val phaseDurationMs = durationSeconds * 1000L

        // Immediately update state and fire cues asynchronously
        _sessionState.update {
            it.copy(
                currentPhase = phase,
                phaseTimeRemainingSec = durationSeconds,
                phaseProgress = 0f
            )
        }

        audioPlayer.playChimeForPhase(phase.name)
        hapticHelper.vibrateForPhase(phase.name)

        val phaseStart = SystemClock.elapsedRealtime()
        var phasePausedMs = 0L

        while (true) {
            if (_sessionState.value.isPaused) {
                val pStart = SystemClock.elapsedRealtime()
                while (_sessionState.value.isPaused && viewModelScope.isActive) {
                    delay(50)
                }
                phasePausedMs += (SystemClock.elapsedRealtime() - pStart)
            }

            val now = SystemClock.elapsedRealtime()
            val phaseElapsed = (now - phaseStart - phasePausedMs).coerceAtLeast(0)

            // Overall session elapsed time calculation
            val overallElapsedMs = (now - sessionStartTimeMs - totalPausedDurationMs).coerceAtLeast(0)
            val overallElapsedSec = (overallElapsedMs / 1000).toInt()
            val totalSec = _sessionState.value.sessionTotalSeconds
            val overallRemainingSec = (totalSec - overallElapsedSec).coerceAtLeast(0)

            if (phaseElapsed >= phaseDurationMs) {
                _sessionState.update {
                    it.copy(
                        phaseTimeRemainingSec = 0,
                        phaseProgress = 1f,
                        sessionElapsedSeconds = overallElapsedSec,
                        sessionRemainingSeconds = overallRemainingSec
                    )
                }
                break
            }

            val remSeconds = ceil((phaseDurationMs - phaseElapsed) / 1000.0).toInt().coerceAtLeast(0)
            val progress = (phaseElapsed.toFloat() / phaseDurationMs).coerceIn(0f, 1f)

            _sessionState.update {
                it.copy(
                    phaseTimeRemainingSec = remSeconds,
                    phaseProgress = progress,
                    sessionElapsedSeconds = overallElapsedSec,
                    sessionRemainingSeconds = overallRemainingSec
                )
            }
            delay(25)
        }
    }

    private fun recordCompletedSession(
        exercise: BreathingExercise,
        durationSeconds: Int,
        completedCycles: Int
    ) {
        viewModelScope.launch {
            val nowJalali = JalaliCalendarHelper.now()
            val session = PracticeSession(
                exerciseKey = exercise.uniqueKey,
                exerciseTitle = exercise.title,
                durationSeconds = durationSeconds,
                completedCycles = completedCycles,
                jalaliDate = nowJalali.formattedShort,
                jalaliMonthKey = nowJalali.monthKey
            )
            repository.recordSession(session)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }

    // Helper functions for stats
    fun calculateCurrentStreak(sessions: List<PracticeSession>): Int {
        val distinctDates = sessions.map { it.jalaliDate }.toSet()
        return JalaliCalendarHelper.calculateStreak(distinctDates)
    }

    fun getWeeklyStats(sessions: List<PracticeSession>): List<DailyPracticeStat> {
        val now = JalaliCalendarHelper.now()
        val todayDow = now.dayOfWeek
        val weekNames = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")

        val result = mutableListOf<DailyPracticeStat>()
        for (dow in 0..6) {
            val daysDiff = dow - todayDow
            val targetDate = addDaysToJalali(now, daysDiff)
            val dateKey = targetDate.formattedShort

            val minutes = sessions
                .filter { it.jalaliDate == dateKey }
                .sumOf { it.durationSeconds } / 60

            result.add(
                DailyPracticeStat(
                    dayName = weekNames[dow],
                    minutes = minutes,
                    isToday = (dow == todayDow)
                )
            )
        }
        return result
    }

    private fun addDaysToJalali(date: JalaliDate, days: Int): JalaliDate {
        val (gY, gM, gD) = JalaliCalendarHelper.jalaliToGregorian(date.year, date.month, date.day)
        val cal = java.util.Calendar.getInstance(JalaliCalendarHelper.tehranTimeZone)
        cal.set(gY, gM - 1, gD)
        cal.add(java.util.Calendar.DAY_OF_MONTH, days)
        return JalaliCalendarHelper.fromTimestamp(cal.timeInMillis)
    }
}
