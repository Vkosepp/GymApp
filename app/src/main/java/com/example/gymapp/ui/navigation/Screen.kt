package com.example.gymapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Settings : Screen("settings", "Ustawienia", Icons.Default.Settings)
    object Plans : Screen("plans", "Plany", Icons.AutoMirrored.Filled.List)
    object Home : Screen("home", "Kalendarz", Icons.Default.DateRange)
    object Stats : Screen("stats", "Statystyki", Icons.Default.Info)
    object Profile : Screen("profile", "Profil", Icons.Default.Person)

    object ActiveWorkout : Screen("active_workout", "Trening", Icons.Default.PlayArrow)
    object PlanEditor : Screen("plan_editor", "Edytor Planu", Icons.Default.Edit)
}