package com.goldcompany.apps.todoapplication.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.goldcompany.apps.todoapplication.R

@Composable
fun TopAppBars(

) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Filled.List,
                contentDescription = stringResource(id = R.string.menu)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.wrapContentSize(Alignment.TopEnd)
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.all_tasks))
                },
                onClick = {  }
            )
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.active_tasks))
                },
                onClick = {  }
            )
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.completed_tasks))
                },
                onClick = {  }
            )
        }
    }
}