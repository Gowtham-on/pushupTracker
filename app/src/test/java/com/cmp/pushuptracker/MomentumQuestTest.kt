package com.cmp.pushuptracker

import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.utils.MomentumQuest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class MomentumQuestTest {
    @Test
    fun newUserGetsBeginnerTargetAndFullRestShields() {
        val quest = MomentumQuest.build(
            sessions = emptyList(),
            referenceDate = LocalDate.of(2026, 6, 12)
        )

        assertEquals(20, quest.todayTarget)
        assertEquals(0, quest.todayReps)
        assertEquals(5, quest.weeklyTargetDays)
        assertEquals(0, quest.activeDaysThisWeek)
        assertEquals(2, quest.restShieldsRemaining)
        assertEquals("Kickstart", quest.levelName)
        assertFalse(quest.isTodayComplete)
    }

    @Test
    fun adaptiveTargetUsesRecentActivityAndRoundsUp() {
        val quest = MomentumQuest.build(
            sessions = listOf(
                PushUpEntity(date = "2026-06-11", reps = 42, sets = 3, duration = 90),
                PushUpEntity(date = "2026-06-10", reps = 35, sets = 2, duration = 80),
                PushUpEntity(date = "2026-06-09", reps = 20, sets = 2, duration = 60)
            ),
            referenceDate = LocalDate.of(2026, 6, 12)
        )

        assertEquals(40, quest.todayTarget)
        assertEquals(3, quest.activeDaysThisWeek)
        assertEquals(3, quest.currentStreak)
        assertEquals("Momentum", quest.levelName)
    }

    @Test
    fun completedWeekEarnsQuestCompleteState() {
        val quest = MomentumQuest.build(
            sessions = listOf(
                PushUpEntity(date = "2026-06-07", reps = 20, sets = 2, duration = 60),
                PushUpEntity(date = "2026-06-08", reps = 20, sets = 2, duration = 60),
                PushUpEntity(date = "2026-06-09", reps = 20, sets = 2, duration = 60),
                PushUpEntity(date = "2026-06-10", reps = 20, sets = 2, duration = 60),
                PushUpEntity(date = "2026-06-11", reps = 20, sets = 2, duration = 60)
            ),
            referenceDate = LocalDate.of(2026, 6, 12)
        )

        assertTrue(quest.isQuestComplete)
        assertEquals("Quest complete", quest.levelName)
        assertEquals("Weekly badge earned", quest.badgeLabel)
    }

    @Test
    fun missedPastDaysConsumeRestShields() {
        val quest = MomentumQuest.build(
            sessions = listOf(
                PushUpEntity(date = "2026-06-07", reps = 20, sets = 2, duration = 60)
            ),
            referenceDate = LocalDate.of(2026, 6, 11)
        )

        assertEquals(0, quest.restShieldsRemaining)
        assertEquals("Comeback day", quest.badgeLabel)
    }
}
