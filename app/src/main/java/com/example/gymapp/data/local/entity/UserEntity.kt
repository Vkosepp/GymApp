package com.example.gymapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Nowy Użytkownik",
    val age: Int = 0,
    val height: Int = 0,
    val avatarPath: String? = null
)