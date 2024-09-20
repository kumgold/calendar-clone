package com.goldcompany.apps.calendar.todo

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goldcompany.apps.calendar.R
import com.goldcompany.apps.calendar.compose.DeleteCautionDialog
import com.goldcompany.apps.calendar.compose.DetailScreenAppBar
import com.goldcompany.apps.calendar.compose.LoadingAnimation
import com.goldcompany.apps.calendar.compose.TaskDatePickerDialog
import com.goldcompany.apps.calendar.compose.TaskTextInput
import com.goldcompany.apps.calendar.util.convertMilliToDate

@Composable
fun TodoScreen(
    viewModel: TodoViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showDialog = remember { mutableStateOf(false) }
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
                taskTitle = uiState.title,
                isEdit = uiState.isEdit,
                deleteTask = {
                    showDialog.value = true
                },
                saveTask = {
                    viewModel.saveTodo()
                },
                navigateBack = navigateBack
            )
        }
    ) { paddingValue ->
        if (uiState.isLoading) {
            LoadingAnimation(modifier = Modifier.padding(paddingValue).wrapContentSize())
        } else {
            Todo(
                modifier = Modifier.padding(paddingValue).wrapContentSize(),
                title = uiState.title,
                description = uiState.description,
                dateMilli = uiState.dateMilli,
                onTitleChange = viewModel::updateTitle,
                onDescriptionChange = viewModel::updateDescription,
                onDateSelected = viewModel::updateDateMilli
            )
        }
    }

    if (showDialog.value) {
        DeleteCautionDialog(
            showDialog = showDialog,
            deleteTask = {
                viewModel.deleteTodo()
            }
        )
    }

    LaunchedEffect(uiState.isDone) {
        if (uiState.isDone) {
            navigateBack()
        }
    }
}

@Composable
private fun Todo(
    modifier: Modifier,
    title: String,
    description: String,
    dateMilli: Long,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateSelected: (Long) -> Unit
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
            Spacer(modifier = Modifier.weight(1f))
            TodoDateSelector(
                savedDateMilli = dateMilli,
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
    }
}

@Composable
private fun TodoDateSelector(
    modifier: Modifier = Modifier,
    savedDateMilli: Long,
    onDateSelected: (Long) -> Unit
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
            textAlign = TextAlign.Center
        )
    }

    if (isShowDatePickerDialog) {
        TaskDatePickerDialog(
            savedDateMilli = savedDateMilli,
            onDateChange = {
                date = it.convertMilliToDate()
                onDateSelected(it)
            },
            onDismiss = { isShowDatePickerDialog = false }
        )
    }
}
