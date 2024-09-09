package com.goldcompany.apps.todoapplication.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.apps.data.data.Task
import com.goldcompany.apps.data.repository.TaskRepository
import com.goldcompany.apps.todoapplication.R
import com.goldcompany.apps.todoapplication.util.CURRENT_DATE_MILLI
import com.goldcompany.apps.todoapplication.util.TASK_ID
import com.goldcompany.apps.todoapplication.util.dateToMilli
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TaskUiState(
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val dateMilli: Long = LocalDate.now().dateToMilli(),
    val isLoading: Boolean = false,
    val isEdit: Boolean = false,
    val isDone: Boolean = false,
    val message: Int? = null
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val taskId: String? = savedStateHandle[TASK_ID]
    private val currentDateMilli: Long = savedStateHandle[CURRENT_DATE_MILLI] ?: LocalDate.now().dateToMilli()

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        if (taskId != null) {
            loadTask(taskId)
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isEdit = false,
                    dateMilli = currentDateMilli,
                )
            }
        }
    }

    private fun loadTask(taskId: String) {
        loading()

        viewModelScope.launch(Dispatchers.IO) {
            taskId.let { id ->
                repository.getTask(id).let { task ->
                    if (task != null) {
                        _uiState.update {
                            it.copy(
                                title = task.title,
                                description = task.description,
                                isCompleted = task.isCompleted,
                                dateMilli = task.dateMilli,
                                isLoading = false,
                                isEdit = true,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loading() {
        _uiState.update {
            it.copy(isLoading = true)
        }
    }

    fun updateTitle(newTitle: String) {
        _uiState.update {
            it.copy(title = newTitle)
        }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update {
            it.copy(description = newDescription)
        }
    }

    fun updateDateMilli(milli: Long) {
        _uiState.update {
            it.copy(dateMilli = milli,)
        }
    }

    fun saveTask() {
        if (_uiState.value.title.isEmpty()) {
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

        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(
                Task(
                    isCompleted = _uiState.value.isCompleted,
                    title = _uiState.value.title,
                    description = _uiState.value.description,
                    dateMilli = _uiState.value.dateMilli
                )
            )
            done()
        }
    }

    private fun updateTask() {
        loading()

        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(
                Task(
                    id = taskId!!,
                    isCompleted = _uiState.value.isCompleted,
                    title = _uiState.value.title,
                    description = _uiState.value.description,
                    dateMilli = _uiState.value.dateMilli
                )
            )
            done()
        }
    }

    fun deleteTask() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(taskId!!.toLong())
            done()
        }
    }

    private fun done() {
        _uiState.update {
            it.copy(isDone = true)
        }
    }
}