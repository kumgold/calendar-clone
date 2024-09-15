package com.goldcompany.apps.calendar.schedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.goldcompany.apps.data.data.schedule.Schedule
import com.goldcompany.apps.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ScheduleUiState(
    val title: String = "",
    val startDateMilli: Long = 0L,
    val startTimeHour: Int = 0,
    val startTimeMinute: Int = 0,
    val endDateMilli: Long = 0L,
    val endTimeHour: Int = 0,
    val endTimeMinute: Int = 0,
    val isDone: Boolean = false,
    val isLoading: Boolean = false,
    val message: Int? = null
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState

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

    fun saveSchedule() {

    }
}