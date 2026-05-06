# Story 05 — Gemini AI Integration

## Goal

Send the photo + location to Gemini 2.5 Flash and receive structured category/title/description in Polish.

## Depends On

- Story 03 (Photo Capture — has photo URI)
- Story 04 (GPS Location — has coordinates)

## Acceptance Criteria

- [ ] `GeminiService` class that takes photo bytes + location → returns `AiDraft` (category, title, description)
- [ ] Uses `com.google.ai.client.generativeai` SDK with model `gemini-2.5-flash`
- [ ] API key stored in `local.properties` as `GEMINI_API_KEY` and exposed via BuildConfig
- [ ] Prompt matches the one in IDEA.md (Polish output, JSON response)
- [ ] Parses JSON response into structured data
- [ ] Handles errors gracefully (network, quota, malformed response)
- [ ] Updates the Report in Room with AI-generated fields

## Data Classes

```kotlin
data class AiDraft(
    val category: String,
    val title: String,
    val description: String
)
```

## Files to Create/Modify

```
app/src/main/java/pl/warszawianin/
├── data/
│   └── ai/
│       ├── GeminiService.kt         # CREATE
│       └── AiDraft.kt               # CREATE
├── di/
│   └── AiModule.kt                  # CREATE — provides GenerativeModel
app/build.gradle.kts                  # MODIFY — read API key from local.properties
```

## Implementation Notes

- Add to `app/build.gradle.kts`:
  ```kotlin
  android {
      defaultConfig {
          val geminiKey = project.findProperty("GEMINI_API_KEY") as? String ?: ""
          buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")
      }
  }
  ```
- In `local.properties` (gitignored): `GEMINI_API_KEY=your_key_here`
- Gemini SDK usage:
  ```kotlin
  val model = GenerativeModel(
      modelName = "gemini-2.5-flash",
      apiKey = BuildConfig.GEMINI_API_KEY
  )
  val response = model.generateContent(
      content {
          image(photoBitmap)
          text(prompt)
      }
  )
  ```
- Parse JSON from `response.text` — use simple regex or `JSONObject` (avoid adding Moshi/Gson just for this)
- Prompt template (from IDEA.md):
  ```
  You are an assistant helping Warsaw residents report neighbourhood issues
  to the city's 19115 service. Analyse the attached photo.

  Location context: {latitude}, {longitude} ({reverse_geocoded_address})

  Respond in Polish. Return ONLY valid JSON:
  {"kategoria": "...", "tytuł": "...", "opis": "..."}
  ```
- If address is not yet available, omit the address part from prompt
