package com.goldcompany.apps.calendar.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.apps.calendar.util.convertDateToMilli
import com.goldcompany.apps.data.data.schedule.Schedule
import com.goldcompany.apps.data.data.todo.Todo
import com.goldcompany.apps.data.repository.ScheduleRepository
import com.goldcompany.apps.data.repository.TodoRepository
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
    val monthlyTodos: Map<Long, List<Todo>> = mapOf(),
    val schedules: List<Schedule> = listOf(),
    val selectedDateMilli: Long = LocalDate.now().convertDateToMilli(),
    val startLocalDate: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _monthlyTodos = MutableStateFlow<Map<Long, List<Todo>>>(mapOf())
    private val _schedules = MutableStateFlow<List<Schedule>>(listOf())
    private val _selectedDateMilli = MutableStateFlow(LocalDate.now().convertDateToMilli())
    private val _startLocalDate = MutableStateFlow(LocalDate.now())
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<TaskUiState> = combine(
        _monthlyTodos, _schedules, _selectedDateMilli, _startLocalDate, _isLoading
    ) { tasks, schedules, selectedDateMilli, startLocalDate, isLoading ->
        TaskUiState(
            monthlyTodos = tasks,
            schedules = schedules,
            selectedDateMilli = selectedDateMilli,
            startLocalDate = startLocalDate,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskUiState(isLoading = true)
    )

    fun getMonthlyTodos(
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusMonths(1)
    ) {
        viewModelScope.launch {
            val map = mutableMapOf<Long, List<Todo>>()
            val list = todoRepository.getMonthlyTodos(
                startDate.convertDateToMilli(),
                endDate.convertDateToMilli()
            )

            list.map { task ->
                map[task.dateMilli] = list.filter { it.dateMilli == task.dateMilli }
            }

            _monthlyTodos.update { map }
        }
        _startLocalDate.update { startDate }
    }

    fun getSchedules(
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusMonths(1)
    ) {
        viewModelScope.launch {
            val schedules = scheduleRepository.getMonthlySchedules(
                startMilli = startDate.convertDateToMilli(),
                endMilli = endDate.convertDateToMilli()
            )

            _schedules.update { schedules }
        }
        _startLocalDate.update { startDate }
    }

    fun updateTodo(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            todoRepository.updateCompletedTodo(taskId.toLong(), isCompleted)
        }
    }

    fun selectDateMilli(milli: Long) {
        _selectedDateMilli.update { milli }
    }
}