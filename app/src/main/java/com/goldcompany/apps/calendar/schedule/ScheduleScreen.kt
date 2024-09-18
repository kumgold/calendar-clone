package com.goldcompany.apps.calendar.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goldcompany.apps.calendar.R
import com.goldcompany.apps.calendar.compose.DetailScreenAppBar
import com.goldcompany.apps.calendar.compose.LoadingAnimation
import com.goldcompany.apps.calendar.compose.TaskTextInput
import com.goldcompany.apps.calendar.schedule.compose.ScheduleDateTimePicker

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            DetailScreenAppBar(
                taskTitle = uiState.title,
                isEdit = uiState.isEdit,
                deleteTask = {
                    viewModel.deleteSchedule()
                },
                saveTask = {
                    viewModel.saveSchedule()
                },
                navigateBack = navigateBack
            )
        }
    ) { paddingValue ->
        if (uiState.isLoading) {
            LoadingAnimation(modifier = Modifier.padding(paddingValue).wrapContentSize())
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
                updateTitle = viewModel::updateTitle
            )
        }
    }

    LaunchedEffect(uiState.isDone) {
        if (uiState.isDone) {
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
    updateTitle: (String) -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    println("title = $title")

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
            onTextChange = { title ->
                updateTitle(title)
            },
            hintResource = R.string.title,
            isSingleLine = true
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.horizontal_margin)))
        ScheduleDateTimePicker(
            text = R.string.start_date,
            dateMilli = startDateMilli,
            hour = startTimeHour,
            minute = startTimeMinute,
            onDateChange = { date -> },
            onTimeChange = { h, m -> }
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.default_margin)))
        ScheduleDateTimePicker(
            text = R.string.end_date,
            dateMilli = endDateMilli,
            hour = endTimeHour,
            minute = endTimeMinute,
            onDateChange = { date -> },
            onTimeChange = { h, m -> }
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.horizontal_margin)))
    }
}