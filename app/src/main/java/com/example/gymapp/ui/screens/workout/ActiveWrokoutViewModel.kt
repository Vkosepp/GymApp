package com.example.gymapp.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.local.entity.ExerciseEntity
import com.example.gymapp.data.local.entity.PerformedSetEntity
import com.example.gymapp.data.local.entity.WorkoutSessionEntity
import com.example.gymapp.data.repository.GymRepository
import com.example.gymapp.ui.theme.AppSettingsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ActiveSet(
    val setNumber: Int,
    val expectedReps: Int,
    val expectedWeight: Double,
    var actualReps: String,
    var actualWeight: String,
    var isCompleted: Boolean = false
)

data class ActiveExercise(
    val exercise: ExerciseEntity,
    val isAdvanced: Boolean,
    val eccentricTempo: Int,
    val isometricTempo: Int,
    val concentricTempo: Int,
    val sets: List<ActiveSet>
)

class ActiveWorkoutViewModel(private val repository: GymRepository) : ViewModel() {

    private val _activeExercises = MutableStateFlow<List<ActiveExercise>>(emptyList())
    val activeExercises = _activeExercises.asStateFlow()

    private val _planId = MutableStateFlow<Int?>(null)

    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds = _timerSeconds.asStateFlow()
    private var timerJob: Job? = null

    private val _restTimerSeconds = MutableStateFlow(0)
    val restTimerSeconds = _restTimerSeconds.asStateFlow()
    private var restTimerJob: Job? = null

    // Kanał powiadomień o wibracji wysyłany do widoku UI
    private val _playVibrationEvent = MutableSharedFlow<Unit>()
    val playVibrationEvent = _playVibrationEvent.asSharedFlow()

    private val _workoutFinished = MutableSharedFlow<Boolean>()
    val workoutFinished = _workoutFinished.asSharedFlow()

    fun startWorkout(planId: Int) {
        // POPRAWKA: Jeśli aplikacja wraca z tła i dane treningu już tu są, przerywamy, by ich nie nadpisać
        if (_planId.value == planId && _activeExercises.value.isNotEmpty()) return

        _planId.value = planId
        startTimer()

        viewModelScope.launch {
            val planExercises = repository.getPlanExercisesByPlanId(planId)
            val allExercises = repository.getAllExercises().first()
            val allSets = repository.getPlanSetsByExerciseIds(planExercises.map { it.id })

            val loadedExercises = planExercises.mapNotNull { pEx ->
                val baseEx = allExercises.find { it.id == pEx.exerciseId } ?: return@mapNotNull null
                val setsForThis = allSets.filter { it.planExerciseId == pEx.id }.sortedBy { it.setNumber }

                ActiveExercise(
                    exercise = baseEx,
                    isAdvanced = pEx.isAdvanced,
                    eccentricTempo = pEx.eccentricTempo,
                    isometricTempo = pEx.isometricTempo,
                    concentricTempo = pEx.concentricTempo,
                    sets = setsForThis.map { setEntity ->
                        ActiveSet(
                            setNumber = setEntity.setNumber,
                            expectedReps = setEntity.reps,
                            expectedWeight = setEntity.weight,
                            actualReps = setEntity.reps.toString(),
                            actualWeight = setEntity.weight.toString()
                        )
                    }
                )
            }
            _activeExercises.value = loadedExercises
        }
    }

    private fun startTimer() {
        // POPRAWKA: Jeśli stoper główny już działa, nie uruchamiamy go ponownie
        if (timerJob?.isActive == true) return

        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _timerSeconds.value += 1
            }
        }
    }

    // POPRAWKA: Czas pobierany jest dynamicznie z ustawień wewnątrz funkcji
    private fun startRestTimer() {
        restTimerJob?.cancel()
        val chosenSeconds = AppSettingsState.defaultRestTime.value
        _restTimerSeconds.value = chosenSeconds

        restTimerJob = viewModelScope.launch {
            while (_restTimerSeconds.value > 0) {
                delay(1000L)
                _restTimerSeconds.value -= 1
            }
            // Gdy odliczanie dobiegnie końca, odpalamy wibracje (jeśli są włączone w ustawieniach)
            if (AppSettingsState.vibrationsEnabled.value) {
                _playVibrationEvent.emit(Unit)
            }
        }
    }

    fun stopRestTimer() {
        restTimerJob?.cancel()
        _restTimerSeconds.value = 0
    }

    fun adjustRestTimer(secondsDelta: Int) {
        val newVal = _restTimerSeconds.value + secondsDelta
        _restTimerSeconds.value = if (newVal > 0) newVal else 0
    }

    fun toggleSetCompleted(exerciseId: Int, setNumber: Int, isChecked: Boolean) {
        _activeExercises.update { list ->
            list.map { ex ->
                if (ex.exercise.id == exerciseId) {
                    ex.copy(sets = ex.sets.map { set ->
                        if (set.setNumber == setNumber) set.copy(isCompleted = isChecked) else set
                    })
                } else ex
            }
        }

        if (isChecked) {
            startRestTimer()
        } else {
            stopRestTimer()
        }
    }

    fun updateSetValues(exerciseId: Int, setNumber: Int, reps: String, weight: String) {
        _activeExercises.update { list ->
            list.map { ex ->
                if (ex.exercise.id == exerciseId) {
                    ex.copy(sets = ex.sets.map { set ->
                        if (set.setNumber == setNumber) set.copy(actualReps = reps, actualWeight = weight) else set
                    })
                } else ex
            }
        }
    }

    fun finishWorkout() {
        timerJob?.cancel()
        restTimerJob?.cancel()
        val currentPlanId = _planId.value ?: return

        viewModelScope.launch {
            val session = WorkoutSessionEntity(
                planId = currentPlanId,
                dateTimestamp = System.currentTimeMillis(),
                durationSeconds = _timerSeconds.value
            )
            val sessionId = repository.insertWorkoutSession(session).toInt()

            val performedSets = mutableListOf<PerformedSetEntity>()
            _activeExercises.value.forEach { activeEx ->
                activeEx.sets.filter { it.isCompleted }.forEach { completedSet ->
                    performedSets.add(
                        PerformedSetEntity(
                            sessionId = sessionId,
                            exerciseId = activeEx.exercise.id,
                            setNumber = completedSet.setNumber,
                            reps = completedSet.actualReps.toIntOrNull() ?: 0,
                            weight = completedSet.actualWeight.toDoubleOrNull() ?: 0.0
                        )
                    )
                }
            }

            repository.insertPerformedSets(performedSets)
            _workoutFinished.emit(true)
        }
    }
}