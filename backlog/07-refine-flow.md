# Story 07 — Refine Flow ("Popraw")

## Goal

Allow the user to ask Gemini to regenerate the report with additional instructions/corrections.

## Depends On

- Story 06 (Report Draft Screen)

## Acceptance Criteria

- [ ] "Popraw" button opens a bottom sheet or dialog with a text field
- [ ] User types feedback (e.g. "Opisz bardziej szczegółowo", "To jest latarnia, nie drzewo")
- [ ] On confirm, sends photo + original prompt + user feedback to Gemini
- [ ] Shows loading state while waiting
- [ ] Updates report fields with new AI response
- [ ] Previous AI output is replaced (no history needed for MVP)
- [ ] User can cancel and keep current text

## Files to Modify

```
app/src/main/java/pl/warszawianin/
├── data/ai/
│   └── GeminiService.kt              # MODIFY — add refine method
├── ui/screens/reportdraft/
│   ├── ReportDraftScreen.kt          # MODIFY — add bottom sheet
│   └── ReportDraftViewModel.kt       # MODIFY — add refine action
```

## Implementation Notes

- Extended prompt for refine:
  ```
  Previous analysis:
  Kategoria: {current_category}
  Tytuł: {current_title}
  Opis: {current_description}

  User feedback: {user_feedback}

  Please regenerate the report taking the feedback into account.
  Return ONLY valid JSON: {"kategoria": "...", "tytuł": "...", "opis": "..."}
  ```
- Use `ModalBottomSheet` from Material 3
- Bottom sheet has: label "Twoje uwagi", text field, "Wyślij" button
- ViewModel action: `refine(feedback: String)` → calls Gemini → updates Report in Room
