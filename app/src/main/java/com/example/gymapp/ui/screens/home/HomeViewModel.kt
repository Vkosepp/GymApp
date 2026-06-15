package com.example.gymapp.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.repository.GymRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth

class HomeViewModel(private val repository: GymRepository) : ViewModel() {

    // Stan aktualnie wybranej daty
    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    @RequiresApi(Build.VERSION_CODES.O)
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Stan aktualnie oglądanego miesiąca w kalendarzu
    @RequiresApi(Build.VERSION_CODES.O)
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    @RequiresApi(Build.VERSION_CODES.O)
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    // Pobieranie listy planów (na razie pobiera wszystkie, później podepniemy tu sesje)
    val workoutPlans = repository.getAllWorkoutPlans().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
        // Opcjonalnie: jeśli klikniesz dzień z innego miesiąca, zmień też widok miesiąca
        if (YearMonth.from(date) != _currentMonth.value) {
            _currentMonth.value = YearMonth.from(date)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onPreviousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onNextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
    }
}

class HomeViewModelFactory(private val repository: GymRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}