package com.goldcompany.apps.todoapplication.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.data.repository.TaskRepository
import com.goldcompany.apps.todoapplication.util.dateToMilli
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
    val selectedDateMilli: Long = LocalDate.now().dateToMilli(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<SnapshotStateList<Task>>(mutableStateListOf())
    private val _selectedDateMilli = MutableStateFlow(System.currentTimeMillis())
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<TaskUiState> = combine(
        _tasks, _selectedDateMilli, _isLoading
    ) { tasks, selectedDateMilli, isLoading ->
        TaskUiState(
            tasks = tasks.toMutableStateList(),
            selectedDateMilli = selectedDateMilli,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskUiState(isLoading = true)
    )

    init {
        getDailyTasks()
    }

    fun getDailyTasks(
        milli: Long = LocalDate.now().dateToMilli()
    ) {
        viewModelScope.launch {
            _tasks.update { repository.getDailyTasks(milli).toMutableStateList() }
        }
    }

    fun updateTask(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateCompleted(taskId.toLong(), isCompleted)
        }
    }

    fun updateTask(index: Int, isCompleted: Boolean) {
        val task = _tasks.value[index]

        viewModelScope.launch {
            _tasks.value[index] = task.copy(isCompleted = isCompleted)
            repository.updateCompleted(task.id.toLong(), isCompleted)
        }
    }

    fun selectDateMilli(milli: Long) {
        _selectedDateMilli.update { milli }
        getDailyTasks(milli)
    }
}