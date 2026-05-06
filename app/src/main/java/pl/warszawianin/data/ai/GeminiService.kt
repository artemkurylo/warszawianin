package pl.warszawianin.data.ai

import kotlinx.coroutines.delay
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Demo AI service — always returns the hardcoded demo response.
 * Take a real photo, get a pre-written report about a broken light at Widok 16/1.
 */
@Singleton
class GeminiService @Inject constructor() {

    suspend fun analyzePhoto(
        photoFile: File,
        latitude: Double?,
        longitude: Double?,
        address: String?
    ): AiDraft {
        // Simulate AI thinking (feels realistic in demo)
        delay(2000L)

        return AiDraft(
            category = "Oświetlenie",
            title = "Niesprawna latarnia przy klatce",
            description = "Latarnia uliczna przy wejściu do klatki schodowej pod adresem " +
                    "ul. Widok 16/1 nie świeci się od kilku dni. " +
                    "Brak oświetlenia w godzinach wieczornych stwarza zagrożenie " +
                    "dla mieszkańców wchodzących do budynku. " +
                    "Proszę o pilną naprawę lub wymianę źródła światła."
        )
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
        // Simulate AI refinement
        delay(1500L)

        return AiDraft(
            category = currentCategory,
            title = currentTitle,
            description = currentDescription + "\n\nUwaga dodatkowa: $userFeedback"
        )
    }
}
