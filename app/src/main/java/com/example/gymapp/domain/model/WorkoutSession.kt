package com.example.gymapp.domain.model

data class WorkoutSession(
    val id: Int = 0,
    val planId: Int,
    val dateTimestamp: Long, // Data wykonania (do kalendarza)
    val durationSeconds: Int // Czas ze stopera na Ekranie Treningu
)