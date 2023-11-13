package com.goldcompany.apps.todoapplication.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.goldcompany.apps.todoapplication.R

class TaskAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            TaskContents()
        }
    }

    @Composable
    private fun TaskContents() {
        var isChecked by remember { mutableStateOf(false) }

        Row(
            modifier = GlanceModifier.fillMaxSize()
                .background(Color.White)
                .padding(R.dimen.default_margin)
                .cornerRadius(R.dimen.default_corner_radius)
                .appWidgetBackground(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CheckBox(
                checked = isChecked,
                onCheckedChange = {
                    isChecked = !isChecked
                }
            )
            Text(
                modifier = GlanceModifier.fillMaxWidth()
                    .padding(R.dimen.default_margin),
                text = LocalContext.current.getString(R.string.app_widget_default_text),
                style = TextStyle(
                    fontSize = 15.sp
                )
            )
        }
    }
}

class TaskAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TaskAppWidget()
}