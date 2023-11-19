package com.goldcompany.apps.todoapplication.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.goldcompany.apps.data.usecase.GetTasksUseCase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class TaskWidgetReceiver : GlanceAppWidgetReceiver() {
    private val coroutineScope = MainScope()

    @Inject
    lateinit var getTasksUseCase: GetTasksUseCase

    override val glanceAppWidget: GlanceAppWidget = TaskWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        observeTasks(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == UpdateTaskCallback.UPDATE_ACTION) {
            observeTasks(context)
        }
    }

    private fun observeTasks(context: Context) {
        coroutineScope.launch {
            val id = GlanceAppWidgetManager(context).getGlanceIds(TaskWidget::class.java).firstOrNull()
            val task = getTasksUseCase()

            id?.let {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { preferences ->
                    preferences.toMutablePreferences().apply {
                        this[currentTask] = task.title
                        this[currentTaskState] = task.isCompleted
                    }
                }
                glanceAppWidget.update(context, it)
            }
        }
    }

    companion object {
        val currentTask = stringPreferencesKey("Current Task")
        val currentTaskState = booleanPreferencesKey("Current Task State")
    }
}

class UpdateTaskCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val intent = Intent(context, TaskWidgetReceiver::class.java).apply {
            action = UPDATE_ACTION
        }
        context.sendBroadcast(intent)
    }

    companion object {
        const val UPDATE_ACTION = "update task state"
    }
}