package pl.warszawianin.data.mock

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MockNeighbourDataTest {

    @Test
    fun `mock data contains expected number of reports`() {
        assertEquals(6, MockNeighbourData.reports.size)
    }

    @Test
    fun `all reports have non-blank titles`() {
        MockNeighbourData.reports.forEach { report ->
            assertTrue("Report ${report.id} has blank title", report.title.isNotBlank())
        }
    }

    @Test
    fun `all reports have unique IDs`() {
        val ids = MockNeighbourData.reports.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `all reports have non-blank categories`() {
        MockNeighbourData.reports.forEach { report ->
            assertTrue("Report ${report.id} has blank category", report.category.isNotBlank())
        }
    }

    @Test
    fun `all reports have non-blank distance`() {
        MockNeighbourData.reports.forEach { report ->
            assertTrue("Report ${report.id} has blank distance", report.distance.isNotBlank())
        }
    }

    @Test
    fun `supporters count is non-negative`() {
        MockNeighbourData.reports.forEach { report ->
            assertTrue("Report ${report.id} has negative supporters", report.supporters >= 0)
        }
    }
}
