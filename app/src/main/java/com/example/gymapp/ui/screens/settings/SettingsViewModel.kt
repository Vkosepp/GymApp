package com.example.gymapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.repository.GymRepository
import com.example.gymapp.ui.theme.AppSettingsState
import com.example.gymapp.ui.theme.ThemeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: GymRepository) : ViewModel() {

    val isDarkMode = ThemeState.isDark.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled = _notificationsEnabled.asStateFlow()

    private val _keepScreenOn = MutableStateFlow(true)
    val keepScreenOn = _keepScreenOn.asStateFlow()

    // Czytamy bezpośrednio z globalnego stanu aplikacji
    val vibrationsEnabled = AppSettingsState.vibrationsEnabled.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) { ThemeState.isDark.value = enabled }
    fun toggleNotifications(enabled: Boolean) { _notificationsEnabled.value = enabled }
    fun toggleKeepScreenOn(enabled: Boolean) { _keepScreenOn.value = enabled }
    fun toggleVibrations(enabled: Boolean) { AppSettingsState.vibrationsEnabled.value = enabled }

    fun resetAllUserData() {
        viewModelScope.launch {
            repository.clearAllUserData()
        }
    }
}