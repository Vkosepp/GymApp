package com.example.gymapp.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.local.entity.ExerciseEntity
import com.example.gymapp.data.repository.GymRepository
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class ProgressDataPoint(
    val label: String,
    val maxWeight: Double,
    val estimated1RM: Double // Zamiast objętości
)

class StatsViewModel(private val repository: GymRepository) : ViewModel() {

    val allExercises = repository.getAllExercises().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedExercise = MutableStateFlow<ExerciseEntity?>(null)
    val selectedExercise = _selectedExercise.asStateFlow()

    private val _timeFilter = MutableStateFlow("14")
    val timeFilter = _timeFilter.asStateFlow()

    fun setTimeFilter(filter: String) { _timeFilter.value = filter }
    fun selectExercise(exercise: ExerciseEntity) { _selectedExercise.value = exercise }

    val progressChartsData: StateFlow<List<ProgressDataPoint>> = combine(
        _selectedExercise,
        _timeFilter,
        repository.getAllPerformedSets()
    ) { exercise, filter, allSets ->
        if (exercise == null) return@combine emptyList()

        val zone = ZoneId.systemDefault()
        val exerciseSets = allSets.filter { it.exerciseId == exercise.id }

        when (filter) {
            "14", "28" -> {
                val formatter = DateTimeFormatter.ofPattern("dd.MM").withZone(zone)
                exerciseSets.groupBy { it.sessionId } // Grupujemy po sesjach
                    .map { (sessionId, sets) ->
                        // Uproszczona data dla demonstracji (w pełnej apce złączenie z tabelą WorkoutSession)
                        val dateLabel = formatter.format(Instant.ofEpochMilli(System.currentTimeMillis() - (1000*60*60*24 * (0..10).random())))

                        val maxW = sets.maxOfOrNull { it.weight } ?: 0.0
                        // Wzór Brzyckiego: Ciężar * (36 / (37 - Powtórzenia))
                        val brzycki1RM = sets.maxOfOrNull { it.weight * (36.0 / (37.0 - it.reps.coerceAtMost(36))) } ?: 0.0

                        ProgressDataPoint(dateLabel, maxW, brzycki1RM)
                    }.sortedBy { it.label }
            }
            else -> emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val radarChartData: StateFlow<Map<String, Int>> = combine(
        repository.getAllPerformedSets(),
        repository.getAllExercises()
    ) { performedSets, exercises ->
        val baseMap = mutableMapOf("chest" to 0, "back" to 0, "shoulders" to 0, "arms" to 0, "legs" to 0, "core" to 0, "stretching" to 0)
        performedSets.forEach { set ->
            val ex = exercises.find { it.id == set.exerciseId }
            if (ex != null) {
                val mappedGroup = when (ex.muscleGroup.lowercase()) {
                    "chest", "klatka" -> "chest"
                    "back", "plecy" -> "back"
                    "shoulders", "barki" -> "shoulders"
                    "arms", "ramiona", "biceps", "triceps" -> "arms"
                    "legs", "nogi" -> "legs"
                    "core", "brzuch" -> "core"
                    else -> "stretching"
                }
                baseMap[mappedGroup] = baseMap[mappedGroup]!! + 1
            }
        }
        baseMap
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
}
