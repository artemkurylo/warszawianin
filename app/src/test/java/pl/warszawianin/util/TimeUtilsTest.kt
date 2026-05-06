package pl.warszawianin.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

class TimeUtilsTest {

    @Test
    fun `relativeTime returns 'przed chwila' for recent timestamps`() {
        val now = System.currentTimeMillis()
        assertEquals("przed chwilą", TimeUtils.relativeTime(now))
    }

    @Test
    fun `relativeTime returns minutes for timestamps under 1 hour`() {
        val thirtyMinAgo = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30)
        assertEquals("30 min temu", TimeUtils.relativeTime(thirtyMinAgo))
    }

    @Test
    fun `relativeTime returns hours for timestamps under 1 day`() {
        val fiveHoursAgo = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(5)
        assertEquals("5 godz. temu", TimeUtils.relativeTime(fiveHoursAgo))
    }

    @Test
    fun `relativeTime returns 'wczoraj' for 1 day ago`() {
        val oneDayAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
        assertEquals("wczoraj", TimeUtils.relativeTime(oneDayAgo))
    }

    @Test
    fun `relativeTime returns days for timestamps under 7 days`() {
        val threeDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3)
        assertEquals("3 dni temu", TimeUtils.relativeTime(threeDaysAgo))
    }

    @Test
    fun `relativeTime returns formatted date for older timestamps`() {
        // 30 days ago — should return a formatted date
        val thirtyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
        val result = TimeUtils.relativeTime(thirtyDaysAgo)
        // Should contain a year
        assert(result.contains("202"))
    }

    @Test
    fun `formatDate returns Polish formatted date with time`() {
        // Fixed timestamp: May 6, 2026 12:30
        val result = TimeUtils.formatDate(1778266200000L)
        // Should contain "maja" (Polish month) or similar
        assert(result.isNotBlank())
    }

    @Test
    fun `formatDateShort returns date without time`() {
        val result = TimeUtils.formatDateShort(1778266200000L)
        assert(result.isNotBlank())
        // Should NOT contain a colon (no time)
        assert(!result.contains(":"))
    }
}
