# Story 12 — History Screen

## Goal

Add a dedicated HistoryScreen that shows all submitted reports with their status. This is the destination after "Wyślij zgłoszenie" fires.

## Depends On

- Story 01 (Room Database)
- Story 08 (Submit Email — navigates here after send)

## Acceptance Criteria

- [ ] `HistoryViewModel` observes `ReportDao.getAll()` ordered by `createdAt DESC`
- [ ] Header: back button (`ArrowLeft`) → Home, large serif "Historia" h2, subtitle "Twoje zgłoszenia i ich status"
- [ ] Each report card (`bg-card`, `border`, `rounded-2xl`):
  - Serif title (h3)
  - Status pill top-right: green "Zrealizowane" (`CheckCircle2`) for SENT, blue "Wysłane" (`Clock`) for SENT (use SENT for both in MVP — no "completed" state in Room yet)
  - `MapPin` icon + address (or "Lokalizacja nieznana")
  - Date formatted "d MMMM yyyy" in Polish
  - Category text right-aligned
- [ ] Empty state: "Brak zgłoszeń" + "Utwórz pierwsze zgłoszenie" button → navigates to Home
- [ ] Tapping a card navigates to ReportDraftScreen with that report's ID (for review)

## UI Spec (from Figma)

```
┌─────────────────────────────┐
│  [←]  Historia              │  ← ArrowLeft back + serif h2
│  Twoje zgłoszenia i status  │  ← muted subtitle
├─────────────────────────────┤
│  ┌──────────────────────┐   │
│  │ Uszkodzona ławka  [✅]│   │  ← serif title + status pill
│  │ 📍 Park Łazienkowski │   │
│  │ 6 maja 2026   Infra  │   │
│  └──────────────────────┘   │
│  ┌──────────────────────┐   │
│  │ Dziura w chodniku [🕐]│   │
│  │ 📍 ul. Marszałkowska │   │
│  │ 3 maja 2026   Drogi  │   │
│  └──────────────────────┘   │
└─────────────────────────────┘
```

## Status Display (MVP simplification)

Since Room only has `DRAFT` and `SENT`, map as follows until a `COMPLETED` status is added:
- `SENT` → "Wysłane" pill (`primary/10` blue, `Clock` icon)
- `DRAFT` → "Szkic" pill (grey) — shouldn't appear here normally

## Files to Create

```
app/src/main/java/pl/warszawianin/ui/screens/history/
├── HistoryScreen.kt             # CREATE
└── HistoryViewModel.kt          # CREATE
```

Also:
```
app/src/main/java/pl/warszawianin/navigation/NavGraph.kt   # MODIFY — add history route
```

## Implementation Notes

- Route: `"history"` in NavGraph, no arguments needed (shows all reports)
- Date format: `SimpleDateFormat("d MMMM yyyy", Locale("pl", "PL"))`
- Card `active:scale-[0.99]` press feedback: `Modifier.clickable` with `indication`
- Back navigation from History goes to Home (`popUpTo("ticket_list") { inclusive = false }`)
