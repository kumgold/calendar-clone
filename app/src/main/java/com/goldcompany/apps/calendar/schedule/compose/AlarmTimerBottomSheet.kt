package com.goldcompany.apps.calendar.schedule.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.goldcompany.apps.calendar.R
import com.goldcompany.apps.calendar.schedule.AlarmItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmTimerBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    alarmList: List<AlarmItem>,
    setTimer: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        sheetState = sheetState,
        onDismissRequest = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    showBottomSheet.value = false
                }
            }
        }
    ) {
        Column {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.alarm),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet.value = false
                                }
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }
            )
            LazyColumn(
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.default_margin))
            ) {
                itemsIndexed(alarmList) {index, item ->
                    val checked = remember { mutableStateOf(item.checked) }
                    Row(
                        modifier = Modifier
                            .padding(all = dimensionResource(id = R.dimen.default_margin))
                            .clickable {
                                checked.value = !checked.value
                                setTimer(index)
                            }
                    ) {
                        Text(
                            text = item.displayText,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (checked.value) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}