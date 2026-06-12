package com.cmp.pushuptracker

import com.cmp.pushuptracker.database.entity.PushUpEntity
import com.cmp.pushuptracker.ui.screen.home.model.PushupQuickAdd
import com.cmp.pushuptracker.utils.PushupUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class PushupUtilsTest {
    @Test
    fun mergeCreatesStorageDatedSession() {
        val result = PushupUtils.buildPushupMerge(
            selectedDayData = null,
            selectedDate = "12/06/2026",
            addPushupData = PushupQuickAdd(reps = "20", sets = "2", min = "1", secs = "30")
        )

        assertEquals("2026-06-12", result.session.date)
        assertEquals(20, result.session.reps)
        assertEquals(2, result.session.sets)
        assertEquals(90, result.session.duration)
        assertEquals(20, result.addedReps)
        assertEquals(90, result.addedDuration)
    }

    @Test
    fun mergeAddsOnlyNewValuesToExistingSession() {
        val result = PushupUtils.buildPushupMerge(
            selectedDayData = PushUpEntity(date = "2026-06-12", reps = 30, sets = 3, duration = 120),
            selectedDate = "2026-06-12",
            addPushupData = PushupQuickAdd(reps = "10", sets = "1", min = "0", secs = "45")
        )

        assertEquals(40, result.session.reps)
        assertEquals(4, result.session.sets)
        assertEquals(165, result.session.duration)
        assertEquals(10, result.addedReps)
        assertEquals(45, result.addedDuration)
    }
}
