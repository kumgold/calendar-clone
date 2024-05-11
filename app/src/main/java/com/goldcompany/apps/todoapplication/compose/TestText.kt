package com.goldcompany.apps.todoapplication.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Preview
@Composable
private fun TextFieldTest(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            var text by remember { mutableStateOf("TextField Test") }
//
//            OutlinedTextField(
//                value = text,
//                onValueChange = { text = it },
//                trailingIcon = {
//                    IconButton(onClick = { /*TODO*/ }) {
//                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
//                    }
//                },
//            )

            var text by rememberSaveable { mutableStateOf("") }

            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { text = "" }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = null,
                        )
                    }
                },
                textStyle = TextStyle(
                    fontSize = 20.sp
                )
            )
        }
    }
}