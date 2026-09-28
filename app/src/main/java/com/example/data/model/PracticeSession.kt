package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "practice_sessions")
data class PracticeSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exerciseKey: String,
    val exerciseTitle: String,
    val durationSeconds: Int,
    val completedCycles: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val jalaliDate: String, // e.g. "1405/07/06"
    val jalaliMonthKey: String // e.g. "1405/07"
)
