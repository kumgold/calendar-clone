package com.goldcompany.apps.calendar.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.goldcompany.apps.calendar.R
import com.goldcompany.apps.calendar.compose.DetailScreenAppBar
import com.goldcompany.apps.calendar.compose.TaskTextInput
import com.goldcompany.apps.calendar.schedule.compose.ScheduleDateTimePicker
import com.goldcompany.apps.calendar.util.convertDateToMilli
import java.time.LocalDate
import java.util.Calendar

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            DetailScreenAppBar(
                isEdit = false,
                deleteTask = {},
                saveTask = {},
                navigateBack = navigateBack
            )
        }
    ) { paddingValue ->

        val keyboard = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(focusRequester) {
            focusRequester.requestFocus()
            keyboard?.show()
        }

        Column(
            modifier = Modifier
                .padding(paddingValue)
                .padding(all = dimensionResource(id = R.dimen.default_margin))
        ) {
            TaskTextInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                text = "",
                onTextChange = {},
                hintResource = R.string.title,
                isSingleLine = true
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.horizontal_margin)))
            ScheduleDateTimePicker(
                text = R.string.start_date,
                dateMilli = LocalDate.now().convertDateToMilli(),
                hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                minute = Calendar.getInstance().get(Calendar.MINUTE),
                onDateChange = { date -> },
                onTimeChange = { h, m -> }
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.default_margin)))
            ScheduleDateTimePicker(
                text = R.string.end_date,
                dateMilli = LocalDate.now().convertDateToMilli(),
                hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1,
                minute = Calendar.getInstance().get(Calendar.MINUTE),
                onDateChange = { date -> },
                onTimeChange = { h, m -> }
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.horizontal_margin)))
        }
    }
}