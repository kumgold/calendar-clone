package com.goldcompany.apps.todoapplication.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goldcompany.apps.data.util.convertMilliToDate
import com.goldcompany.apps.todoapplication.R
import com.goldcompany.apps.todoapplication.compose.DetailScreenAppBar
import com.goldcompany.apps.todoapplication.compose.LoadingAnimation
import com.goldcompany.apps.todoapplication.compose.TaskTextInput
import com.goldcompany.apps.todoapplication.util.dateToMilli
import java.time.LocalDate

@Composable
fun TaskScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        snackbarHost = {
            SnackbarHost(hostState = snackBarState)
        },
        topBar = {
            DetailScreenAppBar(
                isEdit = uiState.isEdit,
                deleteTask = {
                    viewModel.deleteTask()
                },
                saveTask = {
                    viewModel.saveTask()
                },
                navigateBack = navigateBack
            )
        }
    ) { paddingValue ->
        if (uiState.isLoading) {
            LoadingAnimation(modifier = Modifier.padding(paddingValue).wrapContentSize())
        } else {
            EditTask(
                modifier = Modifier.padding(paddingValue).wrapContentSize(),
                title = uiState.title,
                description = uiState.description,
                dateMilli = uiState.dateMilli,
                onTitleChange = viewModel::updateTitle,
                onDescriptionChange = viewModel::updateDescription,
                saveTask = {
                    viewModel.saveTask()
                },
                navigateBack = navigateBack,
                onDateSelected = viewModel::updateDateMilli
            )
        }
    }

    uiState.message?.let { message ->
        val snackBarMessage = stringResource(id = message)
        LaunchedEffect(uiState.message) {
            snackBarState.showSnackbar(snackBarMessage)
            viewModel.shownSnackBarMessage()
        }
    }

    LaunchedEffect(key1 = uiState.isDone) {
        if (uiState.isDone) {
            navigateBack()
        }
    }
}

@Composable
private fun EditTask(
    modifier: Modifier,
    title: String,
    description: String,
    dateMilli: Long,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    saveTask: () -> Unit,
    navigateBack: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    Column(
        modifier = modifier.padding(all = dimensionResource(id = R.dimen.default_margin))
    ) {
        TaskTextInput(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            onTextChange = onTitleChange,
            hintResource = R.string.title,
            isSingleLine = true
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.horizontal_margin)))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.date),
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.default_margin)))
            TaskDateSelector(
                savedDate = convertMilliToDate(dateMilli),
                onDateSelected = onDateSelected
            )
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.vertical_margin)))
        TaskTextInput(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            text = description,
            onTextChange = onDescriptionChange,
            hintResource = R.string.description,
            isSingleLine = false
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.vertical_margin)))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = { saveTask() },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = stringResource(id = R.string.save))
            }
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.horizontal_margin)))
            Button(
                modifier = Modifier.weight(1f),
                onClick = { navigateBack() },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    }
}

@Composable
private fun TaskDateSelector(
    modifier: Modifier = Modifier,
    savedDate: String,
    onDateSelected: (Long) -> Unit
) {
    var date by remember { mutableStateOf(savedDate) }
    var isShowDatePickerDialog by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .wrapContentSize()
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.black),
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
            textAlign = TextAlign.Center
        )
    }

    if (isShowDatePickerDialog) {
        TaskDatePickerDialog(
            onDateSelected = {
                date = convertMilliToDate(it)
                onDateSelected(it)
            },
            onDismiss = { isShowDatePickerDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDatePickerDialog(
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    val currentTime = LocalDate.now().dateToMilli()

                    onDateSelected(selectedDate ?: currentTime)
                    onDismiss()
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
