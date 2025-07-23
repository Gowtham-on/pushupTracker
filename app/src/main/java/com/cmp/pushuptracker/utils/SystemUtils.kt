package com.cmp.pushuptracker.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.cmp.pushuptracker.database.entity.PushUpEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

fun vibrate(context: Context, duration: Long) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vm.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    val vibrationEffect =
        VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)

    vibrator.vibrate(vibrationEffect)
}


fun estimatePushupCalories(
    reps: Int,
    durationSec: Int,
    weightKg: Double,
    met: Double = 3.8
): String {
    // MET-based
    val minutes = durationSec / 60.0
    val caloriesPerMin = (met * weightKg * 3.5) / 200.0
    val caloriesMet = caloriesPerMin * minutes

    // Per-rep
    val baseKcalPerRep = 0.32
    val caloriesPerRep = baseKcalPerRep * (weightKg / 70.0)
    val caloriesRep = caloriesPerRep * reps

    // You could average them or choose one:
    var result = ((caloriesMet + caloriesRep) / 2.0).toString()

    return String.format(Locale.getDefault(), "%.2f", result.toDouble())
}

fun getWeeklyReps(
    sessions: List<PushUpEntity>,
    referenceDate: LocalDate = LocalDate.now()
): List<Int> {
    // 1) find Monday of this week
    val weekStart = referenceDate
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    // 2) formatter matching your stored date format
    val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // 3) for each day Monday→Sunday, sum reps
    val list = (0L until 7L).map { offset ->
        val day = weekStart.plusDays(offset)
        val dayKey = day.format(fmt)
        sessions
            .filter { it.date == dayKey }
            .sumOf { it.reps }
    }

    return list
}

fun calculateWeeklyCount(sessions: List<PushUpEntity>): Int {
    var count = 0
    getWeeklyReps(sessions).map {
        count += it
    }
    return count
}