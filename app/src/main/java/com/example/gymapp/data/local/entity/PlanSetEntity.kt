package com.example.gymapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "plan_sets",
    foreignKeys = [
        ForeignKey(
            entity = PlanExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["planExerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("planExerciseId")]
)
data class PlanSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val planExerciseId: Int,
    val setNumber: Int,
    val reps: Int,
    val weight: Double
)