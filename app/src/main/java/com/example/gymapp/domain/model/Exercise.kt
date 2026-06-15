package com.example.gymapp.domain.model

data class Exercise(
    val id: Int = 0,
    val name: String,
    val muscleGroup: String // np. "Chest", "Legs", "Back"
)