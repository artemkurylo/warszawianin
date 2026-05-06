# Story 02 — Ticket List Screen (Real Data)

## Goal

Replace the placeholder TicketListScreen with a real implementation that reads reports from Room and displays them.

## Depends On

- Story 01 (Room Database)

## Acceptance Criteria

- [ ] `TicketListViewModel` injected with Hilt, observes `ReportDao.getAll()`
- [ ] Shows list of report cards: photo thumbnail, title, address, relative time, status badge
- [ ] Empty state: icon + "Brak zgłoszeń" text when no reports exist
- [ ] FAB (+) navigates to PhotoCaptureScreen
- [ ] Each card is tappable → navigates to ReportDraftScreen with that report's ID
- [ ] Status badge: green "Wysłane" for SENT, grey "Szkic" for DRAFT
- [ ] List shows newest first

## UI Spec (from IDEA.md)

```
┌─────────────────────────────┐
│  Warszawianin        [gear] │
├─────────────────────────────┤
│  ┌───┬────────────────────┐ │
│  │ 📷│ Dziura w chodniku   │ │
│  │   │ ul. Marszałkowska  │ │
│  │   │ 2 godz. temu • Wysłane│
│  └───┴────────────────────┘ │
│                     [ + ]   │
└─────────────────────────────┘
```

## Files to Create/Modify

```
app/src/main/java/pl/warszawianin/ui/screens/ticketlist/
├── TicketListScreen.kt          # MODIFY — replace placeholder
├── TicketListViewModel.kt       # CREATE
└── components/
    └── ReportCard.kt            # CREATE — reusable card composable
```

## Implementation Notes

- Use `collectAsStateWithLifecycle()` for Flow
- Photo thumbnail: load from `report.photoUri` using `AsyncImage` (add Coil dependency) or simple `Image` with file URI
- Relative time: helper function formatting `createdAt` as "2 godz. temu", "wczoraj" etc.
- Consider adding `coil-compose` to `libs.versions.toml` for image loading
- TopAppBar gear icon is a placeholder — no settings screen yet, just show the icon
