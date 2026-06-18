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
    @Query("SELECT * FROM workout_plans WHERE id = :planId")
    suspend fun getWorkoutPlanById(planId: Int): WorkoutPlanEntity?

    @Query("SELECT * FROM plan_exercises WHERE planId = :planId")
    suspend fun getPlanExercisesByPlanId(planId: Int): List<PlanExerciseEntity>

    @Query("SELECT * FROM plan_sets WHERE planExerciseId IN (:planExerciseIds)")
    suspend fun getPlanSetsByExerciseIds(planExerciseIds: List<Int>): List<PlanSetEntity>

    @Update
    suspend fun updateWorkoutPlan(plan: WorkoutPlanEntity): Int // Dodano: Int

    @Query("DELETE FROM plan_exercises WHERE planId = :planId")
    suspend fun deletePlanExercisesByPlanId(planId: Int): Int // Dodano: Int
    @Insert
    suspend fun insertScheduledWorkout(scheduledWorkout: ScheduledWorkoutEntity): Long

    @Query("SELECT * FROM scheduled_workouts")
    fun getAllScheduledWorkouts(): Flow<List<ScheduledWorkoutEntity>>

    @Query("DELETE FROM scheduled_workouts WHERE id = :scheduleId")
    suspend fun deleteScheduledWorkout(scheduleId: Int): Int

    @Query("SELECT * FROM performed_sets")
    fun getAllPerformedSets(): Flow<List<PerformedSetEntity>>

    @Insert
    suspend fun insertProgressPhoto(photo: ProgressPhotoEntity): Long

    @Query("SELECT * FROM progress_photos ORDER BY dateTimestamp DESC")
    fun getAllProgressPhotos(): Flow<List<ProgressPhotoEntity>>
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserEntity?>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(user: UserEntity): Long

    @Query("DELETE FROM workout_plans")
    suspend fun clearAllWorkoutPlans(): Int // <-- Dodane : Int

    @Query("DELETE FROM workout_sessions")
    suspend fun clearAllSessions(): Int // <-- Dodane : Int

    @Query("DELETE FROM scheduled_workouts")
    suspend fun clearAllScheduled(): Int // <-- Dodane : Int

    @Query("DELETE FROM progress_photos")
    suspend fun clearAllPhotos(): Int // <-- Dodane : Int

    @Query("DELETE FROM user_profile")
    suspend fun clearUserProfile(): Int // <-- Dodane : Int

    @Update
    suspend fun updateProgressPhoto(photo: ProgressPhotoEntity): Int
}