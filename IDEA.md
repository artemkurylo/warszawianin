# IDEA.md — Warszawianin (Hackathon MVP)

## Vision

**Warszawianin** — snap a photo of a neighbourhood problem, let AI write the report, send it to Warsaw 19115.

---

## MVP User Flow

```
┌────────────────────┐       ┌─────────────────────┐       ┌─────────────────────┐
│   Ticket List      │──(+)──│  Photo Capture      │──────▶│  AI Draft + Submit  │
│   (your reports)   │       │  Camera or Gallery   │       │  Edit / Send        │
└────────────────────┘       └─────────────────────┘       └─────────────────────┘
```

**Screen 1 — Ticket List (Home)**
- Shows previously created reports (stored locally in Room)
- Each ticket: thumbnail, title, date, status (draft / sent)
- FAB button (+) → opens photo capture

**Screen 2 — Photo Capture**
- Two options: take a photo (camera) or pick from gallery
- Also captures GPS location silently in background
- Once photo is selected → auto-navigates to Screen 3

**Screen 3 — AI Draft & Submit**
- Shows the photo at the top
- Shows loading spinner while Gemini analyses
- Displays generated: title, category, description (in Polish)
- User can edit any field
- Two buttons: "Wyślij" (Submit via email) / "Popraw" (Refine — re-prompt AI with user feedback)
- On submit → opens email intent pre-filled to kontakt@um.warszawa.pl with photo attached

---

## Tech Stack (minimal)

| Layer | Choice | Why |
|-------|--------|-----|
| Language | Kotlin | Standard |
| UI | Jetpack Compose + Material 3 | Fast to build |
| DI | Hilt | Simple setup |
| Camera | CameraX + Photo Picker | Reliable |
| Location | FusedLocationProvider | Accurate GPS |
| LLM | Google Gemini 2.5 Flash | Free tier, vision+thinking, fast, Android SDK |
| Storage | Room | Local ticket list |
| Submission | ACTION_SEND email intent | Zero backend needed |
| Architecture | Single-module MVVM | Hackathon simplicity |

---

## Screens Breakdown

### 1. TicketListScreen
```
┌─────────────────────────────┐
│  Warszawianin        [gear] │  ← TopBar
├─────────────────────────────┤
│  ┌───┬────────────────────┐ │
│  │ 📷│ Dziura w chodniku   │ │  ← ticket card
│  │   │ ul. Marszałkowska  │ │
│  │   │ 2 godz. temu • Wysłane│
│  └───┴────────────────────┘ │
│  ┌───┬────────────────────┐ │
│  │ 📷│ Zepsuta latarnia    │ │
│  │   │ ul. Puławska       │ │
│  │   │ wczoraj • Szkic    │ │
│  └───┴────────────────────┘ │
│                             │
│                     [ + ]   │  ← FAB
└─────────────────────────────┘
```

### 2. PhotoCaptureScreen
```
┌─────────────────────────────┐
│  ← Nowe zgłoszenie          │
├─────────────────────────────┤
│                             │
│     ┌─────────────────┐    │
│     │                 │    │
│     │   Camera View   │    │
│     │                 │    │
│     └─────────────────┘    │
│                             │
│  [ 📷 Zrób zdjęcie ]       │
│  [ 🖼️ Wybierz z galerii ]  │
│                             │
└─────────────────────────────┘
```

### 3. ReportDraftScreen
```
┌─────────────────────────────┐
│  ← Zgłoszenie               │
├─────────────────────────────┤
│  ┌─────────────────────┐   │
│  │   [photo preview]   │   │
│  └─────────────────────┘   │
│                             │
│  📍 ul. Marszałkowska 12   │
│                             │
│  Kategoria: [Drogi ▼]      │
│                             │
│  Tytuł:                     │
│  ┌─────────────────────┐   │
│  │ Dziura w chodniku   │   │
│  └─────────────────────┘   │
│                             │
│  Opis:                      │
│  ┌─────────────────────┐   │
│  │ Na chodniku przy ul. │   │
│  │ Marszałkowskiej 12   │   │
│  │ znajduje się głęboka │   │
│  │ dziura o średnicy... │   │
│  └─────────────────────┘   │
│                             │
│  [ 🔄 Popraw ]  [ ✉️ Wyślij ]│
└─────────────────────────────┘
```

