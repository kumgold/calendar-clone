package com.goldcompany.apps.calendar.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.goldcompany.apps.calendar.MainActivity
import com.goldcompany.apps.calendar.R

class TaskWidget : GlanceAppWidget() {

    companion object {
        const val KEY_TASK_ID = "TASK_ID"
        const val KEY_TASK_STATE = "TASK_STATE"

        private val smallMode = DpSize(90.dp, 120.dp)
        private val largeMode = DpSize(260.dp, 200.dp)

        fun getTaskIdParameterKey(): ActionParameters.Key<String> {
            return ActionParameters.Key(KEY_TASK_ID)
        }

        fun getTaskStateParameterKey(): ActionParameters.Key<Boolean> {
            return ActionParameters.Key(KEY_TASK_STATE)
        }
    }

    override val stateDefinition: GlanceStateDefinition<*>
        get() = PreferencesGlanceStateDefinition

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(smallMode, largeMode)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTaskWidget()
        }
    }

    @Composable
    private fun GlanceTaskWidget() {
        val size = LocalSize.current
        val context = LocalContext.current
        val preferences = currentState<Preferences>()

        val taskForWidget = TaskForWidget(
            id = preferences[TaskWidgetReceiver.currentTaskId].toString(),
            title = preferences[TaskWidgetReceiver.currentTaskTitle] ?: context.getString(R.string.app_widget_default_text),
            description = preferences[TaskWidgetReceiver.currentTaskDescription] ?: "",
            isCompleted = preferences[TaskWidgetReceiver.currentTaskState] ?: false
        )

        GlanceTheme {
            when (size) {
                smallMode -> SmallTask(taskForWidget = taskForWidget)
                largeMode -> LargeTask(taskForWidget = taskForWidget)
            }
        }
    }

    @Composable
    private fun SmallTask(taskForWidget: TaskForWidget) {
        Row(
            modifier = GlanceModifier.fillMaxSize()
                .background(Color.White)
                .padding(R.dimen.default_margin)
                .cornerRadius(R.dimen.default_corner_radius)
                .appWidgetBackground()
                .clickable(
                    actionStartActivity<MainActivity>()
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CheckBox(
                checked = taskForWidget.isCompleted,
                onCheckedChange = actionRunCallback<UpdateTaskCallback>(
                    actionParametersOf(
                        getTaskIdParameterKey() to taskForWidget.id,
                        getTaskStateParameterKey() to taskForWidget.isCompleted
                    )
                )
            )
            Text(
                modifier = GlanceModifier.fillMaxWidth()
                    .padding(R.dimen.default_margin),
                text = taskForWidget.title,
                style = TextStyle(
                    fontSize = 18.sp
                )
            )
        }
    }

    @Composable
    private fun LargeTask(taskForWidget: TaskForWidget) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(Color.White)
                .padding(R.dimen.default_margin)
                .cornerRadius(R.dimen.default_corner_radius)
                .appWidgetBackground(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                CheckBox(
                    checked = taskForWidget.isCompleted,
                    onCheckedChange = actionRunCallback<UpdateTaskCallback>(
                        actionParametersOf(
                            getTaskIdParameterKey() to taskForWidget.id,
                            getTaskStateParameterKey() to taskForWidget.isCompleted
                        )
                    )
                )
                Text(
                    modifier = GlanceModifier.fillMaxWidth()
                        .padding(R.dimen.default_margin),
                    text = taskForWidget.title,
                    style = TextStyle(
                        fontSize = 18.sp
                    )
                )
            }
            Text(
                text = taskForWidget.description,
                style = TextStyle(
                    fontSize = 15.sp
                )
            )
        }
    }
}

class UpdateTaskCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        /**
         * App data update
         */
        val id = parameters[TaskWidget.getTaskIdParameterKey()].toString()
        val isCompleted = !(parameters[TaskWidget.getTaskStateParameterKey()] ?: false)
        val intent = Intent(TaskWidgetReceiver.UPDATE_ACTION)
        intent.putExtra(TaskWidget.KEY_TASK_ID, id)
        intent.putExtra(TaskWidget.KEY_TASK_STATE, isCompleted)

        context.sendBroadcast(intent)

        /**
         * Glance Widget Update
         */
        updateAppWidgetState(context, glanceId) { preferences ->
            preferences[TaskWidgetReceiver.currentTaskState] = !(preferences[TaskWidgetReceiver.currentTaskState] ?: false)
        }

        TaskWidget().update(context, glanceId)
    }
}
