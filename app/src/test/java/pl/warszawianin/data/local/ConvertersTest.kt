package pl.warszawianin.data.local

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.warszawianin.data.model.ReportStatus

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `fromReportStatus converts DRAFT to string`() {
        assertEquals("DRAFT", converters.fromReportStatus(ReportStatus.DRAFT))
    }

    @Test
    fun `fromReportStatus converts SENT to string`() {
        assertEquals("SENT", converters.fromReportStatus(ReportStatus.SENT))
    }

    @Test
    fun `toReportStatus converts string to DRAFT`() {
        assertEquals(ReportStatus.DRAFT, converters.toReportStatus("DRAFT"))
    }

    @Test
    fun `toReportStatus converts string to SENT`() {
        assertEquals(ReportStatus.SENT, converters.toReportStatus("SENT"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `toReportStatus throws for invalid string`() {
        converters.toReportStatus("INVALID")
    }
}
