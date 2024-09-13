package com.goldcompany.apps.calendar.compose

import androidx.annotation.StringRes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun TaskTextInput(
    modifier: Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    @StringRes hintResource: Int,
    isSingleLine: Boolean

) {
    val textState = remember {
        mutableStateOf(
            TextFieldValue(
                text = text,
                selection = TextRange(text.length)
            )
        )
    }
    OutlinedTextField(
        modifier = modifier,
        shape = RoundedCornerShape(5.dp),
        value = textState.value,
        onValueChange = {
            onTextChange(it.text)
            textState.value = it
        },
        label = {
            Text(
                text = stringResource(id = hintResource),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = isSingleLine
    )
}