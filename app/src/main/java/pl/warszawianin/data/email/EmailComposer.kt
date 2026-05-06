package pl.warszawianin.data.email

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import pl.warszawianin.data.model.Report
import java.io.File

object EmailComposer {

    fun createEmailIntent(context: Context, report: Report): Intent {
        val subject = "Zgłoszenie: ${report.title}"
        val body = buildBody(report)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("artemkurylo17@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Attach photo
        val photoUri = getPhotoContentUri(context, report.photoUri)
        if (photoUri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, photoUri)
        }

        return intent
    }

    private fun buildBody(report: Report): String {
        val location = if (report.address != null) {
            "${report.address} (GPS: ${report.latitude}, ${report.longitude})"
        } else if (report.latitude != null && report.longitude != null) {
            "GPS: ${report.latitude}, ${report.longitude}"
        } else {
            "Nieznana"
        }

        return """
            |Szanowni Państwo,
            |
            |Zgłaszam następujący problem:
            |
            |Kategoria: ${report.category}
            |Lokalizacja: $location
            |
            |${report.description}
            |
            |Zdjęcie w załączniku.
            |
            |Z poważaniem,
            |Użytkownik aplikacji Warszawianin
            |
            |---
            |Wysłano z aplikacji Warszawianin
        """.trimMargin()
    }

    private fun getPhotoContentUri(context: Context, photoUri: String): Uri? {
        return try {
            val uri = Uri.parse(photoUri)
            val file = File(uri.path ?: return null)
            if (file.exists()) {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
