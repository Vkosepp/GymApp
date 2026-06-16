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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
    onNavigateToWorkout: (Int) -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val scheduledPlans by viewModel.scheduledPlans.collectAsState()
    val availablePlans by viewModel.availablePlans.collectAsState()

    // Filtrujemy plany tylko dla wybranej daty
    val plansForSelectedDate = scheduledPlans.filter { it.date == selectedDate }

    // Lista unikalnych dat, w których jest jakikolwiek trening (do oznaczania kropką)
    val datesWithWorkouts = scheduledPlans.map { it.date }.toSet()

    // Stan kontrolujący wyświetlanie okienka przypisywania planu
    var showAssignDialog by remember { mutableStateOf(false) }

    var scheduleToDelete by remember { mutableStateOf<Int?>(null) }
    val isToday = selectedDate == LocalDate.now()


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        CustomCalendar(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            datesWithWorkouts = datesWithWorkouts, // Przekazujemy daty do oznaczenia
            onPreviousMonth = { viewModel.onPreviousMonth() },
            onNextMonth = { viewModel.onNextMonth() },
            onDateSelected = { viewModel.onDateSelected(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Treningi na: ${selectedDate}", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { showAssignDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Przypisz plan")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (plansForSelectedDate.isEmpty()) {
            Text("Brak zaplanowanych treningów na ten dzień. Kliknij +, aby przypisać plan.", color = Color.Gray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(plansForSelectedDate) { scheduledItem ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = scheduledItem.planTitle, fontWeight = FontWeight.Bold)
                            }

                            Row {
                                Button(onClick = { onNavigateToWorkout(scheduledItem.planId) }, enabled = isToday) {
                                    Text("Start")
                                }
                                IconButton(onClick = { viewModel.removeScheduledPlan(scheduledItem.scheduleId) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Usuń", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Okienko przypisywania planu
    if (showAssignDialog) {
        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = { Text("Przypisz plan do: $selectedDate") },
            text = {
                if (availablePlans.isEmpty()) {
                    Text("Nie masz jeszcze żadnych planów. Przejdź do zakładki Plany, aby je stworzyć.")
                } else {
                    LazyColumn {
                        items(availablePlans) { plan ->
                            Text(
                                text = plan.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.schedulePlanForDate(plan.id, selectedDate)
                                        showAssignDialog = false
                                    }
                                    .padding(16.dp)
                            )
                            Divider()
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAssignDialog = false }) { Text("Anuluj") }
            }
        )
    }

    if (scheduleToDelete != null) {
        AlertDialog(
            onDismissRequest = { scheduleToDelete = null },
            title = { Text("Usunąć trening z kalendarza?") },
            text = { Text("Czy na pewno chcesz usunąć ten trening z zaplanowanych na ten dzień?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeScheduledPlan(scheduleToDelete!!)
                        scheduleToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Usuń") }
            },
            dismissButton = { TextButton(onClick = { scheduleToDelete = null }) { Text("Anuluj") } }
        )
    }

}

// Zaktualizowany komponent Kalendarza
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomCalendar(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    datesWithWorkouts: Set<LocalDate>, // Nowy parametr
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Poprzedni miesiąc") }
                val monthName = currentMonth.month.getDisplayName(TextStyle.FULL, Locale("pl", "PL")).replaceFirstChar { it.uppercase() }
                Text(text = "$monthName ${currentMonth.year}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                IconButton(onClick = onNextMonth) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Następny miesiąc") }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val daysOfWeek = listOf("Pn", "Wt", "Śr", "Cz", "Pt", "Sb", "Nd")
            Row(modifier = Modifier.fillMaxWidth()) {
                daysOfWeek.forEach { day ->
                    Text(text = day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val daysInMonth = currentMonth.lengthOfMonth()
            val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value - 1
            val gridItems = List(firstDayOfWeek) { null } + (1..daysInMonth).toList()
            val rows = gridItems.chunked(7)

            Column {
                rows.forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        for (i in 0 until 7) {
                            val day = row.getOrNull(i)
                            if (day == null) {
                                Spacer(modifier = Modifier.weight(1f))
                            } else {
                                val date = currentMonth.atDay(day)
                                val isSelected = date == selectedDate
                                val isToday = date == LocalDate.now()
                                val hasWorkout = datesWithWorkouts.contains(date) // Sprawdzamy czy dzień ma trening

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> MaterialTheme.colorScheme.primary
                                                isToday -> MaterialTheme.colorScheme.primaryContainer
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable { onDateSelected(date) },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                        else if (isToday) MaterialTheme.colorScheme.onPrimaryContainer
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                    // Kropka oznaczająca przypisany trening
                                    if (hasWorkout) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 2.dp)
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
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
}