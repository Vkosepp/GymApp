package com.example.gymapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gymapp.data.local.GymDatabase
import com.example.gymapp.data.repository.GymRepository
import com.example.gymapp.ui.screens.home.HomeScreen
import com.example.gymapp.ui.screens.home.HomeViewModel
import com.example.gymapp.ui.screens.home.HomeViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Settings.route) { Text("Ekran 3: Ustawienia (Dark mode, Notyfikacje)") }
        composable(Screen.Plans.route) { Text("Ekran 2: Lista planów (Przycisk + i nawigacja do edycji)") }
        composable(Screen.Home.route) {
            val context = LocalContext.current
            val database = GymDatabase.getDatabase(context)
            val repository = GymRepository(database.gymDao())

            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(repository)
            )

            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToWorkout = { navController.navigate(Screen.ActiveWorkout.route) }
            )
        }
        composable(Screen.Stats.route) { Text("Ekran 4: Statystyki i wykres pajęczynowy") }
        composable(Screen.Profile.route) { Text("Ekran 5: Profil, waga i bezpieczna galeria") }

        composable(Screen.ActiveWorkout.route) { Text("Ekran Aktywnego Treningu ze stoperem!") }
        composable(Screen.PlanEditor.route) { Text("Edytor planu (Basic/Advanced)") }
    }
}