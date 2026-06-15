package com.example.gymapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.repository.GymRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: GymRepository) : ViewModel() {

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled = _notificationsEnabled.asStateFlow()

    private val _keepScreenOn = MutableStateFlow(true)
    val keepScreenOn = _keepScreenOn.asStateFlow()

    private val _vibrationsEnabled = MutableStateFlow(true)
    val vibrationsEnabled = _vibrationsEnabled.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) { _isDarkMode.value = enabled }
    fun toggleNotifications(enabled: Boolean) { _notificationsEnabled.value = enabled }
    fun toggleKeepScreenOn(enabled: Boolean) { _keepScreenOn.value = enabled }
    fun toggleVibrations(enabled: Boolean) { _vibrationsEnabled.value = enabled }

    fun resetAllUserData() {
        viewModelScope.launch {
            repository.clearAllUserData()
        }
    }
}