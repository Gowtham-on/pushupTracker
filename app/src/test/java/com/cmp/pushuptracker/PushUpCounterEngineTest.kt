package com.cmp.pushuptracker

import com.cmp.pushuptracker.camera.PushUpCounterEngine
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.CurrentPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PushUpCounterEngineTest {
    @Test
    fun countsRepOnlyAfterDownThenUp() {
        val engine = PushUpCounterEngine(minRepIntervalMs = 500L)

        val down = engine.update(80f, nowMs = 1_000L)
        assertEquals(CurrentPhase.DOWN, down.phase)
        assertFalse(down.counted)
        assertEquals(0, down.count)

        val up = engine.update(160f, nowMs = 1_600L)
        assertEquals(CurrentPhase.UP, up.phase)
        assertTrue(up.counted)
        assertEquals(1, up.count)
    }

    @Test
    fun debouncePreventsDuplicateRep() {
        val engine = PushUpCounterEngine(minRepIntervalMs = 500L)

        engine.update(80f, nowMs = 1_000L)
        engine.update(160f, nowMs = 1_600L)
        engine.update(80f, nowMs = 1_700L)
        val duplicate = engine.update(160f, nowMs = 1_800L)

        assertEquals(CurrentPhase.UP, duplicate.phase)
        assertFalse(duplicate.counted)
        assertEquals(1, duplicate.count)
    }
}
