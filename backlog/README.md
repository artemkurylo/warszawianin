# Backlog

Stories for AI agent implementation. Each file is a self-contained unit of work.

## Priority Order

| # | Story | Dependency |
|---|-------|-----------|
| 1 | `01-room-database.md` | None |
| 2 | `02-ticket-list-screen.md` | 01 |
| 3 | `03-photo-capture.md` | 01 |
| 4 | `04-gps-location.md` | 03 |
| 5 | `05-gemini-integration.md` | 03, 04 |
| 6 | `06-report-draft-screen.md` | 05 |
| 7 | `07-refine-flow.md` | 06 |
| 8 | `08-submit-email.md` | 06, 12 |
| 9 | `09-permissions-handling.md` | 03, 04 |
| 10 | `10-error-states-loading.md` | All above |
| 11 | `11-reverse-geocoding.md` | 04 |
| 12 | `12-history-screen.md` | 01, 08 |

## Rules for the implementing agent

1. **Branch per story** — create `feat/<story-slug>` from `main`
2. **Build must pass** — run `./gradlew assembleDebug` before committing
3. **Single-module MVVM** — all code in `:app`, use Hilt ViewModels
4. **Compose only** — no XML layouts, no Fragments
5. **Polish language** — all user-facing strings in Polish
6. **Read IDEA.md** — it's the source of truth for UX and data model
