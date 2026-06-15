package com.example.gymapp.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymapp.data.repository.GymRepository
import com.example.gymapp.ui.screens.home.HomeViewModel
import com.example.gymapp.ui.screens.plans.ExerciseSearchViewModel
import com.example.gymapp.ui.screens.plans.PlansViewModel
import com.example.gymapp.ui.screens.editor.PlanEditorViewModel

class GymViewModelFactory(private val repository: GymRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(PlansViewModel::class.java) -> {
                PlansViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ExerciseSearchViewModel::class.java) -> {
                ExerciseSearchViewModel(repository) as T
            }
            modelClass.isAssignableFrom(PlanEditorViewModel::class.java) -> {
                PlanEditorViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}