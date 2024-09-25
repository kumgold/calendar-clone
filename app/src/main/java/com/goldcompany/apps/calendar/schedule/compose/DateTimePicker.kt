package com.goldcompany.apps.calendar.schedule.compose

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.goldcompany.apps.calendar.R
import com.goldcompany.apps.calendar.compose.TaskDatePickerDialog
import com.goldcompany.apps.calendar.util.convertMilliToDate
import com.goldcompany.apps.calendar.util.getDateString
import java.util.Calendar

@Composable
fun ScheduleDateTimePicker(
    @StringRes text: Int,
    dateMilli: Long,
    hour: Int,
    minute: Int,
    onDateChange: (Long) -> Boolean,
    onTimeChange: (Int, Int) -> Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = text),
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        ScheduleDateSelector(
            savedDateMilli = dateMilli,
            onDateChange = onDateChange
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.default_margin)))
        ScheduleTimeSelector(
            hour = hour,
            minute = minute,
            onDateTimeChange = onTimeChange
        )
    }
}

@Composable
fun ScheduleDateSelector(
    modifier: Modifier = Modifier,
    savedDateMilli: Long,
    onDateChange: (Long) -> Boolean
) {
    var date by remember { mutableStateOf(savedDateMilli.convertMilliToDate()) }
    var isShowDatePickerDialog by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .wrapContentSize()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(5.dp)
            )
            .clip(RoundedCornerShape(5.dp))
            .clickable {
                isShowDatePickerDialog = true
            }
            .padding(horizontal = dimensionResource(id = R.dimen.default_margin)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }

    if (isShowDatePickerDialog) {
        TaskDatePickerDialog(
            savedDateMilli = savedDateMilli,
            onDateChange = {
                if (onDateChange(it)) {
                    date = it.convertMilliToDate()
                }
            },
            onDismiss = { isShowDatePickerDialog = false }
        )
    }
}

@Composable
private fun ScheduleTimeSelector(
    modifier: Modifier = Modifier,
    hour: Int,
    minute: Int,
    onDateTimeChange: (Int, Int) -> Boolean
) {
    val time = remember {
        mutableStateOf("${hour.getDateString()}:${minute.getDateString()}")
    }
    var isShowDatePickerDialog by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .wrapContentSize()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(5.dp)
            )
            .clip(RoundedCornerShape(5.dp))
            .clickable {
                isShowDatePickerDialog = true
            }
            .padding(horizontal = dimensionResource(id = R.dimen.default_margin)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time.value,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }

    if (isShowDatePickerDialog) {
        Dialog(
            onDismissRequest = { isShowDatePickerDialog = false }
        ) {
            ScheduleTimePickerDialog(
                time = time,
                hour = hour,
                minute = minute,
                onTimeChange = { h, m ->
                    onDateTimeChange(h, m)
                },
                onDismiss = { isShowDatePickerDialog = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleTimePickerDialog(
    time: MutableState<String>,
    hour: Int,
    minute: Int,
    onTimeChange: (Int, Int) -> Boolean,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = false,
    )

    Column(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(all = dimensionResource(id = R.dimen.default_margin_large))
    ) {
        TimePicker(
            modifier = Modifier.fillMaxWidth(),
            state = timePickerState
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    if (onTimeChange(timePickerState.hour, timePickerState.minute)) {
                        time.value = "${timePickerState.hour.getDateString()}:${timePickerState.minute.getDateString()}"
                    }
                    onDismiss()
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.save),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.horizontal_margin)))
            Button(
                modifier = Modifier.weight(1f),
                onClick = { onDismiss() },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}