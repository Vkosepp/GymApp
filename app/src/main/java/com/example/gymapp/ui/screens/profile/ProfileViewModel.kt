package com.example.gymapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.local.entity.ProgressPhotoEntity
import com.example.gymapp.data.local.entity.UserEntity
import com.example.gymapp.data.repository.GymRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ProfileViewModel(private val repository: GymRepository) : ViewModel() {

    // Pobieranie profilu użytkownika. Jeśli nie istnieje (apka odpalona pierwszy raz), zwraca null
    val userProfile = repository.getUserProfile().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    // Galeria zostaje bez zmian
    val groupedPhotos: StateFlow<Map<String, List<ProgressPhotoEntity>>> = repository.getAllProgressPhotos()
        .map { photos ->
            val formatter = DateTimeFormatter.ofPattern("LLLL yyyy").withZone(ZoneId.systemDefault())
            photos.groupBy { photo ->
                val instant = Instant.ofEpochMilli(photo.dateTimestamp)
                formatter.format(instant).replaceFirstChar { it.uppercase() }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun addProgressPhoto(filePath: String, weightInput: String) {
        viewModelScope.launch {
            val weight = weightInput.toDoubleOrNull()
            repository.insertProgressPhoto(
                ProgressPhotoEntity(filePath = filePath, dateTimestamp = System.currentTimeMillis(), weight = weight)
            )
        }
    }

    // Aktualizacja całego profilu lub samego awatara
    fun updateProfile(name: String, age: Int, height: Int, avatarPath: String?) {
        viewModelScope.launch {
            val currentAvatar = userProfile.value?.avatarPath
            repository.upsertUserProfile(
                UserEntity(
                    id = 1,
                    name = name,
                    age = age,
                    height = height,
                    avatarPath = avatarPath ?: currentAvatar // Zatrzymujemy stare zdjęcie, jeśli nie podano nowego
                )
            )
        }
    }
}