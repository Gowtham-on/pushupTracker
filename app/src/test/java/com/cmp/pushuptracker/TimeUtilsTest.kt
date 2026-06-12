package com.cmp.pushuptracker

import com.cmp.pushuptracker.utils.TimeUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeUtilsTest {
    @Test
    fun convertsLegacyDateToStorageDate() {
        assertEquals("2026-06-12", TimeUtils.toStorageDate("12/06/2026"))
    }

    @Test
    fun keepsStorageDateAsStorageDate() {
        assertEquals("2026-06-12", TimeUtils.toStorageDate("2026-06-12"))
    }

    @Test
    fun formatsStorageDateForDisplay() {
        assertEquals("12/06/2026", TimeUtils.formatDateForDisplay("2026-06-12"))
    }
}
