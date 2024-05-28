package com.android.partagix.utils

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.Date

class utilsTest {

    @Test
    fun stripTimeTest() {
        val calendar = Calendar.getInstance()
        calendar.set(2021, Calendar.MAY, 5, 13, 5, 10)
        val strippedDate = stripTime(calendar.time)
        calendar.set(2021, Calendar.MAY, 5, 0, 0, 0)
        assertEquals(calendar.time.date, strippedDate.date)
        assertEquals(calendar.time.hours, strippedDate.hours)
        assertEquals(calendar.time.minutes, strippedDate.minutes)
        assertEquals(calendar.time.seconds, strippedDate.seconds)
    }
}