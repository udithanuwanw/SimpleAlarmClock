package com.example.alarmclockchatgpt

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            "STOP_ALARM" -> {
                context.stopService(Intent(context, AlarmService::class.java))
                NotificationManagerCompat.from(context).cancel(1)
                Log.d("AlarmReceiver", "Alarm stopped.")
            }
            else -> {
                // Start alarm service
                val alarmIntent = Intent(context, AlarmService::class.java)
                context.startForegroundService(alarmIntent)

                // Create a notification with "Stop Alarm" action
                val stopIntent = Intent(context, AlarmReceiver::class.java).apply {
                    action = "STOP_ALARM"
                }
                val stopPendingIntent = PendingIntent.getBroadcast(
                    context,
                    1,
                    stopIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(context, "ALARM_CHANNEL")
                    .setSmallIcon(R.drawable.ic_alarm)
                    .setContentTitle("Alarm")
                    .setContentText("Your alarm is ringing!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_alarm, "Stop Alarm", stopPendingIntent)
                    .build()

                NotificationManagerCompat.from(context).notify(1, notification)
                Log.d("AlarmReceiver", "Alarm started.")
            }
        }
    }
}
