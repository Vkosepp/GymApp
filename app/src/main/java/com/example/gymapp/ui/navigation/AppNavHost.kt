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
import com.example.gymapp.ui.screens.editor.PlanEditorScreen
import com.example.gymapp.ui.screens.editor.PlanEditorViewModel
import com.example.gymapp.ui.screens.home.HomeScreen
import com.example.gymapp.ui.screens.home.HomeViewModel
import com.example.gymapp.ui.screens.plans.ExerciseSearchScreen
import com.example.gymapp.ui.screens.plans.ExerciseSearchViewModel
import com.example.gymapp.ui.screens.plans.PlansScreen
import com.example.gymapp.ui.screens.plans.PlansViewModel
import com.example.gymapp.utils.GymViewModelFactory
import androidx.navigation.navArgument
import com.example.gymapp.ui.screens.profile.ProfileScreen
import com.example.gymapp.ui.screens.profile.ProfileViewModel
import com.example.gymapp.ui.screens.settings.SettingsScreen
import com.example.gymapp.ui.screens.settings.SettingsViewModel
import com.example.gymapp.ui.screens.workout.ActiveWorkoutScreen
import com.example.gymapp.ui.screens.workout.ActiveWorkoutViewModel
import com.example.gymapp.ui.screens.stats.StatsScreen
import com.example.gymapp.ui.screens.stats.StatsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {

    // 1. Inicjalizacja bazy i repozytorium na najwyższym poziomie
    val context = LocalContext.current
    val database = GymDatabase.getDatabase(context)
    val repository = GymRepository(database.gymDao())
    val factory = GymViewModelFactory(repository)

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
            SettingsScreen(viewModel = settingsViewModel)
        }

        // --- EKRAN 1: Kalendarz ---
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = factory)
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToWorkout = { planId ->
                    // Przekazujemy ID planu prosto do ścieżki
                    navController.navigate("${Screen.ActiveWorkout.route}/$planId")
                }
            )
        }

        // --- EKRAN 2: Lista Planów ---
        composable(Screen.Plans.route) {
            val plansViewModel: PlansViewModel = viewModel(factory = factory)
            PlansScreen(
                viewModel = plansViewModel,
                onNavigateToSearch = { navController.navigate(Screen.ExerciseSearch.route) },
                // Zmiana: Przekazujemy parametr planId zamiast listy IDs
                onNavigateToEditor = { planId -> navController.navigate("${Screen.PlanEditor.route}?planId=$planId") }
            )
        }

        // EKRAN 2.1: Wyszukiwarka Ćwiczeń
        composable(Screen.ExerciseSearch.route) {
            val searchViewModel: ExerciseSearchViewModel = viewModel(factory = factory)
            ExerciseSearchScreen(
                viewModel = searchViewModel,
                onNext = { ids ->
                    // Zmiana: Przekazujemy parametr exerciseIds
                    val idsString = ids.joinToString(",")
                    navController.navigate("${Screen.PlanEditor.route}?exerciseIds=$idsString")
                }
            )
        }

        composable(Screen.Stats.route) {
            val statsViewModel: StatsViewModel = viewModel(factory = factory)
            StatsScreen(viewModel = statsViewModel)
        }
        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel(factory = factory)
            ProfileScreen(viewModel = profileViewModel)
        }

        composable(
            route = "${Screen.ActiveWorkout.route}/{planId}",
            arguments = listOf(androidx.navigation.navArgument("planId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getInt("planId") ?: return@composable
            val activeWorkoutViewModel: ActiveWorkoutViewModel = viewModel(factory = factory)

            ActiveWorkoutScreen(
                viewModel = activeWorkoutViewModel,
                planId = planId,
                onNavigateBack = { navController.popBackStack(Screen.Home.route, inclusive = false) }
            )
        }

        composable(
            route = "${Screen.PlanEditor.route}?exerciseIds={exerciseIds}&planId={planId}",
            arguments = listOf(
                androidx.navigation.navArgument("exerciseIds") { nullable = true },
                androidx.navigation.navArgument("planId") {
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val exerciseIds = backStackEntry.arguments?.getString("exerciseIds")
            val planId = backStackEntry.arguments?.getString("planId")?.toIntOrNull()

            val editorViewModel: PlanEditorViewModel = viewModel(factory = factory)

            PlanEditorScreen(
                viewModel = editorViewModel,
                exerciseIds = exerciseIds, // Zaktualizowana nazwa
                planId = planId,           // Zaktualizowana nazwa
                onNavigateBack = { navController.popBackStack(Screen.Plans.route, inclusive = false) }
            )
        }
    }
}