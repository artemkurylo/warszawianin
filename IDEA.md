# IDEA.md — Warszawianin (Hackathon MVP)

## Vision

**Warszawianin** — snap a photo of a neighbourhood problem, let AI write the report, send it to Warsaw 19115.

---

## MVP User Flow

```
┌────────────────────┐       ┌─────────────────────┐       ┌─────────────────────┐
│   Ticket List      │──(+)──│  Photo Capture      │──────▶│  AI Draft + Submit  │
│  (your reports +   │       │  Camera or Gallery   │       │  Edit / Send        │
│   neighbours feed) │       └─────────────────────┘       └─────────────────────┘
└────────┬───────────┘
         │ (tab)
┌────────▼───────────┐
│  Neighbourhood     │
│  Feed (mock)       │
│  Reports nearby    │
└────────────────────┘
```

**Screen 1 — Ticket List (Home)**
- Two tabs: **Moje zgłoszenia** (your reports) and **W okolicy** (neighbours' reports)
- Your tab: thumbnail, title, date, status (draft / sent); FAB button (+) → opens photo capture
- Neighbours tab: shows mock reports from nearby residents (see mock data below)

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
| LLM | Google Gemini 1.5 Flash | Free, vision-capable, Android SDK |
| Storage | Room | Local ticket list |
| Submission | ACTION_SEND email intent | Zero backend needed |
| Architecture | Single-module MVVM | Hackathon simplicity |

---

## Screens Breakdown

### 1. TicketListScreen
```
┌─────────────────────────────┐
│  Warszawianin        [gear] │  ← TopBar
├──────────────┬──────────────┤
│ Moje zgłosz.│  W okolicy   │  ← Tabs
├──────────────┴──────────────┤
│  ┌───┬────────────────────┐ │
│  │ 📷│ Dziura w chodniku   │ │  ← your ticket card
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

### 1b. Neighbourhood Feed Tab ("W okolicy")
```
┌─────────────────────────────┐
│  Warszawianin        [gear] │
├──────────────┬──────────────┤
│ Moje zgłosz.│  W okolicy   │  ← active tab
├──────────────┴──────────────┤
│  📍 W promieniu 500 m       │
│  ┌───┬────────────────────┐ │
│  │ 📷│ Porzucony rower     │ │  ← neighbour card
│  │   │ ul. Złota 44       │ │
│  │   │ Anna K. • 1 godz.  │ │
│  └───┴────────────────────┘ │
│  ┌───┬────────────────────┐ │
│  │ 📷│ Graffiti na bramie  │ │
│  │   │ ul. Emilii Plater  │ │
│  │   │ Tomasz W. • 3 godz.│ │
│  └───┴────────────────────┘ │
│  ┌───┬────────────────────┐ │
│  │ 📷│ Uszkodzony kosz     │ │
│  │   │ Park Saski         │ │
│  │   │ Maria S. • wczoraj │ │
│  └───┴────────────────────┘ │
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

## Mock Neighbour Requests

Hardcoded in-memory list shown in the **"W okolicy"** tab. No network call needed for MVP — seed data ships with the app. Replace with real API later.

```kotlin
// NeighbourReport.kt — simple data class, no Room needed
data class NeighbourReport(
    val id: Int,
    val photoRes: Int,          // drawable resource id (placeholder images)
    val title: String,
    val category: String,
    val description: String,
    val address: String,
    val authorName: String,     // first name + last initial only
    val hoursAgo: Int,          // relative timestamp label
    val latitude: Double,
    val longitude: Double
)

// MockNeighbourData.kt
object MockNeighbourData {
    val reports = listOf(
        NeighbourReport(
            id = 1,
            photoRes = R.drawable.mock_bike,
            title = "Porzucony rower blokuje wejście",
            category = "infrastruktura",
            description = "Rower bez kłódki stoi przy bramie od ponad tygodnia, " +
                          "utrudniając wejście do budynku.",
            address = "ul. Złota 44, Warszawa",
            authorName = "Anna K.",
            hoursAgo = 1,
            latitude = 52.2297, longitude = 21.0122
        ),
        NeighbourReport(
            id = 2,
            photoRes = R.drawable.mock_graffiti,
            title = "Graffiti na bramie kamienicy",
            category = "czystość",
            description = "Duży napis sprayem na zabytkowej bramie przy ul. Emilii Plater. " +
                          "Wymaga interwencji konserwatora.",
            address = "ul. Emilii Plater 12, Warszawa",
            authorName = "Tomasz W.",
            hoursAgo = 3,
            latitude = 52.2315, longitude = 21.0098
        ),
        NeighbourReport(
            id = 3,
            photoRes = R.drawable.mock_bin,
            title = "Uszkodzony kosz na śmieci",
            category = "czystość",
            description = "Metalowy kosz jest przewrócony i przyspawany do słupka — " +
                          "śmieci wysypują się na ścieżkę.",
            address = "Park Saski, aleja środkowa, Warszawa",
            authorName = "Maria S.",
            hoursAgo = 22,
            latitude = 52.2422, longitude = 21.0058
        ),
        NeighbourReport(
            id = 4,
            photoRes = R.drawable.mock_pothole,
            title = "Głęboka dziura przy przejściu dla pieszych",
            category = "drogi",
            description = "Wyrwa w asfalcie tuż przed pasami — niebezpieczna dla " +
                          "pieszych i rowerzystów, szczególnie nocą.",
            address = "ul. Świętokrzyska / Nowy Świat, Warszawa",
            authorName = "Piotr M.",
            hoursAgo = 5,
            latitude = 52.2361, longitude = 21.0178
        ),
        NeighbourReport(
            id = 5,
            photoRes = R.drawable.mock_lamp,
            title = "Niedziałająca latarnia uliczna",
            category = "oświetlenie",
            description = "Latarnia nie świeci od co najmniej trzech dni. " +
                          "Odcinek chodnika jest całkowicie ciemny po zmroku.",
            address = "ul. Chmielna 30, Warszawa",
            authorName = "Karolina B.",
            hoursAgo = 48,
            latitude = 52.2288, longitude = 21.0145
        ),
        NeighbourReport(
            id = 6,
            photoRes = R.drawable.mock_tree,
            title = "Połamane drzewo zwisa nad chodnikiem",
            category = "zieleń",
            description = "Po wczorajszej burzy konar drzewa zwisa nisko nad chodnikiem " +
                          "i stanowi zagrożenie dla przechodniów.",
            address = "ul. Marszałkowska 84, Warszawa",
            authorName = "Robert J.",
            hoursAgo = 10,
            latitude = 52.2340, longitude = 21.0060
        )
    )
}
```

### Placeholder drawables needed
Add 6 placeholder images to `res/drawable/`:
- `mock_bike.jpg` — parked/abandoned bicycle
- `mock_graffiti.jpg` — graffiti on a wall/gate
- `mock_bin.jpg` — broken/overturned bin
- `mock_pothole.jpg` — road pothole
- `mock_lamp.jpg` — street lamp
- `mock_tree.jpg` — fallen/damaged tree branch

Use free-license photos from Unsplash (search: "Warsaw street problem") or generate placeholders with a solid colour + label for hackathon.

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
- [ ] **T13** — Neighbourhood feed tab with mock `NeighbourReport` data
- [ ] **T14** — NeighbourReportCard composable (photo, title, author, relative time)

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
- ❌ Real neighbour API (mock data only for now)

---

## To Validate Before Building

1. **Email to 19115** — does `kontakt@um.warszawa.pl` process reports sent this way?
2. **Gemini quality** — take 5 photos of real Warsaw issues, run through the prompt, check output
3. **Gemini API key** — get one from Google AI Studio (ai.google.dev)