---

## Data Model

```kotlin
// Single entity — that's all we need
@Entity(tableName = "reports")
data class Report(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val photoUri: String,          // local file URI
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,          // reverse-geocoded
    val category: String,          // AI-suggested or user-picked
    val title: String,             // AI-generated, editable
    val description: String,       // AI-generated, editable
    val status: ReportStatus,      // DRAFT, SENT
    val createdAt: Long,           // epoch millis
    val sentAt: Long?              // when submitted
)

enum class ReportStatus { DRAFT, SENT }
```

---

## LLM Model Choice

Google AI Studio (ai.google.dev) free tier — **no billing required**, just an API key.

| Model | Speed | Quality | Free Tier | Our Pick |
|-------|-------|---------|-----------|----------|
| Gemini 2.5 Flash | ⚡ Fast | Very good | Generous (high RPM/RPD) | ✅ **Primary** |
| Gemini 2.5 Pro | 🐢 Slower | Excellent | Available but lower RPM | Fallback if Flash isn't good enough |
| Gemini 3.1 Pro Preview | 🐢 Slower | Best | Preview (may be unstable) | Future upgrade |
| Gemini 2.5 Flash-Lite | ⚡⚡ Fastest | Good | Very generous | If we need speed over accuracy |

**Decision: `gemini-2.5-flash`**
- Has "thinking" capability (better reasoning about what's in the photo)
- Multimodal (image + text input natively)
- Fast enough for good UX (< 3s response)
- Free tier is very generous for a hackathon/beta
- Available via `com.google.ai.client.generativeai` Android SDK

Get API key at: https://aistudio.google.com/apikey

## Gemini Prompt

```
You are an assistant helping Warsaw residents report neighbourhood issues 
to the city's 19115 service. Analyse the attached photo.

Location context: {latitude}, {longitude} ({reverse_geocoded_address})

Respond in Polish. Return ONLY valid JSON:
{
  "kategoria": "drogi | oświetlenie | zieleń | czystość | infrastruktura | bezpieczeństwo | inne",
  "tytuł": "short descriptive title, max 60 chars",
  "opis": "2-4 sentence factual description of the visible problem, include location reference"
}
```

---

## Email Template (on Submit)

```
To: kontakt@um.warszawa.pl
Subject: Zgłoszenie: {title}
Attachment: photo.jpg

Szanowni Państwo,

Zgłaszam następujący problem:

Kategoria: {category}
Lokalizacja: {address} (GPS: {lat}, {lng})

{description}

Zdjęcie w załączniku.

Z poważaniem,
{user_name}

---
Wysłano z aplikacji Warszawianin
```

---

## Task List (build order)

- [ ] **T1** — Project scaffold (Gradle, Hilt, Compose, dependencies)
- [ ] **T2** — Room database + Report entity + DAO
- [ ] **T3** — TicketListScreen + ViewModel (reads from Room)
- [ ] **T4** — PhotoCaptureScreen (CameraX + gallery picker)
- [ ] **T5** — GPS location capture on photo taken
- [ ] **T6** — Gemini integration (send photo + location → get JSON)
- [ ] **T7** — ReportDraftScreen (display AI result, allow edits)
- [ ] **T8** — "Popraw" flow (re-send to Gemini with user corrections)
- [ ] **T9** — "Wyślij" flow (compose email intent, save as SENT)
- [ ] **T10** — Permissions handling (camera, location, storage)
- [ ] **T11** — Basic error states & loading UI
- [ ] **T12** — Reverse geocoding (GPS → address string)

---

## What We Skip (hackathon scope)

- ❌ Backend / server
- ❌ User accounts / auth
- ❌ Push notifications
- ❌ Report status tracking from city
- ❌ Offline queue (just show error)
- ❌ Multi-photo
- ❌ Dark mode polish
- ❌ Onboarding
- ❌ Tests (sorry)
- ❌ ProGuard / release build

---

## To Validate Before Building

1. **Email to 19115** — does `kontakt@um.warszawa.pl` process reports sent this way?
2. **Gemini quality** — take 5 photos of real Warsaw issues, run through the prompt, check output
3. **Gemini API key** — get one from Google AI Studio (ai.google.dev)
