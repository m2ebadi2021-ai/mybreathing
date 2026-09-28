package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PracticeSession
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM practice_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<PracticeSession>>

    @Query("SELECT * FROM practice_sessions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<PracticeSession>>

    @Query("SELECT * FROM practice_sessions WHERE jalaliMonthKey = :monthKey")
    fun getSessionsForJalaliMonth(monthKey: String): Flow<List<PracticeSession>>

    @Query("SELECT * FROM practice_sessions WHERE jalaliDate = :dateStr")
    suspend fun getSessionsForDate(dateStr: String): List<PracticeSession>

    @Query("SELECT SUM(durationSeconds) FROM practice_sessions")
    fun getTotalDurationSeconds(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM practice_sessions")
    fun getTotalSessionsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PracticeSession): Long

    @Query("DELETE FROM practice_sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)
}
