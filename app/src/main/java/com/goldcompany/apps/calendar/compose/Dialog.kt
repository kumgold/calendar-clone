package com.goldcompany.apps.calendar.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.goldcompany.apps.calendar.R

@Composable
fun DeleteCautionDialog(
    showDialog: MutableState<Boolean>,
    deleteTask: () -> Unit
) {
    Dialog(onDismissRequest = { showDialog.value = false }) {
        Card {
            Column(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.default_margin_large))
            ) {
                Text(
                    text = stringResource(id = R.string.delete_description),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(50.dp))
                Row {
                    TextButton(
                        modifier = Modifier.weight(1f),
                        onClick = { showDialog.value = false }
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    TextButton(
                        modifier = Modifier.weight(1f),
                        onClick = { deleteTask() }
                    ) {
                        Text(
                            text = stringResource(id = R.string.ok),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}