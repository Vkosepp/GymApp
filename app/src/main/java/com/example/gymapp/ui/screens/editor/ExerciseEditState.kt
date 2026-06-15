package com.example.gymapp.ui.screens.editor

import com.example.gymapp.data.local.entity.ExerciseEntity

// Trzymamy dane jako Stringi, żeby Jetpack Compose łatwo radził sobie z polami tekstowymi
data class ExerciseEditState(
    val exercise: ExerciseEntity,
    val sets: String = "3",
    val reps: String = "12",
    val weight: String = "0.0",
    val tempoEccentric: String = "2",
    val tempoIsometric: String = "1",
    val tempoConcentric: String = "1"
)