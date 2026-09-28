package ir.taravatgroup.aramesh.data.repository

import ir.taravatgroup.aramesh.data.local.CustomExerciseDao
import ir.taravatgroup.aramesh.data.local.SessionDao
import ir.taravatgroup.aramesh.data.model.BreathingExercise
import ir.taravatgroup.aramesh.data.model.PracticeSession
import kotlinx.coroutines.flow.Flow

class BreathworkRepository(
    private val sessionDao: SessionDao,
    private val customExerciseDao: CustomExerciseDao
) {
    val allSessions: Flow<List<PracticeSession>> = sessionDao.getAllSessions()
    val recentSessions: Flow<List<PracticeSession>> = sessionDao.getRecentSessions(10)
    val totalDurationSeconds: Flow<Int?> = sessionDao.getTotalDurationSeconds()
    val totalSessionsCount: Flow<Int> = sessionDao.getTotalSessionsCount()
    val customExercises: Flow<List<BreathingExercise>> = customExerciseDao.getAllCustomExercises()

    fun getSessionsForMonth(monthKey: String): Flow<List<PracticeSession>> {
        return sessionDao.getSessionsForJalaliMonth(monthKey)
    }

    suspend fun recordSession(session: PracticeSession): Long {
        return sessionDao.insertSession(session)
    }

    suspend fun saveCustomExercise(exercise: BreathingExercise): Long {
        return customExerciseDao.insertExercise(exercise)
    }

    suspend fun deleteCustomExercise(exercise: BreathingExercise) {
        customExerciseDao.deleteExercise(exercise)
    }

    suspend fun deleteCustomExerciseById(id: Long) {
        customExerciseDao.deleteExerciseById(id)
    }
}
