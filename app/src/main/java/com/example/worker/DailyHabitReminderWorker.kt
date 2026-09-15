package com.example.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class DailyHabitReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        sendReminderNotification()
        return Result.success()
    }

    private fun sendReminderNotification() {
        val channelId = "aramesh_daily_reminders"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "یادآورهای روزانه آرامش (Aramesh Daily)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "یادآوری سپاسگزاری، تمرین تنفس و آرامش روزانه"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("لحظه‌ای آرامش و سپاسگزاری 🌱")
            .setContentText("امروز چه زیبایی کوچکی دیدی؟ برای چند لحظه نفس عمیق بکش و قدردانی کن.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }

    companion object {
        private const val WORK_NAME = "aramesh_daily_habit_reminder"

        fun scheduleDailyReminder(context: Context, enabled: Boolean) {
            val workManager = WorkManager.getInstance(context)
            if (enabled) {
                val dailyRequest = PeriodicWorkRequestBuilder<DailyHabitReminderWorker>(
                    24, TimeUnit.HOURS,
                    15, TimeUnit.MINUTES
                ).build()
                workManager.enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    dailyRequest
                )
            } else {
                workManager.cancelUniqueWork(WORK_NAME)
            }
        }
    }
}
