package com.goldcompany.apps.todoapplication.task

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetTaskScreen(
    bottomSheetState: MutableState<Boolean>
) {
    ModalBottomSheet(
        onDismissRequest = {
            bottomSheetState.value = false
        }
    ) {
        TaskScreen {

        }
    }
}