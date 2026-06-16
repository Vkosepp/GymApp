package com.example.gymapp.ui.screens.stats

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val allExercises by viewModel.allExercises.collectAsState()
    val selectedExercise by viewModel.selectedExercise.collectAsState()
    val radarChartData by viewModel.radarChartData.collectAsState()
    val progressData by viewModel.progressChartsData.collectAsState()
    val timeFilter by viewModel.timeFilter.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("General statistics", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth().height(280.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Muscle Distribution", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                RadarChart(data = radarChartData, modifier = Modifier.fillMaxSize())
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedExercise?.name ?: "Select Exercise...",
                onValueChange = {}, readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                allExercises.forEach { exercise ->
                    DropdownMenuItem(text = { Text(exercise.name) }, onClick = { viewModel.selectExercise(exercise); expanded = false })
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            FilterChip(selected = timeFilter == "14", onClick = { viewModel.setTimeFilter("14") }, label = { Text("14 Dni") })
            FilterChip(selected = timeFilter == "28", onClick = { viewModel.setTimeFilter("28") }, label = { Text("28 Dni") })
        }

        Spacer(modifier = Modifier.height(8.dp))

        // DWA WYKRESY OBOK SIEBIE (W RZĘDZIE)
        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(modifier = Modifier.weight(1f).fillMaxHeight(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Max [kg]", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    LineChartWithAxes(data = progressData.map { Pair(it.label, it.maxWeight) }, modifier = Modifier.fillMaxSize())
                }
            }
            Card(modifier = Modifier.weight(1f).fillMaxHeight(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("1RM (Brzycki) [kg]", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    LineChartWithAxes(data = progressData.map { Pair(it.label, it.estimated1RM) }, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun LineChartWithAxes(data: List<Pair<String, Double>>, modifier: Modifier = Modifier) {
    val lineColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    Canvas(modifier = modifier.padding(bottom = 16.dp, start = 16.dp, end = 8.dp)) {
        if (data.isEmpty()) return@Canvas

        val maxWeight = data.maxOfOrNull { it.second }?.let { if (it < 10.0) 10.0 else it } ?: 10.0
        val minWeight = 0.0
        val range = maxWeight - minWeight


        val xStep = size.width / if (data.size > 1) (data.size - 1).toFloat() else 1f

        val path = Path()

        val textPaint = Paint().apply {
            color = textColor
            textSize = 24f
            textAlign = Paint.Align.CENTER
        }

        // Rysowanie osi i punktów
        data.forEachIndexed { index, point ->
            val normalizedY = 1f - ((point.second - minWeight) / range).toFloat()
            val x = index * xStep
            val y = normalizedY * size.height

            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            drawCircle(color = lineColor, radius = 6f, center = Offset(x, y))

            // Label osi X (Data) na co drugim lub każdym
            if (data.size < 8 || index % 2 == 0) {
                drawContext.canvas.nativeCanvas.drawText(point.first, x, size.height + 30f, textPaint)
            }
        }

        // Rysowanie linii wykresu
        drawPath(path = path, color = lineColor, style = Stroke(width = 4f, cap = StrokeCap.Round))

        // Oś Y - Wartość maksymalna i minimalna po lewej
        textPaint.textAlign = Paint.Align.RIGHT
        drawContext.canvas.nativeCanvas.drawText("${maxWeight.toInt()}", -10f, 20f, textPaint)
        drawContext.canvas.nativeCanvas.drawText("0", -10f, size.height, textPaint)
    }
}

@Composable
fun RadarChart(data: Map<String, Int>, modifier: Modifier = Modifier) {
    val labels = data.keys.toList()
    val values = data.values.toList()
    val maxVal = values.maxOrNull()?.toFloat()?.let { if (it == 0f) 1f else it } ?: 1f

    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f * 0.7f
        val center = Offset(size.width / 2f, size.height / 2f)
        val angleStep = (2 * Math.PI) / labels.size

        val textPaint = Paint().apply {
            color = textColor
            textSize = 28f
            textAlign = Paint.Align.CENTER
        }

        for (i in 1..4) {
            val stepRadius = radius * (i / 4f)
            val bgPath = Path()
            for (j in labels.indices) {
                val angle = j * angleStep - (Math.PI / 2)
                val x = center.x + stepRadius * cos(angle).toFloat()
                val y = center.y + stepRadius * sin(angle).toFloat()
                if (j == 0) bgPath.moveTo(x, y) else bgPath.lineTo(x, y)
            }
            bgPath.close()
            drawPath(path = bgPath, color = Color.Gray.copy(alpha = 0.3f), style = Stroke(width = 2f))
        }

        val dataPath = Path()
        labels.indices.forEach { j ->
            val value = values[j]
            val valueRadius = radius * (value / maxVal)
            val angle = j * angleStep - (Math.PI / 2)

            val x = center.x + valueRadius * cos(angle).toFloat()
            val y = center.y + valueRadius * sin(angle).toFloat()

            if (j == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)

            val edgeX = center.x + radius * cos(angle).toFloat()
            val edgeY = center.y + radius * sin(angle).toFloat()
            drawLine(color = Color.Gray.copy(alpha = 0.3f), start = center, end = Offset(edgeX, edgeY), strokeWidth = 2f)
            drawCircle(color = primaryColor, radius = 8f, center = Offset(x, y))

            // ZMIANA: Etykiety sztywnych 7 partii wokół wykresu
            val labelX = center.x + (radius + 40f) * cos(angle).toFloat()
            val labelY = center.y + (radius + 40f) * sin(angle).toFloat()
            drawContext.canvas.nativeCanvas.drawText(labels[j], labelX, labelY + 10f, textPaint)
        }
        dataPath.close()
        drawPath(path = dataPath, color = primaryColor.copy(alpha = 0.5f))
        drawPath(path = dataPath, color = primaryColor, style = Stroke(width = 4f))
    }
}
