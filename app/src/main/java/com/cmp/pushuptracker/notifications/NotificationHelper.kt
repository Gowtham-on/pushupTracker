package com.cmp.pushuptracker.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.cmp.pushuptracker.MainActivity
import com.cmp.pushuptracker.R

object NotificationHelper {
    private const val CHANNEL_ID = "pushup_daily_reminders"
    private const val CHANNEL_NAME = "Push-up reminders"
    private const val CHANNEL_DESCRIPTION = "Daily nudges to keep your push-up streak alive"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESCRIPTION
            enableVibration(true)
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    fun showReminder(context: Context, reminderType: String) {
        createChannel(context)
        val notificationId = when (reminderType) {
            DailyReminderScheduler.TYPE_MORNING -> 8
            DailyReminderScheduler.TYPE_EVENING -> 21
            else -> return
        }

        val title = context.getString(R.string.reminder_title)
        val reminderMessages = context.resources.getStringArray(R.array.reminder_messages)
        val message = reminderMessages.random()

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(activityIntent)
            .getPendingIntent(
                notificationId,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            ?: PendingIntent.getActivity(
                context,
                notificationId,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
