# Story 02 — Home Screen (Moje zgłoszenia + W okolicy tabs)

## Goal

Replace the placeholder TicketListScreen with a real two-tab home screen: your own reports from Room and a mock neighbourhood feed.

## Depends On

- Story 01 (Room Database)

## Acceptance Criteria

- [ ] `TicketListViewModel` injected with Hilt, observes `ReportDao.getAll()`
- [ ] Screen header: large serif "Warszawiak" h1 + subtitle "Zobacz, co dzieje się w Twojej okolicy" (no TopAppBar, no gear icon)
- [ ] Warm gradient background: `Brush.linearGradient` `#F8F9FB` → `#FAF8F6` → `#FEF7F3`, fills entire screen
- [ ] Pill tab switcher: `white/60` container, `rounded-2xl`, `1.5dp` padding; active tab = `bg-white` with shadow; text: "Moje zgłoszenia" | "W okolicy"
- [ ] **"Moje zgłoszenia" tab**: cards from Room — thumbnail, serif title, address, relative time, status pill badge
- [ ] **"W okolicy" tab**: `MockNeighbourData` cards — thumbnail, serif title, `MapPin` icon + distance, category dot-separator, supporter count or join button
- [ ] Card style: `white/80` background, `rounded-2xl`, subtle shadow, `4dp` content padding; `80×80dp` thumbnail with `rounded-xl`
- [ ] Status badge: green pill "Wysłane" for SENT, grey pill "Szkic" for DRAFT
- [ ] Supporter label: "{n} osoba zgłosiła" (n=1) / "{n} osoby zgłosiły" (n>1) / "Dołącz do zgłoszenia" link (n=0)
- [ ] Empty state (Moje tab): centred "Brak zgłoszeń" + "Zgłoś pierwszy problem w okolicy"
- [ ] FAB: Camera icon (`Camera` lucide equivalent), primary colour, `64dp` circle, bottom-right; tapping navigates to PhotoCaptureScreen
- [ ] Each "Moje" card tappable → navigates to ReportDraftScreen with that report's ID
- [ ] List shows newest first

## UI Spec (from Figma)

```
┌─────────────────────────────────┐
│  Warszawiak                     │  ← large serif h1
│  Zobacz, co dzieje się...       │  ← muted subtitle
├─────────────────────────────────┤
│ ┌──[Moje zgłoszenia]──[W okolicy]┐ ← pill tab switcher (white/60 bg)
│ └────────────────────────────────┘
│  ┌────┬──────────────────────┐  │  ← Card: white/80, rounded-2xl
│  │[📷]│ Dziura w chodniku    │  │
│  │    │ 📍 340 m • Drogi     │  │
│  │    │ 5 osoby zgłosiły     │  │
│  └────┴──────────────────────┘  │
│                          [📷FAB]│  ← Camera FAB, primary, 64dp
└─────────────────────────────────┘
```

## Files to Create/Modify

```
app/src/main/java/pl/warszawianin/
├── ui/screens/ticketlist/
│   ├── TicketListScreen.kt          # MODIFY — full redesign per Figma
│   ├── TicketListViewModel.kt       # CREATE
│   └── components/
│       ├── MyReportCard.kt          # CREATE — your report card (thumbnail + status badge)
│       └── NeighbourReportCard.kt   # CREATE — neighbour card (distance + supporters)
├── data/mock/
│   ├── NeighbourReport.kt           # CREATE — data class from IDEA.md
│   └── MockNeighbourData.kt         # CREATE — 6 hardcoded entries from IDEA.md
```

## Implementation Notes

- Use `collectAsStateWithLifecycle()` for Room Flow
- Gradient: `Box(modifier = Modifier.background(brush = gradientBrush).fillMaxSize())`
- Thumbnail: `AsyncImage` (Coil) for `report.photoUri`; neighbour cards use `painterResource(report.photoRes)`
- Relative time helper: "2 godz. temu", "wczoraj", "3 dni temu" based on `createdAt` millis
- `NeighbourReport` and `MockNeighbourData` are pure in-memory — no Room, no DI needed
- Add `coil-compose` to `libs.versions.toml`
- App display name in this header is "Warszawiak" (design) — keep package/class names as `warszawianin`
