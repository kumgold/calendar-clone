package com.goldcompany.apps.calendar.schedule.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.goldcompany.apps.calendar.R
import com.goldcompany.apps.data.data.schedule.Schedule
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

data class AlarmItem(
    val displayText: String,
    val dateTimeMilli: Long,
    val checked: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmTimerBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    schedule: Schedule,
    setTimer: (Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val timer = getTimerList(
        schedule.alarmList,
        schedule.startDateTimeMilli,
        schedule.startHour,
        schedule.startMinute,
        schedule.isAllDay
    )

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        sheetState = sheetState,
        onDismissRequest = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    showBottomSheet.value = false
                }
            }
        }
    ) {
        Column {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.alarm),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet.value = false
                                }
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }
            )
            LazyColumn(
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.default_margin))
            ) {
                itemsIndexed(timer) {index, item ->
                    val checked = remember { mutableStateOf(item.checked) }
                    Row(
                        modifier = Modifier
                            .padding(all = dimensionResource(id = R.dimen.default_margin))
                            .clickable {
                                checked.value = !checked.value
                                setTimer(item.dateTimeMilli)
                            }
                    ) {
                        Text(
                            text = item.displayText,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (checked.value) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getTimerList(
    alarmList: List<Long>,
    currentDateMilli: Long,
    hour: Int,
    minute: Int,
    isAllDay: Boolean
): List<AlarmItem> {
    val date = Instant.ofEpochMilli(currentDateMilli).atZone(ZoneId.systemDefault()).toLocalDate()
    val c = Calendar.getInstance().apply {
        set(date.year, date.monthValue-1, date.dayOfMonth)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val list = mutableListOf(
        AlarmItem(
            "당일 오전 0시",
            c.timeInMillis,
            alarmList.contains(c.timeInMillis)
        ),
        AlarmItem(
            "당일 오전 8시",
            c.apply {
                set(Calendar.HOUR_OF_DAY, 8)
            }.timeInMillis,
            alarmList.contains(
                c.apply {
                    set(Calendar.HOUR_OF_DAY, 8)
                }.timeInMillis
            )
        ),
        AlarmItem(
            "당일 정오",
            c.apply {
                set(Calendar.HOUR_OF_DAY, 12)
            }.timeInMillis,
            alarmList.contains(
                c.apply {
                    set(Calendar.HOUR_OF_DAY, 12)
                }.timeInMillis
            )
        )
    )

    if (!isAllDay) {
        list += listOf(
            AlarmItem(
                "당일 이벤트 시작 5분 전",
                c.apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute - 5)
                }.timeInMillis,
                alarmList.contains(
                    c.apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute - 5)
                    }.timeInMillis
                )
            ),
            AlarmItem(
                "당일 이벤트 시작 1시간 전",
                c.apply {
                    set(Calendar.HOUR_OF_DAY, hour-1)
                    set(Calendar.MINUTE, minute)
                }.timeInMillis,
                alarmList.contains(
                    c.apply {
                        set(Calendar.HOUR_OF_DAY, hour-1)
                        set(Calendar.MINUTE, minute)
                    }.timeInMillis
                )
            )
        )
    }

    return list
}