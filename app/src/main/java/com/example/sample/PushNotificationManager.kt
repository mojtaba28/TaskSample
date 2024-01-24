package com.example.sample

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat


class PushNotificationManager(private val context: Context) {

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a Notification Channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Const.CHANNEL_ID,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

     fun showNotification(sender: String, messageBody: String) {

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        // Build the notification
        val notifBuilder = NotificationCompat.Builder(context, Const.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("New SMS from $sender")
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Show the notification
        notificationManager.notify(Const.NOTIFICATION_ID, notifBuilder.build())

        Log.d("SmsReceiver", "Notification displayed: $sender - $messageBody")
    }

    companion object {
        @Volatile
        private var instance: PushNotificationManager? = null

        fun getInstance(context: Context): PushNotificationManager {
            return instance ?: synchronized(this) {
                instance ?: PushNotificationManager(context).also { instance = it }
            }
        }
    }
}