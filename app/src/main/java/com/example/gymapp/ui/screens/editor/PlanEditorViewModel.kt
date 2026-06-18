package com.example.gymapp.ui.screens.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.local.entity.ExerciseEntity
import com.example.gymapp.data.local.entity.PlanExerciseEntity
import com.example.gymapp.data.local.entity.PlanSetEntity
import com.example.gymapp.data.local.entity.WorkoutPlanEntity
import com.example.gymapp.data.repository.GymRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlanEditorViewModel(private val repository: GymRepository) : ViewModel() {

    private val _planTitle = MutableStateFlow("Nowy Plan #1")
    val planTitle = _planTitle.asStateFlow()

    private val _isAdvancedMode = MutableStateFlow(false)
    val isAdvancedMode = _isAdvancedMode.asStateFlow()

    private val _exercisesToEdit = MutableStateFlow<List<ExerciseEditState>>(emptyList())
    val exercisesToEdit = _exercisesToEdit.asStateFlow()

    private val _saveSuccess = MutableSharedFlow<Boolean>()
    val saveSuccess = _saveSuccess.asSharedFlow()

    // Flaga określająca, czy aktualizujemy istniejący plan
    private var currentEditingPlanId: Int? = null

    // TRYB 1: Tworzenie nowego planu
    fun initForCreation(idsString: String) {
        if (_exercisesToEdit.value.isNotEmpty()) return
        val ids = idsString.split(",").mapNotNull { it.toIntOrNull() }
        viewModelScope.launch {
            val allExercises = repository.getAllExercises().first()
            val selected = allExercises.filter { it.id in ids }
            _exercisesToEdit.value = selected.map { ExerciseEditState(it) }
        }
    }

    // TRYB 2: Edycja istniejącego planu
    fun initForEdit(planId: Int) {
        if (currentEditingPlanId == planId) return
        currentEditingPlanId = planId

        viewModelScope.launch {
            val plan = repository.getWorkoutPlanById(planId) ?: return@launch
            _planTitle.value = plan.title

            val planExercises = repository.getPlanExercisesByPlanId(planId)
            if (planExercises.isEmpty()) return@launch

            _isAdvancedMode.value = planExercises.first().isAdvanced
            val allSets = repository.getPlanSetsByExerciseIds(planExercises.map { it.id })
            val allExercises = repository.getAllExercises().first()

            _exercisesToEdit.value = planExercises.mapNotNull { pEx ->
                val baseEx = allExercises.find { it.id == pEx.exerciseId } ?: return@mapNotNull null
                val setsForThis = allSets.filter { it.planExerciseId == pEx.id }

                ExerciseEditState(
                    exercise = baseEx,
                    sets = setsForThis.size.toString(),
                    reps = setsForThis.firstOrNull()?.reps?.toString() ?: "12",
                    weight = setsForThis.firstOrNull()?.weight?.toString() ?: "0.0",
                    tempoEccentric = pEx.eccentricTempo.toString(),
                    tempoIsometric = pEx.isometricTempo.toString(),
                    tempoConcentric = pEx.concentricTempo.toString()
                )
            }
        }
    }

    fun onTitleChange(newTitle: String) { _planTitle.value = newTitle }
    fun onModeToggle(isAdvanced: Boolean) { _isAdvancedMode.value = isAdvanced }

    fun updateField(exerciseId: Int, field: String, value: String) {
        _exercisesToEdit.update { list ->
            list.map { item ->
                if (item.exercise.id == exerciseId) {
                    when (field) {
                        "sets" -> item.copy(sets = value)
                        "reps" -> item.copy(reps = value)
                        "weight" -> item.copy(weight = value)
                        "eccentric" -> item.copy(tempoEccentric = value)
                        "isometric" -> item.copy(tempoIsometric = value)
                        "concentric" -> item.copy(tempoConcentric = value)
                        else -> item
                    }
                } else item
            }
        }
    }

    fun savePlan() {
        viewModelScope.launch {
            val finalPlanId: Int

            if (currentEditingPlanId != null) {
                // Tryb Edycji: Aktualizujemy tytuł, usuwamy stare ćwiczenia z serii (zrobią to kaskadowo)
                repository.updateWorkoutPlan(WorkoutPlanEntity(id = currentEditingPlanId!!, title = _planTitle.value))
                repository.deletePlanExercisesByPlanId(currentEditingPlanId!!)
                finalPlanId = currentEditingPlanId!!
            } else {
                // Tryb Tworzenia: Tworzymy nowy rekord
                finalPlanId = repository.insertWorkoutPlan(WorkoutPlanEntity(title = _planTitle.value)).toInt()
            }

            val allSetsToInsert = mutableListOf<PlanSetEntity>()

            _exercisesToEdit.value.forEach { state ->
                val planExercise = PlanExerciseEntity(
                    planId = finalPlanId,
                    exerciseId = state.exercise.id,
                    isAdvanced = _isAdvancedMode.value,
                    eccentricTempo = state.tempoEccentric.toIntOrNull() ?: 2,
                    isometricTempo = state.tempoIsometric.toIntOrNull() ?: 1,
                    concentricTempo = state.tempoConcentric.toIntOrNull() ?: 1
                )

                val planExerciseId = repository.insertPlanExercise(planExercise).toInt()

                val setsCount = state.sets.toIntOrNull() ?: 3
                val repsCount = state.reps.toIntOrNull() ?: 12
                val weightVal = state.weight.toDoubleOrNull() ?: 0.0

                for (i in 1..setsCount) {
                    allSetsToInsert.add(PlanSetEntity(planExerciseId = planExerciseId, setNumber = i, reps = repsCount, weight = weightVal))
                }
            }

            repository.insertPlanSets(allSetsToInsert)
            _saveSuccess.emit(true)
        }
    }
    val availableExercises = repository.getAllExercises().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeExercise(exerciseId: Int) {
        _exercisesToEdit.update { list -> list.filter { it.exercise.id != exerciseId } }
    }

    fun addExercise(exercise: ExerciseEntity) {
        if (_exercisesToEdit.value.any { it.exercise.id == exercise.id }) return
        _exercisesToEdit.update { list -> list + ExerciseEditState(exercise) }
    }
}