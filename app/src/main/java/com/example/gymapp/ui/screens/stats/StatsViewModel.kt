package com.example.gymapp.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.local.entity.ExerciseEntity
import com.example.gymapp.data.repository.GymRepository
import kotlinx.coroutines.flow.*

class StatsViewModel(private val repository: GymRepository) : ViewModel() {

    // Lista wszystkich ćwiczeń do wyszukiwarki/dropdownu
    val allExercises = repository.getAllExercises().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _selectedExercise = MutableStateFlow<ExerciseEntity?>(null)
    val selectedExercise = _selectedExercise.asStateFlow()

    // 1. DANE DO WYKRESU LINIOWEGO (Ciężar wybranego ćwiczenia)
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val lineChartData: StateFlow<List<Pair<Long, Double>>> = _selectedExercise
        .flatMapLatest { exercise ->
            if (exercise == null) flowOf(emptyList())
            else repository.getExerciseHistoryForStats(exercise.id).map { sets ->
                // Grupujemy po ID sesji i bierzemy maksymalny ciężar z danego treningu
                sets.groupBy { it.sessionId }
                    .map { (_, sessionSets) ->
                        // Uproszczenie: dla osi czasu używamy po prostu kolejności (indeksu),
                        // ale w pełnej wersji łączylibyśmy to z datą z tabeli WorkoutSession
                        val maxWeight = sessionSets.maxOfOrNull { it.weight } ?: 0.0
                        Pair(sessionSets.first().id.toLong(), maxWeight)
                    }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 2. DANE DO WYKRESU PAJĘCZYNOWEGO (Częstotliwość partii mięśniowych)
    val radarChartData: StateFlow<Map<String, Int>> = combine(
        repository.getAllPerformedSets(),
        repository.getAllExercises()
    ) { performedSets, exercises ->
        val muscleCount = mutableMapOf<String, Int>()

        // Zliczamy, ile razy dana partia była ćwiczona
        performedSets.forEach { set ->
            val exercise = exercises.find { it.id == set.exerciseId }
            if (exercise != null) {
                val currentCount = muscleCount.getOrDefault(exercise.muscleGroup, 0)
                muscleCount[exercise.muscleGroup] = currentCount + 1
            }
        }
        muscleCount
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun selectExercise(exercise: ExerciseEntity) {
        _selectedExercise.value = exercise
    }
}