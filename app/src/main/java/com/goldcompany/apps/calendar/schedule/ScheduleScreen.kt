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
import com.goldcompany.apps.calendar.schedule.compose.AlarmTimerBottomSheet
import com.goldcompany.apps.calendar.schedule.compose.ScheduleDateSelector
import com.goldcompany.apps.calendar.schedule.compose.ScheduleDateTimePicker
import com.goldcompany.apps.calendar.util.ALARM_BUNDLE_DESCRIPTION
import com.goldcompany.apps.calendar.util.ALARM_BUNDLE_TITLE
import com.goldcompany.apps.data.data.schedule.Schedule
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val schedule by viewModel.schedule.collectAsStateWithLifecycle()
    val showDialog = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            DetailScreenAppBar(
                taskTitle = stringResource(id = R.string.schedule),
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
            EditSchedule(
                modifier = Modifier.padding(paddingValue),
                schedule = schedule,
                alarmList = uiState.alarmList.toList(),
                updateTitle = viewModel::updateTitle,
                updateDescription = viewModel::updateDescription,
                updateStartDateMilli = viewModel::updateStartDateMilli,
                updateStartDateTime = viewModel::updateStartDateTime,
                updateEndDateMilli = viewModel::updateEndDateMilli,
                updateEndDateTime = viewModel::updateEndDateTime,
                setIsAllDay = viewModel::setIsAllDay,
                setTimer = viewModel::setTimer
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
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    LaunchedEffect(uiState.isDone) {
        if (uiState.isDone) {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra(ALARM_BUNDLE_TITLE, schedule.title)
                putExtra(ALARM_BUNDLE_DESCRIPTION, schedule.description)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            uiState.alarmList.forEach {
                if (it.checked) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        it.dateTime.atZone(ZoneId.systemDefault()).toEpochSecond(),
                        pendingIntent
                    )
                }
            }
            navigateBack()
        }
    }
}

@Composable
private fun EditSchedule(
    modifier: Modifier,
    schedule: Schedule,
    alarmList: List<AlarmItem>,
    updateTitle: (String) -> Unit,
    updateDescription: (String) -> Unit,
    updateStartDateMilli: (Long) -> Unit,
    updateStartDateTime: (Int, Int) -> Unit,
    updateEndDateMilli: (Long) -> Unit,
    updateEndDateTime: (Int, Int) -> Unit,
    setIsAllDay: (Boolean) -> Unit,
    setTimer: (Int) -> Unit
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
            text = schedule.title,
            onTextChange = { title -> updateTitle(title) },
            hintResource = R.string.title,
            isSingleLine = true
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.horizontal_margin)))
        AnimatedVisibility(visible = schedule.isAllDay) {
            Column {
                Row {
                    Text(
                        text = stringResource(id = R.string.date),
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ScheduleDateSelector(
                        savedDateMilli = schedule.startDateTimeMilli,
                        onDateChange = { milli ->
                            updateStartDateMilli(milli)
                        }
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.default_margin)))
                    ScheduleDateSelector(
                        savedDateMilli = schedule.endDateTimeMilli,
                        onDateChange = { milli ->
                            updateEndDateMilli(milli)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.horizontal_margin)))
            }
        }
        AnimatedVisibility(visible = !schedule.isAllDay) {
            Column {
                ScheduleDateTimePicker(
                    text = R.string.start_date,
                    dateMilli = schedule.startDateTimeMilli,
                    hour = schedule.startHour,
                    minute = schedule.startMinute,
                    onDateChange = { milli -> updateStartDateMilli(milli) },
                    onTimeChange = { h, m -> updateStartDateTime(h, m) }
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.default_margin)))
                ScheduleDateTimePicker(
                    text = R.string.end_date,
                    dateMilli = schedule.endDateTimeMilli,
                    hour = schedule.endHour,
                    minute = schedule.endMinute,
                    onDateChange = { milli -> updateEndDateMilli(milli) },
                    onTimeChange = { h, m -> updateEndDateTime(h, m) }
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.horizontal_margin)))
            }
        }
        AllDaySwitch(
            isAllDay = schedule.isAllDay,
            setIsAllDay = { check -> setIsAllDay(check) }
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.default_margin)))
        TaskTextInput(
            modifier = Modifier.fillMaxWidth(),
            text = schedule.description,
            onTextChange = { description -> updateDescription(description) },
            hintResource = R.string.description,
            isSingleLine = true
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.horizontal_margin)))
        AlarmButton(
            savedDateMilli = schedule.startDateTimeMilli,
            alarmList = alarmList,
            setTimer = setTimer
        )
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

@Composable
private fun AlarmButton(
    savedDateMilli: Long,
    alarmList: List<AlarmItem>,
    setTimer: (Int) -> Unit
) {
    val showBottomSheet = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showBottomSheet.value = true
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

    if (showBottomSheet.value) {
        AlarmTimerBottomSheet(
            showBottomSheet = showBottomSheet,
            alarmList = alarmList,
            setTimer = setTimer
        )
    }
}