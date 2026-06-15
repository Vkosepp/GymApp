package com.example.gymapp.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToWorkout: () -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val workoutPlans by viewModel.workoutPlans.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // Komponent Kalendarza
        CustomCalendar(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            onPreviousMonth = { viewModel.onPreviousMonth() },
            onNextMonth = { viewModel.onNextMonth() },
            onDateSelected = { viewModel.onDateSelected(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Przycisk Start (Placeholder na to, o czym pisałeś w głównych założeniach)
        Button(
            onClick = { /* TODO: Rozpocznij dzisiejszy trening */ },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Rozpocznij trening na dziś")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Nadchodzące treningi:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (workoutPlans.isEmpty()) {
            Text("Brak zaplanowanych treningów.", color = Color.Gray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(workoutPlans) { plan ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onNavigateToWorkout() },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = plan.title,
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomCalendar(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Nagłówek: Miesiąc i przyciski
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Poprzedni miesiąc")
                }

                // Formatyzacja miesiąca np. "Wrzesień 2025"
                val monthName = currentMonth.month.getDisplayName(TextStyle.FULL, Locale("pl", "PL"))
                    .replaceFirstChar { it.uppercase() }
                Text(
                    text = "$monthName ${currentMonth.year}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onNextMonth) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Następny miesiąc")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dni tygodnia (Pn, Wt, Śr...)
            val daysOfWeek = listOf("Pn", "Wt", "Śr", "Cz", "Pt", "Sb", "Nd")
            Row(modifier = Modifier.fillMaxWidth()) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Siatka dni
            val daysInMonth = currentMonth.lengthOfMonth()
            // currentMonth.atDay(1).dayOfWeek.value zwraca 1 dla Pn, 7 dla Nd.
            // Odejmujemy 1, żeby mieć ilość pustych komórek przed pierwszym dniem miesiąca.
            val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value - 1

            // Tworzymy listę wszystkich komórek w siatce (puste + dni miesiąca)
            val gridItems = List(firstDayOfWeek) { null } + (1..daysInMonth).toList()
            val rows = gridItems.chunked(7) // Dzielimy na tygodnie (wiersze po 7 elementów)

            Column {
                rows.forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        for (i in 0 until 7) {
                            val day = row.getOrNull(i)
                            if (day == null) {
                                // Pusta komórka na początku lub końcu miesiąca
                                Spacer(modifier = Modifier.weight(1f))
                            } else {
                                val date = currentMonth.atDay(day)
                                val isSelected = date == selectedDate
                                val isToday = date == LocalDate.now()

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f) // Kwadratowe komórki
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> MaterialTheme.colorScheme.primary
                                                isToday -> MaterialTheme.colorScheme.primaryContainer
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable { onDateSelected(date) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                        else if (isToday) MaterialTheme.colorScheme.onPrimaryContainer
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}