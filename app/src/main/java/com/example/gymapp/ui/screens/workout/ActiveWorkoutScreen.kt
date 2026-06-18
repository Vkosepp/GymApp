package com.example.gymapp.ui.screens.workout

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun ActiveWorkoutScreen(
    viewModel: ActiveWorkoutViewModel,
    planId: Int,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(planId) {
        viewModel.startWorkout(planId)
    }

    LaunchedEffect(Unit) {
        viewModel.workoutFinished.collect { if (it) onNavigateBack() }
    }

    // OBSŁUGA FIZYCZNYCH WIBRACJI TELEFONU
    LaunchedEffect(Unit) {
        viewModel.playVibrationEvent.collect {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500L, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500L)
            }
        }
    }

    val exercises by viewModel.activeExercises.collectAsState()
    val timer by viewModel.timerSeconds.collectAsState()
    val restTimer by viewModel.restTimerSeconds.collectAsState()

    val min = timer / 60
    val sec = timer % 60
    val totalTimeStr = String.format("%02d:%02d", min, sec)

    Scaffold(
        bottomBar = {
            Column {
                if (restTimer > 0) {
                    val rMin = restTimer / 60
                    val rSec = restTimer % 60
                    val restStr = String.format("%02d:%02d", rMin, rSec)

                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Przerwa: $restStr", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                            Row {
                                TextButton(onClick = { viewModel.adjustRestTimer(-30) }) { Text("-30s") }
                                TextButton(onClick = { viewModel.adjustRestTimer(30) }) { Text("+30s") }
                                TextButton(onClick = { viewModel.stopRestTimer() }) { Text("Pomiń") }
                            }
                        }
                    }
                }

                BottomAppBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Czas: $totalTimeStr", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Button(onClick = { viewModel.finishWorkout() }) {
                            Text("Zakończ")
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(exercises) { activeEx ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = activeEx.exercise.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )

                            if (activeEx.isAdvanced) {
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Tempo: ${activeEx.eccentricTempo}-${activeEx.isometricTempo}-${activeEx.concentricTempo}",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Seria", modifier = Modifier.weight(1f))
                            Text("KG", modifier = Modifier.weight(1.5f))
                            Text("Powt.", modifier = Modifier.weight(1.5f))
                            Text("Gotowe", modifier = Modifier.weight(1f))
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        activeEx.sets.forEach { set ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).background(
                                    if (set.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${set.setNumber}", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)

                                OutlinedTextField(
                                    value = set.actualWeight,
                                    onValueChange = { viewModel.updateSetValues(activeEx.exercise.id, set.setNumber, set.actualReps, it) },
                                    modifier = Modifier.weight(1.5f).padding(end = 4.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )

                                OutlinedTextField(
                                    value = set.actualReps,
                                    onValueChange = { viewModel.updateSetValues(activeEx.exercise.id, set.setNumber, it, set.actualWeight) },
                                    modifier = Modifier.weight(1.5f).padding(end = 4.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )

                                Checkbox(
                                    checked = set.isCompleted,
                                    onCheckedChange = { viewModel.toggleSetCompleted(activeEx.exercise.id, set.setNumber, it) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}