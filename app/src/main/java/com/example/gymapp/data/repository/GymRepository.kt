package com.example.gymapp.data.repository

import com.example.gymapp.data.local.dao.GymDao
import com.example.gymapp.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class GymRepository(private val dao: GymDao) {

    // --- ĆWICZENIA ---
    fun getAllExercises(): Flow<List<ExerciseEntity>> {
        return dao.getAllExercises()
    }

    fun searchExercises(query: String): Flow<List<ExerciseEntity>> {
        return dao.searchExercises(query)
    }

    // --- PLANY TRENINGOWE ---
    fun getAllWorkoutPlans(): Flow<List<WorkoutPlanEntity>> {
        return dao.getAllWorkoutPlans()
    }

    suspend fun insertWorkoutPlan(plan: WorkoutPlanEntity): Long {
        return dao.insertWorkoutPlan(plan)
    }

    suspend fun deleteWorkoutPlan(plan: WorkoutPlanEntity) {
        dao.deleteWorkoutPlan(plan)
    }

    suspend fun insertPlanExercise(planExercise: PlanExerciseEntity): Long {
        return dao.insertPlanExercise(planExercise)
    }

    suspend fun insertPlanSets(sets: List<PlanSetEntity>) {
        dao.insertPlanSets(sets)
    }

    // --- SESJE TRENINGOWE (HISTORIA) ---
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>> {
        return dao.getAllSessions()
    }

    suspend fun insertWorkoutSession(session: WorkoutSessionEntity): Long {
        return dao.insertWorkoutSession(session)
    }

    suspend fun insertPerformedSets(sets: List<PerformedSetEntity>) {
        dao.insertPerformedSets(sets)
    }

    fun getExerciseHistoryForStats(exerciseId: Int): Flow<List<PerformedSetEntity>> {
        return dao.getExerciseHistoryForStats(exerciseId)
    }
}