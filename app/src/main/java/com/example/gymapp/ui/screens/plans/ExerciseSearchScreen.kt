package com.example.gymapp.ui.screens.plans

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExerciseSearchScreen(
    viewModel: ExerciseSearchViewModel,
    onNext: (List<Int>) -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val exercises by viewModel.filteredExercises.collectAsState()
    val selectedIds by viewModel.selectedExerciseIds.collectAsState()
    val currentMuscle by viewModel.selectedMuscleGroup.collectAsState()

    val muscleGroups = listOf("All", "Legs", "Front chest", "Back", "Shoulders", "Arms", "Core")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = query,
            onValueChange = { viewModel.onQueryChange(it) },
            label = { Text("Szukaj ćwiczenia...") },
            modifier = Modifier.fillMaxWidth()
        )

        ScrollableTabRow(
            selectedTabIndex = muscleGroups.indexOf(currentMuscle),
            edgePadding = 0.dp
        ) {
            muscleGroups.forEach { group ->
                Tab(
                    selected = currentMuscle == group,
                    onClick = { viewModel.onMuscleGroupSelect(group) },
                    text = { Text(group) }
                )
            }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(exercises) { exercise ->
                ListItem(
                    headlineContent = { Text(exercise.name) },
                    supportingContent = { Text(exercise.muscleGroup) },
                    modifier = Modifier.clickable { viewModel.toggleExerciseSelection(exercise.id) },
                    trailingContent = {
                        Checkbox(
                            checked = selectedIds.contains(exercise.id),
                            onCheckedChange = { viewModel.toggleExerciseSelection(exercise.id) }
                        )
                    }
                )
            }
        }

        Button(
            onClick = { onNext(selectedIds.toList()) },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedIds.isNotEmpty()
        ) {
            Text("Dalej (${selectedIds.size})")
        }
    }
}