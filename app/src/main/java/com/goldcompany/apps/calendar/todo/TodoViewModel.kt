package com.goldcompany.apps.calendar.todo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.apps.data.data.todo.Todo
import com.goldcompany.apps.data.repository.TodoRepository
import com.goldcompany.apps.calendar.R
import com.goldcompany.apps.calendar.util.CURRENT_DATE_MILLI
import com.goldcompany.apps.calendar.util.TODO_ID
import com.goldcompany.apps.calendar.util.convertDateToMilli
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TodoUiState(
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val dateMilli: Long = LocalDate.now().convertDateToMilli(),
    val isLoading: Boolean = false,
    val isEdit: Boolean = false,
    val isDone: Boolean = false,
    val message: Int? = null
)

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val todoId: String? = savedStateHandle[TODO_ID]
    private val currentDateMilli: Long = savedStateHandle[CURRENT_DATE_MILLI] ?: LocalDate.now().convertDateToMilli()

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    init {
        if (todoId != null) {
            loadTodo(todoId)
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

    private fun loadTodo(taskId: String) {
        loading()

        viewModelScope.launch(Dispatchers.IO) {
            taskId.let { id ->
                repository.getTodo(id).let { task ->
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

    fun saveTodo() {
        if (_uiState.value.title.isEmpty()) {
            _uiState.update {
                it.copy(message = R.string.please_check_your_input)
            }
            return
        }

        if (todoId == null) {
            addTodo()
        } else {
            updateTodo()
        }
    }

    private fun addTodo() {
        loading()

        viewModelScope.launch(Dispatchers.IO) {
            repository.addTodo(
                Todo(
                    isCompleted = _uiState.value.isCompleted,
                    title = _uiState.value.title,
                    description = _uiState.value.description,
                    dateMilli = _uiState.value.dateMilli
                )
            )
            done()
        }
    }

    private fun updateTodo() {
        loading()

        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTodo(
                Todo(
                    id = todoId!!,
                    isCompleted = _uiState.value.isCompleted,
                    title = _uiState.value.title,
                    description = _uiState.value.description,
                    dateMilli = _uiState.value.dateMilli
                )
            )
            done()
        }
    }

    fun deleteTodo() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTodo(todoId!!.toLong())
            done()
        }
    }

    private fun done() {
        _uiState.update {
            it.copy(isDone = true)
        }
    }

    fun shownSnackBarMessage() {
        _uiState.update { it.copy(message = null) }
    }
}