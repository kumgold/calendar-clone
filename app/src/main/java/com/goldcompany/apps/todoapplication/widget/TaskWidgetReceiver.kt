package com.goldcompany.apps.todoapplication.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
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

        if (intent.action == "UPDATE_ACTION") {
            Log.d("broadcast", "update action receive")
            observeTasks(context)
        }
    }

    private fun observeTasks(context: Context) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val id = GlanceAppWidgetManager(context).getGlanceIds(TaskWidget::class.java).firstOrNull()
                val task = getTasksUseCase()

                id?.let {
                    updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { preferences ->
                        preferences.toMutablePreferences().apply {
                            this[currentTaskId] = task.id
                            this[currentTaskTitle] = task.title
                            this[currentTaskState] = task.isCompleted
                        }
                    }
                    glanceAppWidget.update(context, it)
                }
            }
        }
    }

    companion object {
        const val UPDATE_ACTION = "UPDATE_TASK_STATE_ACTION"

        val currentTaskId = stringPreferencesKey("Current Task Id")
        val currentTaskTitle = stringPreferencesKey("Current Task Title")
        val currentTaskState = booleanPreferencesKey("Current Task State")
    }
}
