package com.goldcompany.apps.calendar.schedule

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goldcompany.apps.calendar.R
import com.goldcompany.apps.calendar.util.CURRENT_DATE_MILLI
import com.goldcompany.apps.calendar.util.SCHEDULE_ID
import com.goldcompany.apps.calendar.util.convertDateToMilli
import com.goldcompany.apps.data.data.schedule.Schedule
import com.goldcompany.apps.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

@Stable
data class ScheduleUiState(
    val isDone: Boolean = false,
    val isLoading: Boolean = false,
    val isEdit: Boolean = false,
    val message: Int? = null
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val scheduleId: String? = savedStateHandle[SCHEDULE_ID]
    private val currentDateMilli: Long = savedStateHandle[CURRENT_DATE_MILLI] ?: LocalDate.now().convertDateToMilli()

    private val _schedule = MutableStateFlow(Schedule())
    val schedule: StateFlow<Schedule> = _schedule.asStateFlow()

    private val _isDone = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)
    private val _isEdit = MutableStateFlow(false)
    private val _message = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<ScheduleUiState> = combine(
        _isDone,
        _isLoading,
        _isEdit,
        _message
    ) { isDone, isLoading, isEdit, message ->
        ScheduleUiState(
            isDone,
            isLoading,
            isEdit,
            message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1000),
        initialValue = ScheduleUiState(isLoading = true)
    )

    init {
        if (scheduleId != null) {
            loadSchedule(scheduleId)
        } else {
            _schedule.update {
                it.copy(
                    startDateTimeMilli = currentDateMilli,
                    startHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    startMinute = Calendar.getInstance().get(Calendar.MINUTE),
                    endDateTimeMilli = currentDateMilli,
                    endHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1,
                    endMinute = Calendar.getInstance().get(Calendar.MINUTE)
                )
            }
        }
        _isLoading.update { false }
    }

    private fun loadSchedule(scheduleId: String) {
        loading()

        viewModelScope.launch(Dispatchers.IO) {
            repository.getSchedule(scheduleId)?.let { schedule ->
                _schedule.update { schedule }
                _isEdit.update { true }
            }
        }
    }

    private fun loading() {
        _isLoading.update { true }
    }

    private fun done() {
        _isDone.update { true }
    }

    fun updateTitle(newTitle: String) {
        _schedule.update { it.copy(title = newTitle) }
    }

    fun updateDescription(description: String) {
        _schedule.update { it.copy(description = description) }
    }

    fun updateStartDateMilli(milli: Long): Boolean {
        if (milli > schedule.value.endDateTimeMilli) {
            _message.value = R.string.end_date_time_error
            return false
        }
        _schedule.update { it.copy(startDateTimeMilli = milli) }

        return true
    }

    fun updateStartDateTime(hour: Int, minute: Int): Boolean {
        val s = schedule.value
        if (s.startDateTimeMilli == s.endDateTimeMilli) {
            if ((hour == s.endHour && minute > s.endMinute) || hour > s.endHour) {
                _message.value = R.string.end_date_time_error
                return false
            }
        }
        _schedule.update {
            it.copy(
                startHour = hour,
                startMinute = minute
            )
        }

        return true
    }

    fun updateEndDateTime(hour: Int, minute: Int): Boolean {
        val s = schedule.value
        if (s.startDateTimeMilli == s.endDateTimeMilli) {
            if ((hour == s.startHour && minute < s.startMinute) || hour < s.startHour) {
                _message.value = R.string.end_date_time_error
                return false
            }
        }
        _schedule.update {
            it.copy(
                endHour = hour,
                endMinute = minute
            )
        }

        return true
    }

    fun updateEndDateMilli(milli: Long): Boolean {
        if (milli < schedule.value.startDateTimeMilli) {
            _message.value = R.string.end_date_time_error
            return false
        }
        _schedule.update { it.copy(endDateTimeMilli = milli) }

        return true
    }

    fun setIsAllDay(check: Boolean) {
        _schedule.update {
            it.copy(isAllDay = check)
        }
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
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSchedule(schedule.value)
            done()
        }
    }

    private fun updateSchedule() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSchedule(schedule.value)
            done()
        }
    }

    fun deleteSchedule() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSchedule(scheduleId!!.toLong())
            done()
        }
    }

    fun setTimer(timer: Long) {
        if (_schedule.value.alarmList.contains(timer)) {
            _schedule.value.alarmList.remove(timer)
        } else {
            _schedule.value.alarmList.add(timer)
        }
    }

    fun shownMessage() {
        _message.value = null
    }
}