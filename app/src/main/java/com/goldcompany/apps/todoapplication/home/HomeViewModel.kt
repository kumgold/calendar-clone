package com.goldcompany.apps.todoapplication.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.data.repository.TaskRepository
import com.goldcompany.apps.todoapplication.util.TasksFilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

data class TaskUiState(
    val items: List<Task> = emptyList(),
    val currentDateMillis: Long = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _dailyTasks = MutableStateFlow<List<Task>>(emptyList())
    private val _currentDateMillis = MutableStateFlow(System.currentTimeMillis())
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<TaskUiState> = combine(
        _dailyTasks, _currentDateMillis, _isLoading
    ) { tasks, millis, isLoading ->
        TaskUiState(
            items = tasks,
            currentDateMillis = millis,
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

    private fun getDailyTasks(
        millis: Long = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    ) {
        viewModelScope.launch {
            _dailyTasks.value = repository.getDailyTasks(millis)
            println(System.currentTimeMillis())
            println(repository.getDailyTasks(millis))
        }
    }

    fun updateTaskCompleted(taskId: String, completed: Boolean) {
        viewModelScope.launch {
            repository.updateCompleted(taskId.toLong(), completed)
        }
    }

    fun setCurrentDateMillis(millis: Long) {
        _currentDateMillis.value = millis
        getDailyTasks(millis)
    }
}