package pl.warszawianin.data.ai

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor(
    private val generativeModel: GenerativeModel
) {
    suspend fun analyzePhoto(
        photoFile: File,
        latitude: Double?,
        longitude: Double?,
        address: String?
    ): AiDraft {
        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            ?: throw IllegalStateException("Cannot decode photo")

        val locationContext = buildLocationContext(latitude, longitude, address)
        val prompt = buildPrompt(locationContext)

        val response = generativeModel.generateContent(
            content {
                image(bitmap)
                text(prompt)
            }
        )

        val text = response.text ?: throw IllegalStateException("Empty AI response")
        return parseResponse(text)
    }

    suspend fun refine(
        photoFile: File,
        currentCategory: String,
        currentTitle: String,
        currentDescription: String,
        userFeedback: String,
        latitude: Double?,
        longitude: Double?,
        address: String?
    ): AiDraft {
        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            ?: throw IllegalStateException("Cannot decode photo")

        val locationContext = buildLocationContext(latitude, longitude, address)
        val prompt = """
            You are an assistant helping Warsaw residents report neighbourhood issues 
            to the city's 19115 service. Analyse the attached photo.
            
            ${if (locationContext.isNotBlank()) "Location context: $locationContext" else ""}
            
            Previous analysis:
            Kategoria: $currentCategory
            Tytuł: $currentTitle
            Opis: $currentDescription
            
            User feedback: $userFeedback
            
            Please regenerate the report taking the feedback into account.
            Respond in Polish. Return ONLY valid JSON:
            {"kategoria": "drogi | oświetlenie | zieleń | czystość | infrastruktura | bezpieczeństwo | inne", "tytuł": "short descriptive title, max 60 chars", "opis": "2-4 sentence factual description of the visible problem, include location reference"}
        """.trimIndent()

        val response = generativeModel.generateContent(
            content {
                image(bitmap)
                text(prompt)
            }
        )

        val text = response.text ?: throw IllegalStateException("Empty AI response")
        return parseResponse(text)
    }

    private fun buildLocationContext(lat: Double?, lng: Double?, address: String?): String {
        if (lat == null || lng == null) return ""
        val coords = "$lat, $lng"
        return if (address != null) "$coords ($address)" else coords
    }

    private fun buildPrompt(locationContext: String): String = """
        You are an assistant helping Warsaw residents report neighbourhood issues 
        to the city's 19115 service. Analyse the attached photo.
        
        ${if (locationContext.isNotBlank()) "Location context: $locationContext" else ""}
        
        Respond in Polish. Return ONLY valid JSON:
        {"kategoria": "drogi | oświetlenie | zieleń | czystość | infrastruktura | bezpieczeństwo | inne", "tytuł": "short descriptive title, max 60 chars", "opis": "2-4 sentence factual description of the visible problem, include location reference"}
    """.trimIndent()

    private fun parseResponse(text: String): AiDraft {
        // Extract JSON from response (may have markdown fences)
        val jsonStr = text
            .replace("```json", "")
            .replace("```", "")
            .trim()

        val json = JSONObject(jsonStr)
        return AiDraft(
            category = json.optString("kategoria", "inne"),
            title = json.optString("tytuł", "Zgłoszenie"),
            description = json.optString("opis", "")
        )
    }
}
