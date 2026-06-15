package com.example.gymapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "plan_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("planId"), Index("exerciseId")]
)
data class PlanExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val planId: Int,
    val exerciseId: Int,
    val isAdvanced: Boolean,
    val eccentricTempo: Int,
    val isometricTempo: Int,
    val concentricTempo: Int
)