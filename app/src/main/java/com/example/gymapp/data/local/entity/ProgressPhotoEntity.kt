package com.example.gymapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress_photos")
data class ProgressPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val filePath: String,     // Ścieżka do prywatnego pliku aplikacji
    val dateTimestamp: Long,  // Kiedy dodano
    val weight: Double?,       // Opcjonalna waga przy dodawaniu
    val description: String? = null
)