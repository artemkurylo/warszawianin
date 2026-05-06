# IDEA.md — Warszawianin (Hackathon MVP)

## Vision

**Warszawianin** — snap a photo of a neighbourhood problem, let AI write the report, send it to Warsaw 19115.

---

## MVP User Flow

```
┌────────────────────┐       ┌─────────────────────┐       ┌─────────────────────┐       ┌──────────────────┐
│   Home Screen      │──(+)──│  Photo Capture      │──────▶│  Report Preview     │──────▶│  History Screen  │
│  (your reports +   │       │  Camera or Gallery   │       │  AI Draft + Submit  │       │  All sent reports│
│   neighbours feed) │       └─────────────────────┘       └─────────────────────┘       └──────────────────┘
└────────┬───────────┘
         │ (tab)
┌────────▼───────────┐
│  Neighbourhood     │
│  Feed (mock)       │
│  Reports nearby    │
└────────────────────┘
```

**Screen 1 — Home (Ticket List)**
- App title: **"Warszawiak"** (large serif heading) — display name; package stays `pl.warszawianin`
- Warm gradient background (`#F8F9FB` → `#FAF8F6` → `#FEF7F3`)
- Two pill-switcher tabs: **Moje zgłoszenia** (your reports) and **W okolicy** (neighbours' reports)
- "Moje" tab: `white/80` rounded cards — thumbnail, serif title, address, relative time, status badge; FAB Camera button (primary, 64dp) → opens photo capture
- "W okolicy" tab: mock neighbour cards — thumbnail, serif title, distance (MapPin), category, supporter count or "Dołącz do zgłoszenia" link
- Empty state: "Brak zgłoszeń" + "Zgłoś pierwszy problem w okolicy"

**Screen 2 — Photo Capture**
- Two options: take a photo (camera) or pick from gallery
- Also captures GPS location silently in background
- Once photo is selected → auto-navigates to Screen 3

**Screen 3 — Report Preview & Submit**
- Header: X back button, "Podgląd zgłoszenia" label, serif report title
- AI detection badge: "Wykryto automatycznie" pill
- 4:3 photo preview (`rounded-2xl`)
- Details card: Kategoria, Lokalizacja (MapPin icon), Data zgłoszenia (Calendar icon)
- AI report card: description + "✨ Wygenerowano przez AI" footnote
- Department card ("Trafi do:") derived from category
- "Popraw" subtle text button → bottom sheet for re-prompting AI (Story 07)
- Bottom sticky bar: "Wyślij zgłoszenie" primary button with Send icon
- On submit → fires email intent to `kontakt@um.warszawa.pl`, then navigates to Screen 4

**Screen 4 — History**
- Header: ArrowLeft back → Home, serif "Historia", subtitle "Twoje zgłoszenia i ich status"
- List of all sent reports: serif title, status pill (Wysłane / Zrealizowane), address, date, category
- Empty state: "Brak zgłoszeń" + "Utwórz pierwsze zgłoszenie" → Home

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

### 1. HomeScreen (TicketListScreen)
```
┌─────────────────────────────┐
│  Warszawiak                 │  ← large serif h1 (no TopAppBar, no gear)
│  Zobacz, co dzieje się...   │  ← muted subtitle
├─────────────────────────────┤
│ ┌─[Moje zgłoszenia]─[W okolicy]─┐ ← pill switcher (white/60 bg)
│ └────────────────────────────────┘
│  ┌───┬────────────────────┐ │
│  │[📷]│ Dziura w chodniku  │ │  ← white/80 card
│  │   │ ul. Marszałkowska  │ │
│  │   │ 2 godz. temu • [Wysłane]│
│  └───┴────────────────────┘ │
│  ┌───┬────────────────────┐ │
│  │[📷]│ Zepsuta latarnia   │ │
│  │   │ ul. Puławska       │ │
│  │   │ wczoraj • [Szkic]  │ │
│  └───┴────────────────────┘ │
│                             │
│                    [📷 FAB] │  ← Camera FAB, primary, 64dp
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

### 3. ReportDraftScreen (Preview mode, per Figma)
```
┌─────────────────────────────┐
│  [X]   Podgląd zgłoszenia   │  ← back (X), centred label
│  Uszkodzona ławka           │  ← serif title h2
├─────────────────────────────┤
│  ✅ Wykryto automatycznie   │  ← primary/10 AI badge
│  ┌─────────────────────┐   │
│  │   [4:3 photo]        │   │  ← rounded-2xl Coil image
│  └─────────────────────┘   │
│  ┌─────────────────────┐   │  ← details card (bg-card border)
│  │ Kategoria: ...       │   │
│  │ 📍 Lokalizacja: ...  │   │
│  │ 📅 Data: ...         │   │
│  └─────────────────────┘   │
│  ┌─────────────────────┐   │  ← AI report card
│  │ Opis zgłoszenia      │   │
│  │ {AI description}     │   │
│  │ ✨ Wygenerowano AI   │   │
│  └─────────────────────┘   │
│  ┌─────────────────────┐   │  ← department card (accent/10)
│  │ Trafi do: {dept}     │   │
│  └─────────────────────┘   │
│       [🔄 Popraw]          │  ← subtle text button → bottom sheet
├─────────────────────────────┤
│  [→ Wyślij zgłoszenie]     │  ← sticky bottom primary button
└─────────────────────────────┘
```

### 4. HistoryScreen (new — from Figma)
```
┌─────────────────────────────┐
│  [←]  Historia              │  ← ArrowLeft back + serif h2
│  Twoje zgłoszenia i status  │  ← muted subtitle
├─────────────────────────────┤
│  ┌──────────────────────┐   │
│  │ Uszkodzona ławka  [✅]│   │  ← status pill top-right
│  │ 📍 Park Łazienkowski │   │
│  │ 6 maja 2026  Infrastr│   │
│  └──────────────────────┘   │
│  ┌──────────────────────┐   │
│  │ Dziura w chodniku [🕐]│   │
│  │ 📍 ul. Marszałkowska │   │
│  │ 3 maja 2026   Drogi  │   │
│  └──────────────────────┘   │
└─────────────────────────────┘
```

Status pills:
- SENT → "Wysłane" (`primary/10` blue, `Clock` icon)
- DRAFT → "Szkic" (grey) — should not normally appear here

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
- [ ] **T3** — HomeScreen (TicketList) + ViewModel — two tabs, neighbour feed, gradient, pill switcher
- [ ] **T4** — PhotoCaptureScreen (CameraX + gallery picker)
- [ ] **T5** — GPS location capture on photo taken
- [ ] **T6** — Gemini integration (send photo + location → get JSON)
- [ ] **T7** — ReportDraftScreen (preview mode per Figma: AI badge, details card, department card, sticky send button)
- [ ] **T8** — "Popraw" flow (bottom sheet re-prompt AI with user feedback)
- [ ] **T9** — "Wyślij zgłoszenie" flow (compose email intent, save as SENT, navigate to History)
- [ ] **T10** — HistoryScreen (list all sent reports, status pills, back to Home)
- [ ] **T11** — Permissions handling (camera, location, storage)
- [ ] **T12** — Basic error states & loading UI
- [ ] **T13** — Reverse geocoding (GPS → address string)

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
