package com.example.alarmclockchatgpt

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            // Manual MediaPlayer initialization
            mediaPlayer = MediaPlayer()
            val afd = resources.openRawResourceFd(R.raw.alarm2)
            mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mediaPlayer?.prepare()
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()

            // Initialize and start vibration
            vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            val vibrationPattern = longArrayOf(0, 1000, 1000)
            vibrator?.vibrate(vibrationPattern, 0)



            Log.d("AlarmService", "Alarm started successfully.")
        } catch (e: Exception) {
            Log.e("AlarmService", "Error starting alarm: ${e.localizedMessage}")
            stopSelf()
        }
        showNotification()
        return START_STICKY
    }


    private fun showNotification() {
        val stopIntent = Intent(this, AlarmReceiver::class.java).apply {
            action = "STOP_ALARM"
        }
        val stopPendingIntent = android.app.PendingIntent.getBroadcast(
            this,
            0,
            stopIntent,
            android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "ALARM_CHANNEL")
            .setContentTitle("Alarm is ringing")
            .setContentText("Tap to stop the alarm")
            .setSmallIcon(R.drawable.ic_alarm)
            .addAction(R.drawable.ic_alarm, "Stop", stopPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            vibrator?.cancel()
            Log.d("AlarmService", "Alarm stopped successfully.")
        } catch (e: Exception) {
            Log.e("AlarmService", "Error stopping alarm: ${e.localizedMessage}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}