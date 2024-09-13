package com.goldcompany.apps.calendar.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.apps.data.data.todo.Todo
import com.goldcompany.apps.data.repository.TodoRepository
import com.goldcompany.apps.calendar.util.convertDateToMilli
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
    val todos: SnapshotStateList<Todo> = mutableStateListOf(),
    val monthlyTodos: Map<Long, List<Todo>> = mapOf(),
    val selectedDateMilli: Long = LocalDate.now().convertDateToMilli(),
    val startLocalDate: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    private val _todos = MutableStateFlow<SnapshotStateList<Todo>>(mutableStateListOf())
    private val _monthlyTodos = MutableStateFlow<Map<Long, List<Todo>>>(mapOf())
    private val _selectedDateMilli = MutableStateFlow(LocalDate.now().convertDateToMilli())
    private val _startLocalDate = MutableStateFlow(LocalDate.now())
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<TaskUiState> = combine(
        _todos, _monthlyTodos, _selectedDateMilli, _startLocalDate, _isLoading
    ) { tasks, monthlyTasks, selectedDateMilli, startLocalDate, isLoading ->
        TaskUiState(
            todos = tasks.toMutableStateList(),
            monthlyTodos = monthlyTasks,
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
        getMonthlyTodos()
    }

    fun getMonthlyTodos(
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusMonths(1)
    ) {
        viewModelScope.launch {
            val map = mutableMapOf<Long, List<Todo>>()
            val list = repository.getMonthlyTodos(startDate.convertDateToMilli(), endDate.convertDateToMilli())

            list.map { task ->
                map[task.dateMilli] = list.filter { it.dateMilli == task.dateMilli }
            }

            _monthlyTodos.update { map }
        }
        _startLocalDate.update { startDate }
    }

    fun updateTodo(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateCompletedTodo(taskId.toLong(), isCompleted)
        }
    }

    fun selectDateMilli(milli: Long) {
        _selectedDateMilli.update { milli }
    }
}