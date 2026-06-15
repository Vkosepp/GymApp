package com.example.gymapp.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.local.entity.ScheduledWorkoutEntity
import com.example.gymapp.data.local.entity.WorkoutPlanEntity
import com.example.gymapp.data.repository.GymRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

// Pomocnicza klasa dla UI, łącząca kalendarz z konkretnym planem
data class ScheduledPlanDetail(
    val scheduleId: Int,
    val planId: Int,
    val date: LocalDate,
    val planTitle: String
)

class HomeViewModel(private val repository: GymRepository) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    @RequiresApi(Build.VERSION_CODES.O)
    val selectedDate = _selectedDate.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    @RequiresApi(Build.VERSION_CODES.O)
    val currentMonth = _currentMonth.asStateFlow()

    // Pobieramy wszystkie dostępne szablony planów (do wyświetlenia w oknie wyboru)
    val availablePlans = repository.getAllWorkoutPlans().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Reaktywne scalenie zaplanowanych treningów z nazwami planów
    @RequiresApi(Build.VERSION_CODES.O)
    val scheduledPlans = combine(
        repository.getAllScheduledWorkouts(),
        repository.getAllWorkoutPlans()
    ) { schedules, plans ->
        schedules.mapNotNull { schedule ->
            val plan = plans.find { it.id == schedule.planId }
            if (plan != null) {
                ScheduledPlanDetail(
                    scheduleId = schedule.id,
                    planId = plan.id,
                    date = LocalDate.parse(schedule.dateString),
                    planTitle = plan.title
                )
            } else null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @RequiresApi(Build.VERSION_CODES.O)
    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
        if (YearMonth.from(date) != _currentMonth.value) {
            _currentMonth.value = YearMonth.from(date)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onPreviousMonth() { _currentMonth.value = _currentMonth.value.minusMonths(1) }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onNextMonth() { _currentMonth.value = _currentMonth.value.plusMonths(1) }

    fun schedulePlanForDate(planId: Int, date: LocalDate) {
        viewModelScope.launch {
            repository.insertScheduledWorkout(
                ScheduledWorkoutEntity(planId = planId, dateString = date.toString())
            )
        }
    }

    fun removeScheduledPlan(scheduleId: Int) {
        viewModelScope.launch {
            repository.deleteScheduledWorkout(scheduleId)
        }
    }
}