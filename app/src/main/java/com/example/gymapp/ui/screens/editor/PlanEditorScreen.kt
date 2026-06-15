package com.example.gymapp.ui.screens.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
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
    exerciseIds: String?, // Zmienione z idsString: String
    planId: Int?,         // Dodane
    onNavigateBack: () -> Unit
) {
    // Inicjalizacja w zależności od trybu
    LaunchedEffect(exerciseIds, planId) {
        if (planId != null) {
            viewModel.initForEdit(planId)
        } else if (!exerciseIds.isNullOrEmpty()) {
            viewModel.initForCreation(exerciseIds)
        }
    }

    // Nasłuchiwanie na sukces zapisu, żeby wrócić do listy planów
    LaunchedEffect(Unit) {
        viewModel.saveSuccess.collect { success ->
            if (success) onNavigateBack()
        }
    }

    val title by viewModel.planTitle.collectAsState()
    val isAdvanced by viewModel.isAdvancedMode.collectAsState()
    val exercises by viewModel.exercisesToEdit.collectAsState()

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
                        Text(item.exercise.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(item.exercise.muscleGroup, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)

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

                        // Pola Advanced (Tempo: Opadanie - Przytrzymanie - Wznoszenie)
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
        }

        // --- ZAPIS ---
        Button(
            onClick = { viewModel.savePlan() },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Potwierdź i Zapisz")
        }
    }
}

// Komponent pomocniczy dla czystszego kodu
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