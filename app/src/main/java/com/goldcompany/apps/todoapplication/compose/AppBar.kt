package com.goldcompany.apps.todoapplication.compose

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.goldcompany.apps.todoapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailAppBar(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    deleteTask: () -> Unit,
    navigateBack: () -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(id = title)
            )
        },
        navigationIcon = {
            IconButton(
                onClick = navigateBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            TextButton(
                onClick = {
                    deleteTask()
                }
            ) {
                Text(text = stringResource(id = R.string.delete))
            }
        }
    )
}