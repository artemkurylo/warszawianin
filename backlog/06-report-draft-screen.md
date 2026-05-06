# Story 06 — Report Draft Screen (Real UI)

## Goal

Replace the placeholder ReportDraftScreen with the full UI: photo preview, location, AI-generated fields (editable), and action buttons.

## Depends On

- Story 05 (Gemini Integration — provides AI content)

## Acceptance Criteria

- [ ] `ReportDraftViewModel` loads Report by ID from Room, triggers Gemini if fields are empty
- [ ] Shows photo preview at top (from `report.photoUri`)
- [ ] Shows location line: "📍 ul. Marszałkowska 12" (or "Lokalizacja nieznana" if null)
- [ ] Shows category dropdown/chip (AI-suggested, user-changeable)
- [ ] Shows editable title TextField (pre-filled by AI)
- [ ] Shows editable description TextField (multiline, pre-filled by AI)
- [ ] Loading state: spinner + "Analizuję zdjęcie..." while waiting for Gemini
- [ ] "Popraw" button visible (wired in Story 07)
- [ ] "Wyślij" button visible (wired in Story 08)
- [ ] Edits auto-save to Room (debounced or on blur)

## UI Spec (from IDEA.md)

```
┌─────────────────────────────┐
│  ← Zgłoszenie               │
├─────────────────────────────┤
│  ┌─────────────────────┐   │
│  │   [photo preview]   │   │
│  └─────────────────────┘   │
│  📍 ul. Marszałkowska 12   │
│  Kategoria: [Drogi ▼]      │
│  Tytuł:                     │
│  ┌─────────────────────┐   │
│  │ Dziura w chodniku   │   │
│  └─────────────────────┘   │
│  Opis:                      │
│  ┌─────────────────────┐   │
│  │ Na chodniku przy...  │   │
│  └─────────────────────┘   │
│  [ 🔄 Popraw ]  [ ✉️ Wyślij ]│
└─────────────────────────────┘
```

## Files to Create/Modify

```
app/src/main/java/pl/warszawianin/ui/screens/reportdraft/
├── ReportDraftScreen.kt         # MODIFY — full implementation
├── ReportDraftViewModel.kt      # CREATE
└── components/
    └── CategorySelector.kt      # CREATE — dropdown or chip group
```

## Implementation Notes

- Categories list: `["Drogi", "Oświetlenie", "Zieleń", "Czystość", "Infrastruktura", "Bezpieczeństwo", "Inne"]`
- Use `OutlinedTextField` for title and description
- Photo preview: use Coil `AsyncImage` or load bitmap from URI
- ViewModel states: `Loading`, `Ready(report)`, `Error(message)`
- On screen entry: if `report.title.isBlank()` → call Gemini, else show existing data
- Save edits with `reportDao.update(...)` — debounce 500ms or save on navigation away
