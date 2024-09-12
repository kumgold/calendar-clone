package com.goldcompany.apps.todoapplication.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.data.repository.TaskRepository
import com.goldcompany.apps.todoapplication.util.convertDateToMilli
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TaskUiState(
    val tasks: SnapshotStateList<Task> = mutableStateListOf(),
    val monthlyTasks: Map<Long, List<Task>> = mapOf(),
    val selectedDateMilli: Long = LocalDate.now().convertDateToMilli(),
    val startLocalDate: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<SnapshotStateList<Task>>(mutableStateListOf())
    private val _monthlyTasks = MutableStateFlow<Map<Long, List<Task>>>(mapOf())
    private val _selectedDateMilli = MutableStateFlow(LocalDate.now().convertDateToMilli())
    private val _startLocalDate = MutableStateFlow(LocalDate.now())
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<TaskUiState> = combine(
        _tasks, _monthlyTasks, _selectedDateMilli, _startLocalDate, _isLoading
    ) { tasks, monthlyTasks, selectedDateMilli, startLocalDate, isLoading ->
        TaskUiState(
            tasks = tasks.toMutableStateList(),
            monthlyTasks = monthlyTasks,
            selectedDateMilli = selectedDateMilli,
            startLocalDate = startLocalDate,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskUiState(isLoading = true)
    )

    init {
        getMonthlyTasks()
    }

    fun getMonthlyTasks(
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusMonths(1)
    ) {
        viewModelScope.launch {
            val map = mutableMapOf<Long, List<Task>>()
            val list = repository.getMonthlyTasks(startDate.convertDateToMilli(), endDate.convertDateToMilli())

            list.map { task ->
                map[task.dateMilli] = list.filter { it.dateMilli == task.dateMilli }
            }

            _monthlyTasks.update { map }
        }
        _startLocalDate.update { startDate }
    }

    fun updateTask(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateCompleted(taskId.toLong(), isCompleted)
        }
    }

    fun selectDateMilli(milli: Long) {
        _selectedDateMilli.update { milli }
    }
}