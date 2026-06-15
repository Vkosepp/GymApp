package com.example.gymapp.ui.screens.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.local.entity.ExerciseEntity
import com.example.gymapp.data.repository.GymRepository
import kotlinx.coroutines.flow.*

class ExerciseSearchViewModel(private val repository: GymRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedMuscleGroup = MutableStateFlow("All")
    val selectedMuscleGroup = _selectedMuscleGroup.asStateFlow()

    // Lista ID zaznaczonych ćwiczeń
    private val _selectedExerciseIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedExerciseIds = _selectedExerciseIds.asStateFlow()

    // Dynamicznie filtrowana lista ćwiczeń z bazy Room
    val filteredExercises: StateFlow<List<ExerciseEntity>> = combine(
        _searchQuery,
        _selectedMuscleGroup,
        repository.getAllExercises()
    ) { query, muscle, allExercises ->
        allExercises.filter {
            (muscle == "All" || it.muscleGroup == muscle) &&
                    it.name.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChange(newQuery: String) { _searchQuery.value = newQuery }

    fun onMuscleGroupSelect(group: String) { _selectedMuscleGroup.value = group }

    fun toggleExerciseSelection(id: Int) {
        val current = _selectedExerciseIds.value
        _selectedExerciseIds.value = if (current.contains(id)) current - id else current + id
    }
}