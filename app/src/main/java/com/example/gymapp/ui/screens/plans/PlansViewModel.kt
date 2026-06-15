package com.example.gymapp.ui.screens.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.local.entity.WorkoutPlanEntity
import com.example.gymapp.data.repository.GymRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlansViewModel(private val repository: GymRepository) : ViewModel() {

    val workoutPlans: StateFlow<List<WorkoutPlanEntity>> = repository.getAllWorkoutPlans()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deletePlan(plan: WorkoutPlanEntity) {
        viewModelScope.launch {
            repository.deleteWorkoutPlan(plan)
        }
    }
}