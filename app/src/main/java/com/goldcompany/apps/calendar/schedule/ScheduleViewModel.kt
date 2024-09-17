package com.goldcompany.apps.calendar.schedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.apps.calendar.util.CURRENT_DATE_MILLI
import com.goldcompany.apps.calendar.util.SCHEDULE_ID
import com.goldcompany.apps.calendar.util.convertDateToMilli
import com.goldcompany.apps.data.data.schedule.Schedule
import com.goldcompany.apps.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

data class ScheduleUiState(
    val title: String = "",
    val description: String = "",
    val startDateMilli: Long = 0L,
    val startTimeHour: Int = 0,
    val startTimeMinute: Int = 0,
    val endDateMilli: Long = 0L,
    val endTimeHour: Int = 0,
    val endTimeMinute: Int = 0,
    val place: String = "",
    val isAllDay: Boolean = false,
    val isDone: Boolean = false,
    val isLoading: Boolean = false,
    val message: Int? = null
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val scheduleId: String? = savedStateHandle[SCHEDULE_ID]
    private val currentDateMilli: Long = savedStateHandle[CURRENT_DATE_MILLI] ?: LocalDate.now().convertDateToMilli()

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState

    init {
        if (scheduleId != null) {
            loadSchedule(scheduleId)
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    startDateMilli = currentDateMilli,
                    startTimeHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    startTimeMinute = Calendar.getInstance().get(Calendar.MINUTE),
                    endDateMilli = currentDateMilli,
                    endTimeHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1,
                    endTimeMinute = Calendar.getInstance().get(Calendar.MINUTE)
                )
            }
        }
    }

    private fun loadSchedule(scheduleId: String) {
        loading()

        viewModelScope.launch {
            repository.getSchedule(scheduleId)?.let { schedule ->
                _uiState.update {
                    it.copy(
                        title = schedule.title,
                        startDateMilli = schedule.startDateTimeMilli,
                        startTimeHour = schedule.startHour,
                        startTimeMinute = schedule.startMinute,
                        endDateMilli = schedule.endDateTimeMilli,
                        endTimeHour = schedule.endHour,
                        endTimeMinute = schedule.endMinute,
                        isAllDay = schedule.isAllDay
                    )
                }
            }
        }
    }

    private fun loading() {
        _uiState.update { it.copy(isLoading = true) }
    }

    private fun done() {
        _uiState.update {
            it.copy(
                isLoading = false,
                isDone = true
            )
        }
    }

    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun saveSchedule() {
        loading()

        if (scheduleId == null) {
            insertSchedule()
        } else {
            updateSchedule()
        }
    }

    private fun insertSchedule() {
        viewModelScope.launch {
            repository.insertSchedule(
                Schedule(
                    title = uiState.value.title,
                    description = uiState.value.description,
                    startDateTimeMilli = uiState.value.startDateMilli,
                    startHour = uiState.value.startTimeHour,
                    startMinute = uiState.value.startTimeMinute,
                    endDateTimeMilli = uiState.value.endDateMilli,
                    endHour = uiState.value.endTimeHour,
                    endMinute = uiState.value.endTimeMinute,
                    isAllDay = uiState.value.isAllDay,
                    place = uiState.value.place
                )
            )
            done()
        }
    }

    private fun updateSchedule() {
        viewModelScope.launch {
            repository.updateSchedule(
                Schedule(
                    id = scheduleId!!,
                    title = uiState.value.title,
                    description = uiState.value.description,
                    startDateTimeMilli = uiState.value.startDateMilli,
                    startHour = uiState.value.startTimeHour,
                    startMinute = uiState.value.startTimeMinute,
                    endDateTimeMilli = uiState.value.endDateMilli,
                    endHour = uiState.value.endTimeHour,
                    endMinute = uiState.value.endTimeMinute,
                    isAllDay = uiState.value.isAllDay,
                    place = uiState.value.place
                )
            )
            done()
        }
    }

    fun deleteSchedule() {
        viewModelScope.launch {
            repository.deleteSchedule(scheduleId!!.toLong())
            done()
        }
    }
}