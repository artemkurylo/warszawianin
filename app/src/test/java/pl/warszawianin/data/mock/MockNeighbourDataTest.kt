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

    @Test
    fun `all reports have demo photo asset assigned`() {
        MockNeighbourData.reports.forEach { report ->
            assertTrue("Report ${report.id} has blank photo asset", report.demoPhotoAsset.isNotBlank())
            assertTrue("Report ${report.id} photo should end in .jpg", report.demoPhotoAsset.endsWith(".jpg"))
        }
    }

    @Test
    fun `PRIMARY_DEMO_PHOTO is a valid filename`() {
        assertTrue(MockNeighbourData.PRIMARY_DEMO_PHOTO.endsWith(".jpg"))
    }
}
