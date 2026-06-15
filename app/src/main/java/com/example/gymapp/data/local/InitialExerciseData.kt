package com.example.gymapp.data.local

import com.example.gymapp.data.local.entity.ExerciseEntity

object InitialExerciseData {
    val exercises = listOf(
        // Klatka piersiowa (Front chest)
        ExerciseEntity(name = "Barbell Bench Press", muscleGroup = "Front chest"),
        ExerciseEntity(name = "Incline Dumbbell Press", muscleGroup = "Front chest"),
        ExerciseEntity(name = "Decline Barbell Press", muscleGroup = "Front chest"),
        ExerciseEntity(name = "Chest Flyes (Dumbbell)", muscleGroup = "Front chest"),
        ExerciseEntity(name = "Cable Crossover", muscleGroup = "Front chest"),
        ExerciseEntity(name = "Push-ups", muscleGroup = "Front chest"),
        ExerciseEntity(name = "Flat bench press", muscleGroup = "Front chest"),
        ExerciseEntity(name = "Incline bench press", muscleGroup = "Front chest"),
        ExerciseEntity(name = "Dips (Chest focus)", muscleGroup = "Front chest"),

        // Plecy (Back)
        ExerciseEntity(name = "Deadlift", muscleGroup = "Back"),
        ExerciseEntity(name = "Pull-ups", muscleGroup = "Back"),
        ExerciseEntity(name = "Wide-grip lat pulldown", muscleGroup = "Back"),
        ExerciseEntity(name = "Barbell Row", muscleGroup = "Back"),
        ExerciseEntity(name = "Dumbbell Row", muscleGroup = "Back"),
        ExerciseEntity(name = "Close-grip seated cable row", muscleGroup = "Back"),
        ExerciseEntity(name = "Hyperextensions", muscleGroup = "Back"),

        // Nogi (Legs)
        ExerciseEntity(name = "Barbell back squat", muscleGroup = "Legs"),
        ExerciseEntity(name = "Barbell front squat", muscleGroup = "Legs"),
        ExerciseEntity(name = "Leg press", muscleGroup = "Legs"),
        ExerciseEntity(name = "Bulgarian split squats", muscleGroup = "Legs"),
        ExerciseEntity(name = "Glute ham raise", muscleGroup = "Legs"),
        ExerciseEntity(name = "Abductor machine", muscleGroup = "Legs"),
        ExerciseEntity(name = "Barbell hip thrust", muscleGroup = "Legs"),
        ExerciseEntity(name = "Cable Pull-Through", muscleGroup = "Legs"),
        ExerciseEntity(name = "Calf raise", muscleGroup = "Legs"),
        ExerciseEntity(name = "Goblet squat", muscleGroup = "Legs"),
        ExerciseEntity(name = "Good morning", muscleGroup = "Legs"),

        // Barki (Shoulders)
        ExerciseEntity(name = "Overhead press", muscleGroup = "Shoulders"),
        ExerciseEntity(name = "Dumbbell Shoulder Press", muscleGroup = "Shoulders"),
        ExerciseEntity(name = "Lateral Raise (Dumbbell)", muscleGroup = "Shoulders"),
        ExerciseEntity(name = "Front Raise", muscleGroup = "Shoulders"),
        ExerciseEntity(name = "Rear Delt Fly", muscleGroup = "Shoulders"),

        // Ramiona (Arms - Biceps/Triceps)
        ExerciseEntity(name = "Biceps hammer curls", muscleGroup = "Arms"),
        ExerciseEntity(name = "Barbell Curl", muscleGroup = "Arms"),
        ExerciseEntity(name = "Triceps cable pushdown", muscleGroup = "Arms"),
        ExerciseEntity(name = "Skull Crushers", muscleGroup = "Arms"),

        // Brzuch i inne (Core / Stretching)
        ExerciseEntity(name = "Ab crunches", muscleGroup = "Core"),
        ExerciseEntity(name = "Plank", muscleGroup = "Core"),
        ExerciseEntity(name = "Hanging Leg Raise", muscleGroup = "Core"),
        ExerciseEntity(name = "Treadmill", muscleGroup = "Cardio"),
        ExerciseEntity(name = "Stretching", muscleGroup = "Stretching")
    )
}