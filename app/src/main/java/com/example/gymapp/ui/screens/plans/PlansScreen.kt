package com.example.gymapp.ui.screens.plans

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gymapp.data.local.entity.WorkoutPlanEntity

@Composable
fun PlansScreen(
    viewModel: PlansViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToEditor: (Int) -> Unit
) {
    val plans by viewModel.workoutPlans.collectAsState()
    var planToDelete by remember { mutableStateOf<WorkoutPlanEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("Plan list", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Duży, przyjazny przycisk dodawania na górze wzorowany na makiecie
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .clickable { onNavigateToSearch() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = "Dodaj", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(plans) { plan ->
                // Przeprojektowana karta planu w kolorze motywu (jasnoróżowy/niebieskawy)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        .clickable { onNavigateToEditor(plan.id) }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(plan.title, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { planToDelete = plan }) {
                        Icon(Icons.Default.Delete, contentDescription = "Usuń", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }

    if (planToDelete != null) {
        AlertDialog(
            onDismissRequest = { planToDelete = null },
            title = { Text("Usunąć plan treningowy?") },
            text = { Text("Czy na pewno chcesz trwale usunąć '${planToDelete?.title}'? Ta akcja usunie też przypisaną historię.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.deletePlan(planToDelete!!); planToDelete = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Usuń") }
            },
            dismissButton = { TextButton(onClick = { planToDelete = null }) { Text("Anuluj") } }
        )
    }
}
