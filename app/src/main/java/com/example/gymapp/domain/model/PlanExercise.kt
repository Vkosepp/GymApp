package com.example.gymapp.domain.model

data class PlanExercise(
    val id: Int = 0,
    val planId: Int, // Powiązanie z WorkoutPlan
    val exerciseId: Int, // Powiązanie z bazą ćwiczeń
    val isAdvanced: Boolean = false,

    // Opcja BASIC
    val sets: Int = 3,
    val reps: Int = 12,
    val weight: Double = 0.0,

    // Opcja ADVANCED (czas podnoszenia, opuszczania, przytrzymania w sekundach)
    val eccentricTempo: Int = 2, // opadanie
    val isometricTempo: Int = 1, // przytrzymanie
    val concentricTempo: Int = 1 // wznoszenie
    // W przyszłości dla Advanced można tu dodać osobną listę dla każdej pojedynczej serii (Set)
)