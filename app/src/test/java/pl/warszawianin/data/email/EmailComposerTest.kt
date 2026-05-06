package pl.warszawianin.data.email

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import pl.warszawianin.data.model.Report
import pl.warszawianin.data.model.ReportStatus

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class EmailComposerTest {

    private val context = RuntimeEnvironment.getApplication()

    private fun createReport() = Report(
        id = 1,
        photoUri = "file:///data/user/0/pl.warszawianin/files/photos/test.jpg",
        latitude = 52.2297,
        longitude = 21.0122,
        address = "ul. Marszałkowska 12",
        category = "Drogi",
        title = "Dziura w chodniku",
        description = "Na chodniku przy ul. Marszałkowskiej 12 znajduje się głęboka dziura.",
        status = ReportStatus.DRAFT,
        createdAt = System.currentTimeMillis(),
        sentAt = null
    )

    @Test
    fun `createEmailIntent sets correct recipient`() {
        val intent = EmailComposer.createEmailIntent(context, createReport())
        val emails = intent.getStringArrayExtra(Intent.EXTRA_EMAIL)
        assertEquals("artemkurylo17@gmail.com", emails?.first())
    }

    @Test
    fun `createEmailIntent sets subject with title`() {
        val intent = EmailComposer.createEmailIntent(context, createReport())
        val subject = intent.getStringExtra(Intent.EXTRA_SUBJECT)
        assertEquals("Zgłoszenie: Dziura w chodniku", subject)
    }

    @Test
    fun `createEmailIntent body contains category`() {
        val intent = EmailComposer.createEmailIntent(context, createReport())
        val body = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        assertTrue(body.contains("Kategoria: Drogi"))
    }

    @Test
    fun `createEmailIntent body contains address`() {
        val intent = EmailComposer.createEmailIntent(context, createReport())
        val body = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        assertTrue(body.contains("ul. Marszałkowska 12"))
    }

    @Test
    fun `createEmailIntent body contains description`() {
        val intent = EmailComposer.createEmailIntent(context, createReport())
        val body = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        assertTrue(body.contains("głęboka dziura"))
    }

    @Test
    fun `createEmailIntent body contains GPS coordinates`() {
        val intent = EmailComposer.createEmailIntent(context, createReport())
        val body = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        assertTrue(body.contains("52.2297"))
        assertTrue(body.contains("21.0122"))
    }

    @Test
    fun `createEmailIntent body contains sign-off`() {
        val intent = EmailComposer.createEmailIntent(context, createReport())
        val body = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        assertTrue(body.contains("Wysłano z aplikacji Warszawianin"))
    }

    @Test
    fun `createEmailIntent has correct MIME type`() {
        val intent = EmailComposer.createEmailIntent(context, createReport())
        assertEquals("message/rfc822", intent.type)
    }

    @Test
    fun `createEmailIntent handles report without address`() {
        val report = createReport().copy(address = null)
        val intent = EmailComposer.createEmailIntent(context, report)
        val body = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        assertTrue(body.contains("GPS: 52.2297, 21.0122"))
    }

    @Test
    fun `createEmailIntent handles report without location`() {
        val report = createReport().copy(latitude = null, longitude = null, address = null)
        val intent = EmailComposer.createEmailIntent(context, report)
        val body = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        assertTrue(body.contains("Nieznana"))
    }
}
