package com.goldcompany.apps.todoapplication.addedittask

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.goldcompany.apps.todoapplication.R
import com.goldcompany.apps.todoapplication.compose.TitleTopAppBar
import com.goldcompany.apps.todoapplication.util.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    modifier: Modifier = Modifier,
    viewModel: AddEditTaskViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TitleTopAppBar(
                title = R.string.add_task,
                navigateBack = onBack
            )
        }
    ) { paddingValues ->
        AddEditTaskContent(
            modifier = modifier.padding(paddingValues),
            loadingState = uiState.loadingState
        )
    }
}

@Composable
private fun AddEditTaskContent(
    modifier: Modifier,
    loadingState: LoadingState
) {
    when (loadingState) {
        LoadingState.INIT -> {

        }
        LoadingState.LOADING -> {

        }
        LoadingState.SUCCESS -> {
            EditTaskScreen(

            )
        }
        LoadingState.ERROR -> {

        }
    }
}

@Composable
private fun EditTaskScreen(

) {

}