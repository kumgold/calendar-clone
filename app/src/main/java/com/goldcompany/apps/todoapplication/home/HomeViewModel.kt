package com.goldcompany.apps.todoapplication.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.data.repository.TaskRepository
import com.goldcompany.apps.todoapplication.R
import com.goldcompany.apps.todoapplication.util.Async
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskUiState(
    val items: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TASKS_FILTER_SAVED_STATE_KEY = "TASKS_FILTER_SAVED_STATE_KEY"
    }

    private val _savedFilterType = savedStateHandle.getStateFlow(
        TASKS_FILTER_SAVED_STATE_KEY,
        TasksFilterType.ALL_TASKS
    )

    private val _isLoading = MutableStateFlow(false)

    private val _filteredTasks = combine(repository.getAllTasks(), _savedFilterType) { tasks, type ->
        filterTasks(tasks, type)
    }.map {
        Async.Success(it)
    }.catch<Async<List<Task>>> {
        emit(Async.Error(R.string.error_message))
    }

    val uiState: StateFlow<TaskUiState> = combine(
        _filteredTasks, _isLoading
    ) { tasksAsync, isLoading ->
        when (tasksAsync) {
            Async.Loading -> {
                TaskUiState(isLoading = true)
            }
            is Async.Success -> {
                TaskUiState(
                    items = tasksAsync.data,
                    isLoading = isLoading
                )
            }
            is Async.Error -> {
                TaskUiState(
                    isLoading = false,
                    userMessage = tasksAsync.errorMessage
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskUiState(isLoading = true)
    )

    private fun filterTasks(list: List<Task>, filterType: TasksFilterType): List<Task> {
        val tasks = mutableListOf<Task>()

        list.forEach { task ->
            when (filterType) {
                TasksFilterType.ALL_TASKS -> {
                    tasks.add(task)
                }
                TasksFilterType.ACTIVE_TASKS -> {
                    if (!task.isCompleted) {
                        tasks.add(task)
                    }
                }
                TasksFilterType.COMPLETED_TASKS -> {
                    if (task.isCompleted) {
                        tasks.add(task)
                    }
                }
            }
        }

        return tasks
    }

    fun setFiltering(requestType: TasksFilterType) {
        savedStateHandle[TASKS_FILTER_SAVED_STATE_KEY] = requestType
    }

    fun updateTaskCompleted(task: Task, completed: Boolean) {
        viewModelScope.launch {
            repository.updateCompleted(task.id, completed)
        }
    }
}