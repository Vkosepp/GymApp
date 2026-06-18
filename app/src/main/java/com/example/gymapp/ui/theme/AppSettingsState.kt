package com.example.gymapp.ui.theme

import kotlinx.coroutines.flow.MutableStateFlow

object AppSettingsState {
    val defaultRestTime = MutableStateFlow(90)     // Domyślnie 90 sekund
    val vibrationsEnabled = MutableStateFlow(true)  // Globalny stan wibracji
}