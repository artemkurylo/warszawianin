# Story 06 — Report Preview & Draft Screen

## Goal

Replace the placeholder ReportDraftScreen with the full UI: photo preview, AI detection badge, location, category, description (read-only preview with edit option), and a single "Wyślij zgłoszenie" action.

## Depends On

- Story 05 (Gemini Integration — provides AI content)

## Acceptance Criteria

- [ ] `ReportDraftViewModel` loads Report by ID from Room, triggers Gemini if fields are empty
- [ ] Header: back button (X icon) top-left, "Podgląd zgłoszenia" label centred, large serif report title below
- [ ] AI detection badge: `CheckCircle2` icon + "Wykryto automatycznie" pill in `primary/10` colour
- [ ] Photo preview: `4:3` aspect ratio, `rounded-2xl`, loaded from `report.photoUri` via Coil
- [ ] Details card (`bg-card`, `border`, `rounded-2xl`):
  - Kategoria: plain text (AI-suggested)
  - Lokalizacja: `MapPin` primary icon + address string (or "Lokalizacja nieznana")
  - Data zgłoszenia: `Calendar` primary icon + formatted date
- [ ] AI report card: serif "Opis zgłoszenia" heading + description text + "✨ Wygenerowano przez AI" footnote
- [ ] Department info card (`bg-accent/10`, `border-accent/20`): "Trafi do:" label + department name derived from category
- [ ] Loading state: centred spinner + "Analizuję zdjęcie..." while waiting for Gemini
- [ ] Error state: error card with retry button if Gemini fails
- [ ] "Popraw" secondary action: subtle text button below description card opening a bottom sheet (wired in Story 07)
- [ ] Bottom sticky bar: full-width primary "Wyślij zgłoszenie" button with `Send` icon (wired in Story 08)
- [ ] Edits to title/description auto-save to Room via debounced update

## UI Spec (from Figma)

```
┌─────────────────────────────┐
│  [X]   Podgląd zgłoszenia   │  ← back button + centred label
│  Uszkodzona ławka           │  ← serif title h2
├─────────────────────────────┤
│  ✅ Wykryto automatycznie   │  ← primary/10 pill badge
│  ┌─────────────────────┐   │
│  │   [4:3 photo]        │   │  ← rounded-2xl
│  └─────────────────────┘   │
│  ┌─────────────────────┐   │
│  │ Kategoria: ...       │   │  ← details card
│  │ 📍 Lokalizacja: ...  │   │
│  │ 📅 Data: ...         │   │
│  └─────────────────────┘   │
│  ┌─────────────────────┐   │
│  │ Opis zgłoszenia      │   │  ← AI report card
│  │ {description text}   │   │
│  │ ✨ Wygenerowano AI   │   │
│  └─────────────────────┘   │
│  ┌─────────────────────┐   │
│  │ Trafi do: {dept}     │   │  ← accent card
│  └─────────────────────┘   │
├─────────────────────────────┤
│  [→ Wyślij zgłoszenie]     │  ← sticky bottom primary button
└─────────────────────────────┘
```

## Department Mapping

```kotlin
fun categoryToDepartment(category: String): String = when (category.lowercase()) {
    "drogi", "chodniki" -> "Zarząd Dróg Miejskich"
    "oświetlenie" -> "Stołeczne Centrum Oszczędzania Energii"
    "zieleń" -> "Zarząd Zieleni m.st. Warszawy"
    "czystość" -> "Miasto Stołeczne Warszawa – Czystość"
    "infrastruktura" -> "Zarząd Mienia m.st. Warszawy"
    "bezpieczeństwo" -> "Straż Miejska m.st. Warszawy"
    else -> "Urząd m.st. Warszawy"
}
```

## Files to Create/Modify

```
app/src/main/java/pl/warszawianin/ui/screens/reportdraft/
├── ReportDraftScreen.kt         # MODIFY — full implementation per Figma
├── ReportDraftViewModel.kt      # CREATE
└── components/
    └── CategorySelector.kt      # CREATE — used in "Popraw" flow (Story 07)
```

## Implementation Notes

- Categories list: `["Drogi", "Oświetlenie", "Zieleń", "Czystość", "Infrastruktura", "Bezpieczeństwo", "Inne"]`
- ViewModel states: `Loading`, `Ready(report)`, `Error(message)` sealed class
- On screen entry: if `report.title.isBlank()` → call Gemini, else show existing data
- Date format: "d MMMM yyyy, HH:mm" in Polish locale
- Navigation on back (X): back to PhotoCaptureScreen (or pop back stack)
- Navigation after send: to HistoryScreen (Story 08 / Story 12)
- Photo fallback: grey gradient placeholder if URI can't load
