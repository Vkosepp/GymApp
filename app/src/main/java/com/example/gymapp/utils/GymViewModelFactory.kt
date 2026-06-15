package com.example.gymapp.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymapp.data.repository.GymRepository
import com.example.gymapp.ui.screens.editor.PlanEditorViewModel
import com.example.gymapp.ui.screens.home.HomeViewModel
import com.example.gymapp.ui.screens.plans.ExerciseSearchViewModel
import com.example.gymapp.ui.screens.plans.PlansViewModel
import com.example.gymapp.ui.screens.profile.ProfileViewModel
import com.example.gymapp.ui.screens.settings.SettingsViewModel
import com.example.gymapp.ui.screens.workout.ActiveWorkoutViewModel // Nowy import
import com.example.gymapp.ui.screens.stats.StatsViewModel

class GymViewModelFactory(private val repository: GymRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository) as T
            modelClass.isAssignableFrom(PlansViewModel::class.java) -> PlansViewModel(repository) as T
            modelClass.isAssignableFrom(ExerciseSearchViewModel::class.java) -> ExerciseSearchViewModel(repository) as T
            modelClass.isAssignableFrom(PlanEditorViewModel::class.java) -> PlanEditorViewModel(repository) as T
            // DODANY ACTIVE WORKOUT:
            modelClass.isAssignableFrom(ActiveWorkoutViewModel::class.java) -> ActiveWorkoutViewModel(repository) as T
            modelClass.isAssignableFrom(StatsViewModel::class.java) -> StatsViewModel(repository) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(repository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}