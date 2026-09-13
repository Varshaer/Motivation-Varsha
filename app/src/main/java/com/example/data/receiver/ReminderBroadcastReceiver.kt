package com.example.data.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import java.util.Calendar

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(EXTRA_REMINDER_TYPE) ?: TYPE_MORNING
        NotificationHelper.showNotification(context, type)
    }

    companion object {
        const val EXTRA_REMINDER_TYPE = "extra_reminder_type"
        const val TYPE_MORNING = "morning"
        const val TYPE_HABIT = "habit"
        const val TYPE_EVENING = "evening"
        const val TYPE_TEST = "test"
    }
}

object NotificationHelper {
    const val CHANNEL_ID = "motivator_channel"
    private const val CHANNEL_NAME = "Motivator Reminders"
    private const val CHANNEL_DESC = "Daily motivation, habit reminders, and evening check-ins"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context, type: String) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val (title, content, notificationId) = when (type) {
            ReminderBroadcastReceiver.TYPE_MORNING -> Triple(
                "Good Morning, Champion! ⚡",
                "Your daily discipline determines your destiny. Start today's goals strong!",
                1001
            )
            ReminderBroadcastReceiver.TYPE_HABIT -> Triple(
                "Habit Check-In 🎯",
                "Keep your streak alive! Check off your midday habits and earn momentum points.",
                1002
            )
            ReminderBroadcastReceiver.TYPE_EVENING -> Triple(
                "Evening Victory Review 🏆",
                "Review today's wins and set up tomorrow for greatness. You're building an unstoppable life.",
                1003
            )
            else -> Triple(
                "Motivator Alert Active! 🔥",
                "Notifications are enabled and working properly. Stay focused and disciplined!",
                1000
            )
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, builder.build())
        } catch (_: Exception) {
            // Ignored gracefully if notification permissions are restricted
        }
    }

    fun scheduleReminder(context: Context, type: String, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra(ReminderBroadcastReceiver.EXTRA_REMINDER_TYPE, type)
        }
        val requestCode = when (type) {
            ReminderBroadcastReceiver.TYPE_MORNING -> 2001
            ReminderBroadcastReceiver.TYPE_HABIT -> 2002
            ReminderBroadcastReceiver.TYPE_EVENING -> 2003
            else -> 2000
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (_: Exception) {
            // Safe fallback if alarm restriction applies
        }
    }
}
