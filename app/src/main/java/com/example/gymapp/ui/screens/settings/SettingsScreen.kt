package com.example.gymapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val keepScreenOn by viewModel.keepScreenOn.collectAsState()
    val vibrationsEnabled by viewModel.vibrationsEnabled.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Ustawienia", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // --- PREFERENCJE ---
        Text("Wygląd i Działanie", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))

        SettingsSwitchRow(
            title = "Ciemny Motyw",
            subtitle = "Zmienia wygląd całej aplikacji",
            checked = isDarkMode,
            onCheckedChange = { viewModel.toggleDarkMode(it) }
        )

        SettingsSwitchRow(
            title = "Nie wygaszaj ekranu",
            subtitle = "Ekran pozostanie włączony podczas aktywnego treningu",
            checked = keepScreenOn,
            onCheckedChange = { viewModel.toggleKeepScreenOn(it) }
        )

        SettingsSwitchRow(
            title = "Wibracje stopera",
            subtitle = "Wibruj po zakończeniu odliczania przerwy",
            checked = vibrationsEnabled,
            onCheckedChange = { viewModel.toggleVibrations(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- POWIADOMIENIA ---
        Text("Powiadomienia", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))

        SettingsSwitchRow(
            title = "Przypomnienia o treningu",
            subtitle = "Powiadom rano, jeśli masz dziś zaplanowany trening",
            checked = notificationsEnabled,
            onCheckedChange = { viewModel.toggleNotifications(it) }
        )

        Spacer(modifier = Modifier.weight(1f))

        // --- DANGER ZONE ---
        Divider()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Strefa Zagrożenia", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showResetDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Delete, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Zresetuj wszystkie dane")
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Czy na pewno?") },
            text = { Text("Ta akcja usunie wszystkie Twoje plany, historię treningów, zdjęcia i profil. Zostanie tylko baza ćwiczeń. Tego nie da się cofnąć!") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllUserData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Tak, usuń wszystko") }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Anuluj") }
            }
        )
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}