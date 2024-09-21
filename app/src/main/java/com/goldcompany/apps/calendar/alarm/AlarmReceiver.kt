package com.goldcompany.apps.calendar.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.goldcompany.apps.calendar.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("MESSAGE") ?: return
        val channelId = "alarm_id"

        context?.let { c ->
            val notificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(c, channelId)
                .setSmallIcon(R.drawable.ic_calendar_24)
                .setContentTitle(message)
                .setContentText("test")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            notificationManager.notify(1, builder.build())
        }
    }
}