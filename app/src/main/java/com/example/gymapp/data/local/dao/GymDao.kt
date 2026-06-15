package com.example.gymapp.data.local.dao

import androidx.room.*
import com.example.gymapp.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface GymDao {

    // --- SŁOWNIK ĆWICZEŃ ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>): List<Long> // Dodano : List<Long>

    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :searchQuery || '%' OR muscleGroup LIKE '%' || :searchQuery || '%'")
    fun searchExercises(searchQuery: String): Flow<List<ExerciseEntity>>

    // --- PLANY TRENINGOWE ---
    @Insert
    suspend fun insertWorkoutPlan(plan: WorkoutPlanEntity): Long

    @Delete
    suspend fun deleteWorkoutPlan(plan: WorkoutPlanEntity): Int // Dodano : Int

    @Query("SELECT * FROM workout_plans")
    fun getAllWorkoutPlans(): Flow<List<WorkoutPlanEntity>>

    @Insert
    suspend fun insertPlanExercise(planExercise: PlanExerciseEntity): Long

    @Insert
    suspend fun insertPlanSets(sets: List<PlanSetEntity>): List<Long> // Dodano : List<Long>

    // --- SESJE TRENINGOWE (HISTORIA I STATYSTYKI) ---
    @Insert
    suspend fun insertWorkoutSession(session: WorkoutSessionEntity): Long

    @Insert
    suspend fun insertPerformedSets(sets: List<PerformedSetEntity>): List<Long> // Dodano : List<Long>

    @Query("SELECT * FROM workout_sessions ORDER BY dateTimestamp DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("""
        SELECT performed_sets.* FROM performed_sets 
        INNER JOIN workout_sessions ON performed_sets.sessionId = workout_sessions.id
        WHERE performed_sets.exerciseId = :exerciseId 
        ORDER BY workout_sessions.dateTimestamp ASC
    """)
    fun getExerciseHistoryForStats(exerciseId: Int): Flow<List<PerformedSetEntity>>
}