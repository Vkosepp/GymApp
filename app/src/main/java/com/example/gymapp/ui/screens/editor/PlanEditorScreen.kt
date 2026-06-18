package com.example.gymapp.ui.screens.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun PlanEditorScreen(
    viewModel: PlanEditorViewModel,
    exerciseIds: String?,
    planId: Int?,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(exerciseIds, planId) {
        if (planId != null) {
            viewModel.initForEdit(planId)
        } else if (!exerciseIds.isNullOrEmpty()) {
            viewModel.initForCreation(exerciseIds)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.saveSuccess.collect { success ->
            if (success) onNavigateBack()
        }
    }

    val title by viewModel.planTitle.collectAsState()
    val isAdvanced by viewModel.isAdvancedMode.collectAsState()
    val exercises by viewModel.exercisesToEdit.collectAsState()
    val availableExercises by viewModel.availableExercises.collectAsState()

    var showAddExerciseDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // --- NAGŁÓWEK ---
        OutlinedTextField(
            value = title,
            onValueChange = { viewModel.onTitleChange(it) },
            label = { Text("Nazwa Planu") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isAdvanced) "Tryb: Advanced (Z Tempem)" else "Tryb: Basic")
            Switch(
                checked = isAdvanced,
                onCheckedChange = { viewModel.onModeToggle(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- LISTA ĆWICZEŃ ---
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(exercises) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.exercise.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text(item.exercise.muscleGroup, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { viewModel.removeExercise(item.exercise.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Usuń ćwiczenie", tint = MaterialTheme.colorScheme.error)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Pola Basic
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            NumberInput(value = item.sets, label = "Sety", modifier = Modifier.weight(1f)) {
                                viewModel.updateField(item.exercise.id, "sets", it)
                            }
                            NumberInput(value = item.reps, label = "Reps", modifier = Modifier.weight(1f)) {
                                viewModel.updateField(item.exercise.id, "reps", it)
                            }
                            NumberInput(value = item.weight, label = "KG", modifier = Modifier.weight(1f)) {
                                viewModel.updateField(item.exercise.id, "weight", it)
                            }
                        }

                        // Pola Advanced
                        if (isAdvanced) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tempo (sekundy):", style = MaterialTheme.typography.labelMedium)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                NumberInput(value = item.tempoEccentric, label = "W dół", modifier = Modifier.weight(1f)) {
                                    viewModel.updateField(item.exercise.id, "eccentric", it)
                                }
                                NumberInput(value = item.tempoIsometric, label = "Pauza", modifier = Modifier.weight(1f)) {
                                    viewModel.updateField(item.exercise.id, "isometric", it)
                                }
                                NumberInput(value = item.tempoConcentric, label = "W górę", modifier = Modifier.weight(1f)) {
                                    viewModel.updateField(item.exercise.id, "concentric", it)
                                }
                            }
                        }
                    }
                }
            }

            // Przycisk dodawania na samym dole listy
            item {
                OutlinedButton(
                    onClick = { showAddExerciseDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text("+ Dodaj kolejne ćwiczenie")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- ZAPIS ---
        Button(
            onClick = { viewModel.savePlan() },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Potwierdź i Zapisz")
        }
    }

    // --- DIALOG DODAWANIA ĆWICZENIA ---
    if (showAddExerciseDialog) {
        AlertDialog(
            onDismissRequest = { showAddExerciseDialog = false },
            title = { Text("Wybierz ćwiczenie") },
            text = {
                if (availableExercises.isEmpty()) {
                    Text("Ładowanie bazy ćwiczeń...")
                } else {
                    LazyColumn {
                        items(availableExercises) { ex ->
                            Text(
                                text = ex.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.addExercise(ex)
                                        showAddExerciseDialog = false
                                    }
                                    .padding(16.dp)
                            )
                            Divider()
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddExerciseDialog = false }) { Text("Anuluj") }
            }
        )
    }
}

@Composable
fun NumberInput(value: String, label: String, modifier: Modifier = Modifier, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = modifier
    )
}