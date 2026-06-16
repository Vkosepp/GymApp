package com.example.gymapp.ui.screens.plans

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    val recentIds by viewModel.recentExercisesIds.collectAsState() // <-- POBRANE OSTATNIE 8

    val muscleGroups = listOf("All", "Legs", "Chest", "Back", "Shoulders", "Arms", "Core")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = query,
            onValueChange = { viewModel.onQueryChange(it) },
            label = { Text("Szukaj ćwiczenia...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(8.dp))

        ScrollableTabRow(selectedTabIndex = muscleGroups.indexOf(currentMuscle), edgePadding = 0.dp) {
            muscleGroups.forEach { group ->
                Tab(selected = currentMuscle == group, onClick = { viewModel.onMuscleGroupSelect(group) }, text = { Text(group) })
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(exercises) { exercise ->
                val isRecent = recentIds.contains(exercise.id)

                ListItem(
                    headlineContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(exercise.name, fontWeight = if (isRecent) FontWeight.Bold else FontWeight.Normal)
                            if (isRecent) {
                                Spacer(modifier = Modifier.width(8.dp))
                                // Odznaka dla ostatnio używanych
                                Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp)) {
                                    Text("Ostatnie", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                        }
                    },
                    supportingContent = { Text(exercise.muscleGroup) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        // Subtelne podświetlenie całej komórki, jeśli ćwiczenie jest w ostatnich 8
                        .background(if (isRecent) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
                        .clickable { viewModel.toggleExerciseSelection(exercise.id) },
                    trailingContent = {
                        Checkbox(checked = selectedIds.contains(exercise.id), onCheckedChange = { viewModel.toggleExerciseSelection(exercise.id) })
                    }
                )
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onNext(selectedIds.toList()) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = selectedIds.isNotEmpty(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Dalej (${selectedIds.size})", fontWeight = FontWeight.Bold)
        }
    }
}
