package com.goldcompany.apps.todoapplication.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.data.repository.TaskRepository
import com.goldcompany.apps.todoapplication.R
import com.goldcompany.apps.todoapplication.TASK_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditTaskUiState(
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val message: Int? = null,
    val isTaskSaved: Boolean = false
)

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val taskId: String? = savedStateHandle[TASK_ID]

    private val _uiState = MutableStateFlow(AddEditTaskUiState())
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

    init {
        if (taskId != null) {
            loadTask(taskId)
        }
    }

    private fun loadTask(taskId: String) {
        loading()

        viewModelScope.launch {
            taskId.let { id ->
                repository.getTask(id).let { task ->
                    if (task != null) {
                        _uiState.update {
                            it.copy(
                                title = task.title,
                                description = task.description,
                                isCompleted = task.isCompleted,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loading() {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }
    }

    fun updateTaskCompleted() {
        if (taskId != null) {
            viewModelScope.launch {
                repository.updateCompleted(taskId, !_uiState.value.isCompleted)
            }
        }

        _uiState.update {
            it.copy(
                isCompleted = !it.isCompleted
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

    fun saveTask() {
        if (_uiState.value.title.isEmpty() || _uiState.value.description.isEmpty()) {
            _uiState.update {
                it.copy(message = R.string.please_check_your_input)
            }
            return
        }

        if (taskId == null) {
            addTask()
        } else {
            updateTask()
        }
    }

    private fun addTask() {
        loading()

        viewModelScope.launch {
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

    private fun updateTask() {
        loading()

        viewModelScope.launch {
            repository.updateTask(
                Task(
                    id = taskId!!,
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