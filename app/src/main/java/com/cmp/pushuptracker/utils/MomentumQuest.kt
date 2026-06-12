package com.cmp.pushuptracker.utils

import com.cmp.pushuptracker.database.entity.PushUpEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.math.ceil
import kotlin.math.roundToInt

data class MomentumQuestState(
    val todayTarget: Int,
    val todayReps: Int,
    val todayProgress: Float,
    val weeklyTargetDays: Int,
    val activeDaysThisWeek: Int,
    val restShieldsRemaining: Int,
    val currentStreak: Int,
    val levelName: String,
    val badgeLabel: String,
    val message: String,
    val weekDayStates: List<QuestDayState>
) {
    val repsRemainingToday: Int = (todayTarget - todayReps).coerceAtLeast(0)
    val isTodayComplete: Boolean = todayReps >= todayTarget
    val isQuestComplete: Boolean = activeDaysThisWeek >= weeklyTargetDays
}

data class QuestDayState(
    val date: LocalDate,
    val reps: Int,
    val isActive: Boolean,
    val isToday: Boolean,
    val isPast: Boolean
)

object MomentumQuest {
    private const val WEEKLY_TARGET_DAYS = 5
    private const val REST_SHIELDS_PER_WEEK = 2
    private const val DEFAULT_TARGET = 20

    fun build(
        sessions: List<PushUpEntity>,
        referenceDate: LocalDate = LocalDate.now()
    ): MomentumQuestState {
        val repsByDate = sessions
            .mapNotNull { session ->
                val date = TimeUtils.parseAppDate(session.date) ?: return@mapNotNull null
                date to session.reps
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { entry -> entry.value.sum() }

        val weekStart = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val weekDates = (0L until 7L).map { weekStart.plusDays(it) }
        val weekDayStates = weekDates.map { date ->
            val reps = repsByDate[date] ?: 0
            QuestDayState(
                date = date,
                reps = reps,
                isActive = reps > 0,
                isToday = date == referenceDate,
                isPast = date.isBefore(referenceDate)
            )
        }

        val todayReps = repsByDate[referenceDate] ?: 0
        val todayTarget = adaptiveTarget(repsByDate, referenceDate)
        val activeDaysThisWeek = weekDayStates.count { it.isActive }
        val firstActivityDate = repsByDate
            .filterValues { it > 0 }
            .keys
            .minOrNull()
        val challengeStartDate = when {
            firstActivityDate == null -> referenceDate
            firstActivityDate.isAfter(weekStart) -> firstActivityDate
            else -> weekStart
        }
        val missedPastDaysThisWeek = weekDayStates.count {
            it.isPast && !it.isActive && !it.date.isBefore(challengeStartDate)
        }
        val restShieldsRemaining = (REST_SHIELDS_PER_WEEK - missedPastDaysThisWeek).coerceAtLeast(0)
        val currentStreak = currentStreak(repsByDate, referenceDate)
        val todayProgress = (todayReps.toFloat() / todayTarget).coerceIn(0f, 1f)
        val levelName = when {
            activeDaysThisWeek >= WEEKLY_TARGET_DAYS -> "Quest complete"
            currentStreak >= 7 -> "Fire streak"
            currentStreak >= 3 -> "Momentum"
            else -> "Kickstart"
        }
        val badgeLabel = when {
            activeDaysThisWeek >= WEEKLY_TARGET_DAYS -> "Weekly badge earned"
            todayReps >= todayTarget -> "Checkpoint cleared"
            restShieldsRemaining > 0 && todayReps == 0 -> "Rest shield ready"
            missedPastDaysThisWeek > REST_SHIELDS_PER_WEEK -> "Comeback day"
            else -> "Next checkpoint"
        }
        val message = when {
            activeDaysThisWeek >= WEEKLY_TARGET_DAYS -> "You cleared this week's 5-day quest."
            todayReps >= todayTarget -> "Today's checkpoint is done. Extra reps build your best."
            restShieldsRemaining > 0 && todayReps == 0 -> "Hit your target or use a rest shield without breaking momentum."
            missedPastDaysThisWeek > REST_SHIELDS_PER_WEEK -> "A small set today restarts your momentum."
            else -> "${todayTarget - todayReps} reps to clear today's checkpoint."
        }

        return MomentumQuestState(
            todayTarget = todayTarget,
            todayReps = todayReps,
            todayProgress = todayProgress,
            weeklyTargetDays = WEEKLY_TARGET_DAYS,
            activeDaysThisWeek = activeDaysThisWeek,
            restShieldsRemaining = restShieldsRemaining,
            currentStreak = currentStreak,
            levelName = levelName,
            badgeLabel = badgeLabel,
            message = message,
            weekDayStates = weekDayStates
        )
    }

    private fun adaptiveTarget(
        repsByDate: Map<LocalDate, Int>,
        referenceDate: LocalDate
    ): Int {
        val recentActiveReps = (1L..14L)
            .map { referenceDate.minusDays(it) }
            .mapNotNull { repsByDate[it] }
            .filter { it > 0 }

        if (recentActiveReps.isEmpty()) return DEFAULT_TARGET

        val average = recentActiveReps.average()
        val best = recentActiveReps.maxOrNull() ?: DEFAULT_TARGET
        val rawTarget = maxOf(average * 1.10, best * 0.55, DEFAULT_TARGET.toDouble())
        return roundUpToNearestFive(rawTarget.roundToInt()).coerceIn(10, 150)
    }

    private fun roundUpToNearestFive(value: Int): Int {
        return (ceil(value / 5.0) * 5).toInt()
    }

    private fun currentStreak(
        repsByDate: Map<LocalDate, Int>,
        referenceDate: LocalDate
    ): Int {
        var streak = 0
        var date = referenceDate

        if ((repsByDate[date] ?: 0) <= 0) {
            date = date.minusDays(1)
        }

        while ((repsByDate[date] ?: 0) > 0) {
            streak++
            date = date.minusDays(1)
        }
        return streak
    }
}
