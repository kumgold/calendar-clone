package com.goldcompany.apps.calendar.schedule

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import javax.inject.Inject

@Stable
data class ScheduleUiState(
    val alarmList: SnapshotStateList<AlarmItem> = mutableStateListOf(),
    val isDone: Boolean = false,
    val isLoading: Boolean = false,
    val isEdit: Boolean = false,
    val message: Int? = null
)

data class AlarmItem(
    val displayText: String,
    val dateTime: LocalDateTime,
    val checked: Boolean
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
    private val _alarmList = MutableStateFlow(listOf(
        AlarmItem(
            "당일 오전 0시",
            Instant.ofEpochMilli(currentDateMilli)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay(),
            false
        ),
        AlarmItem(
            "당일 오전 8시",
            Instant.ofEpochMilli(currentDateMilli)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay()
                .plusHours(8),
            false
        ),
        AlarmItem(
            "당일 정오",
            Instant.ofEpochMilli(currentDateMilli)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay()
                .plusHours(12),
            false
        ),
        AlarmItem(
            "당일 이벤트 시작 5분 전",
            Instant.ofEpochMilli(currentDateMilli)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .minusMinutes(5),
            true
        ),
        AlarmItem(
            "당일 이벤트 시작 1시간 전",
            Instant.ofEpochMilli(currentDateMilli)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .minusHours(1),
            false
        ),
    ).toMutableStateList())

    val uiState: StateFlow<ScheduleUiState> = combine(
        _alarmList,
        _isDone,
        _isLoading,
        _isEdit,
        _message
    ) { list, isDone, isLoading, isEdit, message ->
        ScheduleUiState(
            list,
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

    fun updateStartDateMilli(milli: Long) {
        _schedule.update { it.copy(startDateTimeMilli = milli) }
    }

    fun updateStartDateTime(hour: Int, minute: Int) {
        _schedule.update {
            it.copy(
                startHour = hour,
                startMinute = minute
            )
        }
    }

    fun updateEndDateTime(hour: Int, minute: Int) {
        _schedule.update {
            it.copy(
                endHour = hour,
                endMinute = minute
            )
        }
    }

    fun updateEndDateMilli(milli: Long) {
        _schedule.update { it.copy(endDateTimeMilli = milli) }
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

    fun setTimer(index: Int) {
        _alarmList.update {
            it[index] = it[index].copy(checked = !it[index].checked)
            it
        }
    }
}