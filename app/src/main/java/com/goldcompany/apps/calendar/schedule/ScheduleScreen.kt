package com.goldcompany.apps.calendar.schedule

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goldcompany.apps.calendar.R
import com.goldcompany.apps.calendar.alarm.AlarmReceiver
import com.goldcompany.apps.calendar.compose.DeleteCautionDialog
import com.goldcompany.apps.calendar.compose.DetailScreenAppBar
import com.goldcompany.apps.calendar.compose.LoadingAnimation
import com.goldcompany.apps.calendar.compose.TaskTextInput
import com.goldcompany.apps.calendar.schedule.compose.ScheduleDateSelector
import com.goldcompany.apps.calendar.schedule.compose.ScheduleDateTimePicker
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showDialog = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            DetailScreenAppBar(
                taskTitle = uiState.title,
                isEdit = uiState.isEdit,
                deleteTask = {
                    showDialog.value = true
                },
                saveTask = {
                    viewModel.saveSchedule()
                },
                navigateBack = navigateBack
            )
        }
    ) { paddingValue ->
        if (uiState.isLoading) {
            LoadingAnimation(modifier = Modifier
                .padding(paddingValue)
                .wrapContentSize())
        } else {
            Schedule(
                modifier = Modifier.padding(paddingValue),
                title = uiState.title,
                startDateMilli = uiState.startDateMilli,
                startTimeHour = uiState.startTimeHour,
                startTimeMinute = uiState.startTimeMinute,
                endDateMilli = uiState.endDateMilli,
                endTimeHour = uiState.endTimeHour,
                endTimeMinute = uiState.endTimeMinute,
                isAllDay = uiState.isAllDay,
                updateTitle = viewModel::updateTitle,
                updateStartDateMilli = viewModel::updateStartDateMilli,
                updateStartDateTime = viewModel::updateStartDateTime,
                updateEndDateMilli = viewModel::updateEndDateMilli,
                updateEndDateTime = viewModel::updateEndDateTime,
                setIsAllDay = viewModel::setIsAllDay
            )
        }
    }

    if (showDialog.value) {
        DeleteCautionDialog(
            showDialog = showDialog,
            deleteTask = {
                viewModel.deleteSchedule()
            }
        )
    }

    val context = LocalContext.current
    LaunchedEffect(uiState.isDone) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("MESSAGE", uiState.title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        if (uiState.isDone) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                LocalDateTime.now().plusSeconds(2).atZone(ZoneId.systemDefault()).toEpochSecond(),
                pendingIntent
            )
            navigateBack()
        }
    }
}

@Composable
private fun Schedule(
    modifier: Modifier,
    title: String,
    startDateMilli: Long,
    startTimeHour: Int,
    startTimeMinute: Int,
    endDateMilli: Long,
    endTimeHour: Int,
    endTimeMinute: Int,
    isAllDay: Boolean,
    updateTitle: (String) -> Unit,
    updateStartDateMilli: (Long) -> Unit,
    updateStartDateTime: (Int, Int) -> Unit,
    updateEndDateMilli: (Long) -> Unit,
    updateEndDateTime: (Int, Int) -> Unit,
    setIsAllDay: (Boolean) -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
        keyboard?.show()
    }

    Column(
        modifier = modifier.padding(all = dimensionResource(id = R.dimen.default_margin))
    ) {
        TaskTextInput(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            text = title,
            onTextChange = { title -> updateTitle(title) },
            hintResource = R.string.title,
            isSingleLine = true
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.horizontal_margin)))
        AnimatedVisibility(visible = isAllDay) {
            Column {
                Row {
                    Text(
                        text = stringResource(id = R.string.date),
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ScheduleDateSelector(
                        savedDateMilli = startDateMilli,
                        onDateChange = { milli ->
                            updateStartDateMilli(milli)
                        }
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.default_margin)))
                    ScheduleDateSelector(
                        savedDateMilli = endDateMilli,
                        onDateChange = { milli ->
                            updateEndDateMilli(milli)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.horizontal_margin)))
            }
        }
        AnimatedVisibility(visible = !isAllDay) {
            Column {
                ScheduleDateTimePicker(
                    text = R.string.start_date,
                    dateMilli = startDateMilli,
                    hour = startTimeHour,
                    minute = startTimeMinute,
                    onDateChange = { milli -> updateStartDateMilli(milli) },
                    onTimeChange = { h, m -> updateStartDateTime(h, m) }
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.default_margin)))
                ScheduleDateTimePicker(
                    text = R.string.end_date,
                    dateMilli = endDateMilli,
                    hour = endTimeHour,
                    minute = endTimeMinute,
                    onDateChange = { milli -> updateEndDateMilli(milli) },
                    onTimeChange = { h, m -> updateEndDateTime(h, m) }
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.horizontal_margin)))
            }
        }
        AllDaySwitch(
            isAllDay = isAllDay,
            setIsAllDay = { check -> setIsAllDay(check) }
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.horizontal_margin)))
        AlarmButton(savedDateMilli = startDateMilli)
    }
}

@Composable
private fun AllDaySwitch(
    isAllDay: Boolean,
    setIsAllDay: (Boolean) -> Unit
) {
    var checked by remember { mutableStateOf(isAllDay) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.all_day),
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = {
                checked = !checked
                setIsAllDay(checked)
            }
        )
    }
}

@SuppressLint("ScheduleExactAlarm")
@Composable
private fun AlarmButton(
    savedDateMilli: Long,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.alarm),
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}