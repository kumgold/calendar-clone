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
    val currentDateMilli: Long = LocalDate.now().convertDateToMilli(),
    val currentMonthLocalDate: LocalDate = LocalDate.now(),
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
    private val _currentDateMilli = MutableStateFlow(LocalDate.now().convertDateToMilli())
    private val _currentMonthLocalDate = MutableStateFlow(LocalDate.now())
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<TaskUiState> = combine(
        _monthlyTodos, _schedules, _currentDateMilli, _currentMonthLocalDate, _isLoading
    ) { tasks, schedules, currentDateMilli, currentMonthLocalDate, isLoading ->
        TaskUiState(
            monthlyTodos = tasks,
            schedules = schedules,
            currentDateMilli = currentDateMilli,
            currentMonthLocalDate = currentMonthLocalDate,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskUiState(isLoading = true)
    )

    fun getMonthlyTodos() {
        val startDate = _currentMonthLocalDate.value
        val endDate = _currentMonthLocalDate.value.plusMonths(1)

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
    }

    fun getSchedules() {
        val startDate = _currentMonthLocalDate.value
        val endDate = _currentMonthLocalDate.value.plusMonths(1)

        viewModelScope.launch {
            val schedules = scheduleRepository.getMonthlySchedules(
                startMilli = startDate.convertDateToMilli(),
                endMilli = endDate.convertDateToMilli()
            )

            _schedules.update { schedules }
        }
    }

    fun updateTodo(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            todoRepository.updateCompletedTodo(taskId.toLong(), isCompleted)
        }
    }

    fun setCurrentDateMilli(milli: Long) {
        _currentDateMilli.update { milli }
    }

    fun setCurrentMonthDate(currentMonth: LocalDate) {
        _currentMonthLocalDate.update { currentMonth }
    }
}