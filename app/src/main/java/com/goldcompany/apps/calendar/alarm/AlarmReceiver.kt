package com.goldcompany.apps.calendar.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.goldcompany.apps.calendar.R
import com.goldcompany.apps.calendar.util.ALARM_BUNDLE_DESCRIPTION
import com.goldcompany.apps.calendar.util.ALARM_BUNDLE_TITLE
import com.goldcompany.apps.calendar.util.CHANNEL_ID

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra(ALARM_BUNDLE_TITLE) ?: return
        val description = intent.getStringExtra(ALARM_BUNDLE_DESCRIPTION)

        context?.let { c ->
            val notificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(c, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_calendar_24)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            notificationManager.notify(1, builder.build())
        }
    }
}