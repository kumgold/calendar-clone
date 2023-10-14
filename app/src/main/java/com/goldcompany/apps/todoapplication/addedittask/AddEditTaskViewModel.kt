package com.goldcompany.apps.todoapplication.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.data.repository.TaskRepository
import com.goldcompany.apps.todoapplication.TASK_ID
import com.goldcompany.apps.todoapplication.util.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditTaskUiState(
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val loadingState: LoadingState = LoadingState.INIT,
    val isTaskSaved: Boolean = false
)

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val taskId: String? = savedStateHandle[TASK_ID]

    private val _uiState = MutableStateFlow(AddEditTaskUiState())
    val uiState: StateFlow<AddEditTaskUiState> = _uiState

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            taskId?.let { id ->
                loading()
                // Task ID is not null
                // Load task and edit it
                repository.getTask(id).map { task ->
                    _uiState.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            isCompleted = task.isCompleted,
                            loadingState = LoadingState.SUCCESS
                        )
                    }
                }
            }.run {
                // Task ID is null
                // Add new task
                _uiState.update {
                    it.copy(
                        loadingState = LoadingState.SUCCESS
                    )
                }
            }
        }
    }

    private fun loading() {
        _uiState.update {
            it.copy(
                loadingState = LoadingState.LOADING
            )
        }
    }

    fun updateTaskCompleted(newCompleted: Boolean) {
        _uiState.update {
            it.copy(
                isCompleted = newCompleted
            )
        }
    }

    fun updateTitle(newTitle: String) {
        _uiState.update {
            it.copy(
                title = newTitle
            )
        }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update {
            it.copy(
                description = newDescription
            )
        }
    }

    fun updateTask() {
        viewModelScope.launch {
            loading()

            repository.addTask(
                Task(
                    isCompleted = _uiState.value.isCompleted,
                    title = _uiState.value.title,
                    description = _uiState.value.description,
                    startTimeMilli = null,
                    endTimeMilli = null
                )
            )
            _uiState.update {
                it.copy(
                    isTaskSaved = true
                )
            }
        }
    }
}