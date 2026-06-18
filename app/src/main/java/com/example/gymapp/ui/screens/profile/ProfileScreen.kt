package com.example.gymapp.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.gymapp.data.local.entity.ProgressPhotoEntity
import java.io.File
import java.io.FileOutputStream

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val groupedPhotos by viewModel.groupedPhotos.collectAsState()

    // Stany dla dodawania zdjęcia progresu
    var showWeightDialog by remember { mutableStateOf(false) }
    var weightInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    var pendingProgressUri by remember { mutableStateOf<Uri?>(null) }

    // Stany dla edycji profilu
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editAge by remember { mutableStateOf("") }
    var editHeight by remember { mutableStateOf("") }

    // Stany dla pełnoekranowego podglądu zdjęcia
    var expandedPhoto by remember { mutableStateOf<ProgressPhotoEntity?>(null) }
    var editDescriptionMode by remember { mutableStateOf(false) }
    var editDescriptionText by remember { mutableStateOf("") }

    fun saveImageToPrivateStorage(uri: Uri, prefix: String = "photo"): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "${prefix}_${System.currentTimeMillis()}.jpg"
            val privateFile = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(privateFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            privateFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    val avatarPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val path = saveImageToPrivateStorage(uri, "avatar")
            if (path != null) {
                val current = userProfile
                viewModel.updateProfile(
                    name = current?.name ?: "Nowy Użytkownik",
                    age = current?.age ?: 0,
                    height = current?.height ?: 0,
                    avatarPath = path
                )
            }
        }
    }

    val progressPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            pendingProgressUri = uri
            showWeightDialog = true
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // --- SEKCJA PROFILU ---
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { avatarPickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (userProfile?.avatarPath != null) {
                    AsyncImage(
                        model = File(userProfile!!.avatarPath!!),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = "Domyślny Avatar", modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(userProfile?.name ?: "Brak danych", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Wiek: ${userProfile?.age ?: 0} | Wzrost: ${userProfile?.height ?: 0}cm", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            IconButton(onClick = {
                editName = userProfile?.name ?: ""
                editAge = userProfile?.age?.takeIf { it > 0 }?.toString() ?: ""
                editHeight = userProfile?.height?.takeIf { it > 0 }?.toString() ?: ""
                showEditProfileDialog = true
            }) {
                Icon(Icons.Default.Edit, contentDescription = "Edytuj profil")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- SEKCJA GALERII ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Galeria Progresu", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Button(onClick = { progressPickerLauncher.launch("image/*") }) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj zdjęcie")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Dodaj")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (groupedPhotos.isEmpty()) {
            Text("Nie masz jeszcze żadnych zdjęć progresu.", color = MaterialTheme.colorScheme.outline)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                groupedPhotos.forEach { (month, photos) ->
                    item {
                        Text(month, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            photos.forEach { photo ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(0.75f)
                                        .clickable { expandedPhoto = photo },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        AsyncImage(model = File(photo.filePath), contentDescription = "Zdjęcie progresu", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                        if (photo.weight != null) {
                                            Box(modifier = Modifier.align(Alignment.BottomEnd).background(Color.Black.copy(alpha = 0.6f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                                Text("${photo.weight} kg", color = Color.White, style = MaterialTheme.typography.labelSmall)
                                            }
                                        }
                                    }
                                }
                            }
                            repeat(3 - photos.size) { Spacer(modifier = Modifier.weight(1f)) }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOG EDYCJI PROFILU ---
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edytuj profil") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Imię / Nick") }, singleLine = true)
                    OutlinedTextField(value = editAge, onValueChange = { editAge = it }, label = { Text("Wiek") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                    OutlinedTextField(value = editHeight, onValueChange = { editHeight = it }, label = { Text("Wzrost (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateProfile(
                        name = editName.ifBlank { "Nieznajomy" },
                        age = editAge.toIntOrNull() ?: 0,
                        height = editHeight.toIntOrNull() ?: 0,
                        avatarPath = null
                    )
                    showEditProfileDialog = false
                }) { Text("Zapisz") }
            },
            dismissButton = { TextButton(onClick = { showEditProfileDialog = false }) { Text("Anuluj") } }
        )
    }

    // --- DIALOG WAGI I OPISU DLA NOWEGO ZDJĘCIA ---
    if (showWeightDialog) {
        AlertDialog(
            onDismissRequest = {
                pendingProgressUri?.let { uri ->
                    val path = saveImageToPrivateStorage(uri, "progress")
                    if (path != null) viewModel.addProgressPhoto(path, "", "")
                }
                showWeightDialog = false
                pendingProgressUri = null
            },
            title = { Text("Dodajesz zdjęcie") },
            text = {
                Column {
                    Text("Podaj aktualną wagę i opis (opcjonalnie).")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = weightInput, onValueChange = { weightInput = it }, label = { Text("Waga (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = descriptionInput, onValueChange = { descriptionInput = it }, label = { Text("Opis (np. Początek redukcji)") }, singleLine = false, maxLines = 3)
                }
            },
            confirmButton = {
                Button(onClick = {
                    pendingProgressUri?.let { uri ->
                        val path = saveImageToPrivateStorage(uri, "progress")
                        if (path != null) viewModel.addProgressPhoto(path, weightInput, descriptionInput)
                    }
                    showWeightDialog = false
                    weightInput = ""
                    descriptionInput = ""
                    pendingProgressUri = null
                }) { Text("Zapisz") }
            },
            dismissButton = {
                TextButton(onClick = {
                    pendingProgressUri?.let { uri ->
                        val path = saveImageToPrivateStorage(uri, "progress")
                        if (path != null) viewModel.addProgressPhoto(path, "", "")
                    }
                    showWeightDialog = false
                    weightInput = ""
                    descriptionInput = ""
                    pendingProgressUri = null
                }) { Text("Pomiń") }
            }
        )
    }

    // --- PEŁNOEKRANOWY PODGLĄD ZDJĘCIA ---
    if (expandedPhoto != null) {
        Dialog(
            onDismissRequest = {
                expandedPhoto = null
                editDescriptionMode = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = { expandedPhoto = null; editDescriptionMode = false }) {
                            Text("X", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        }
                    }

                    AsyncImage(
                        model = File(expandedPhoto!!.filePath),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    )

                    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(24.dp)) {
                        Text(text = "Waga: ${expandedPhoto!!.weight ?: "Brak"} kg", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (editDescriptionMode) {
                            OutlinedTextField(
                                value = editDescriptionText,
                                onValueChange = { editDescriptionText = it },
                                label = { Text("Opis") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                viewModel.updatePhotoDescription(expandedPhoto!!, editDescriptionText)
                                expandedPhoto = expandedPhoto!!.copy(description = editDescriptionText.takeIf { it.isNotBlank() })
                                editDescriptionMode = false
                            }) { Text("Zapisz opis") }
                        } else {
                            Text(text = expandedPhoto!!.description ?: "Brak opisu.", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = {
                                editDescriptionText = expandedPhoto!!.description ?: ""
                                editDescriptionMode = true
                            }) { Text("Edytuj opis") }
                        }
                    }
                }
            }
        }
    }
}