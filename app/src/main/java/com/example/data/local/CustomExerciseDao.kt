package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BreathingExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomExerciseDao {
    @Query("SELECT * FROM custom_exercises ORDER BY id DESC")
    fun getAllCustomExercises(): Flow<List<BreathingExercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: BreathingExercise): Long

    @Delete
    suspend fun deleteExercise(exercise: BreathingExercise)

    @Query("DELETE FROM custom_exercises WHERE id = :id")
    suspend fun deleteExerciseById(id: Long)
}
