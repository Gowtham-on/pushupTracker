package com.cmp.pushuptracker.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cmp.pushuptracker.database.repository.PushupRepository
import com.cmp.pushuptracker.utils.TimeUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: PushupRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val reminderType = inputData.getString(DailyReminderScheduler.KEY_REMINDER_TYPE)
            ?: return Result.success()

        val today = TimeUtils.todayStorageDate()
        val session = repository.getSessionByDate(today)?.firstOrNull()
        val hasCompletedToday = (session?.reps ?: 0) > 0

        val shouldNotify = when (reminderType) {
            DailyReminderScheduler.TYPE_MORNING -> !hasCompletedToday
            DailyReminderScheduler.TYPE_EVENING -> !hasCompletedToday
            else -> false
        }

        if (shouldNotify) {
            NotificationHelper.showReminder(applicationContext, reminderType)
        }

        return Result.success()
    }
}
