package com.goldcompany.apps.todoapplication.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.goldcompany.apps.todoapplication.R
import com.goldcompany.apps.todoapplication.compose.DetailScreenAppBar
import com.goldcompany.apps.todoapplication.compose.TaskTextInput

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
        Column(
            modifier = Modifier
                .padding(paddingValue)
                .padding(all = dimensionResource(id = R.dimen.default_margin))
        ) {
            TaskTextInput(
                modifier = Modifier.fillMaxWidth(),
                text = "",
                onTextChange = {},
                hintResource = R.string.title,
                isSingleLine = true
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.vertical_margin)))

        }
    }
}