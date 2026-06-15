package com.example.gymapp.ui.screens.plans

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlansScreen(
    viewModel: PlansViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToEditor: (Int) -> Unit
) {
    val plans by viewModel.workoutPlans.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToSearch) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj plan")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            items(plans) { plan ->
                ListItem(
                    headlineContent = { Text(plan.title) },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable { onNavigateToEditor(plan.id) },
                    trailingContent = {
                        IconButton(onClick = { viewModel.deletePlan(plan) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Usuń")
                        }
                    }
                )
            }
        }
    }
}