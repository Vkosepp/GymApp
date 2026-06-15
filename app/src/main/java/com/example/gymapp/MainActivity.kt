package com.example.gymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.gymapp.ui.main.MainAppScreen
import com.example.gymapp.ui.theme.GymAppTheme // Zależnie od nazwy projektu, to może się nazywać inaczej

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Nazwa motywu zazwyczaj jest generowana na podstawie nazwy projektu
            // Jeśli podświetla się na czerwono, sprawdź import u góry lub zmień na to, co było tam wcześniej
            GymAppTheme {
                MainAppScreen()
            }
        }
    }
}