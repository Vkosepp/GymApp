package com.example.gymapp.ui.screens.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val allExercises by viewModel.allExercises.collectAsState()
    val selectedExercise by viewModel.selectedExercise.collectAsState()
    val lineChartData by viewModel.lineChartData.collectAsState()
    val radarChartData by viewModel.radarChartData.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("Statystyki Ogólne", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // --- WYKRES PAJĘCZYNOWY (Radar Chart) ---
        Card(
            modifier = Modifier.fillMaxWidth().height(250.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Częstotliwość Partii Mięśniowych", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                if (radarChartData.isEmpty()) {
                    Text("Brak danych z treningów.", color = Color.Gray)
                } else {
                    RadarChart(data = radarChartData, modifier = Modifier.fillMaxSize())
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- WYBÓR ĆWICZENIA DO WYKRESU LINIOWEGO ---
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedExercise?.name ?: "Wybierz ćwiczenie...",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                allExercises.forEach { exercise ->
                    DropdownMenuItem(
                        text = { Text(exercise.name) },
                        onClick = {
                            viewModel.selectExercise(exercise)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- WYKRES LINIOWY (Progres Ciężaru) ---
        Card(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Progres Ciężaru (Max KG)", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                if (selectedExercise == null) {
                    Text("Wybierz ćwiczenie powyżej.", color = Color.Gray)
                } else if (lineChartData.isEmpty()) {
                    Text("Brak historii dla tego ćwiczenia.", color = Color.Gray)
                } else {
                    LineChart(data = lineChartData.map { it.second }, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun LineChart(data: List<Double>, modifier: Modifier = Modifier) {
    val lineColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas

        val maxWeight = data.maxOrNull() ?: 1.0
        val minWeight = data.minOrNull() ?: 0.0
        val range = if (maxWeight == minWeight) 1.0 else (maxWeight - minWeight)

        val xStep = size.width / (data.size - 1)
        val path = Path()

        data.forEachIndexed { index, weight ->
            // Skalowanie wartości na oś Y (odwróconą, bo 0 to góra canvasu)
            val normalizedY = 1f - ((weight - minWeight) / range).toFloat()
            val x = index * xStep
            val y = normalizedY * size.height

            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)

            // Kropki na punktach
            drawCircle(color = lineColor, radius = 6f, center = Offset(x, y))
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun RadarChart(data: Map<String, Int>, modifier: Modifier = Modifier) {
    val labels = data.keys.toList()
    val values = data.values.toList()
    val maxVal = values.maxOrNull()?.toFloat() ?: 1f

    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f * 0.8f // 80% dostępnej przestrzeni
        val center = Offset(size.width / 2f, size.height / 2f)
        val angleStep = (2 * Math.PI) / labels.size

        // Rysowanie "pajęczyny" (tła)
        for (i in 1..4) {
            val stepRadius = radius * (i / 4f)
            val bgPath = Path()
            for (j in labels.indices) {
                val angle = j * angleStep - (Math.PI / 2) // -PI/2 żeby zacząć od góry
                val x = center.x + stepRadius * cos(angle).toFloat()
                val y = center.y + stepRadius * sin(angle).toFloat()
                if (j == 0) bgPath.moveTo(x, y) else bgPath.lineTo(x, y)
            }
            bgPath.close()
            drawPath(path = bgPath, color = Color.Gray.copy(alpha = 0.3f), style = Stroke(width = 2f))
        }

        // Rysowanie danych
        val dataPath = Path()
        labels.indices.forEach { j ->
            val value = values[j]
            val valueRadius = radius * (value / maxVal)
            val angle = j * angleStep - (Math.PI / 2)

            val x = center.x + valueRadius * cos(angle).toFloat()
            val y = center.y + valueRadius * sin(angle).toFloat()

            if (j == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)

            // Rysowanie osi od środka do krawędzi
            val edgeX = center.x + radius * cos(angle).toFloat()
            val edgeY = center.y + radius * sin(angle).toFloat()
            drawLine(color = Color.Gray.copy(alpha = 0.3f), start = center, end = Offset(edgeX, edgeY), strokeWidth = 2f)

            // Kropka wartości
            drawCircle(color = primaryColor, radius = 8f, center = Offset(x, y))
        }
        dataPath.close()
        drawPath(path = dataPath, color = primaryColor.copy(alpha = 0.5f))
        drawPath(path = dataPath, color = primaryColor, style = Stroke(width = 4f))
    }
}