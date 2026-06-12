package com.cmp.pushuptracker

import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.utils.calculateWeeklyCount
import com.cmp.pushuptracker.utils.getWeeklyReps
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class SystemUtilsTest {
    @Test
    fun weeklyRepsSupportsStorageAndLegacyDates() {
        val sessions = listOf(
            PushUpEntity(date = "2026-06-07", reps = 10, sets = 1, duration = 30),
            PushUpEntity(date = "08/06/2026", reps = 15, sets = 1, duration = 45),
            PushUpEntity(date = "2026-06-14", reps = 100, sets = 5, duration = 300)
        )

        val reps = getWeeklyReps(sessions, referenceDate = LocalDate.of(2026, 6, 12))

        assertEquals(listOf(10, 15, 0, 0, 0, 0, 0), reps)
        assertEquals(25, calculateWeeklyCount(sessions, referenceDate = LocalDate.of(2026, 6, 12)))
    }
}
