# Story 10 — Error States & Loading UI

## Goal

Add proper loading indicators and error handling across all screens for a polished UX.

## Depends On

- All previous stories (this is the polish pass)

## Acceptance Criteria

- [ ] TicketListScreen: loading skeleton while Room query initializes
- [ ] PhotoCaptureScreen: loading overlay while saving photo
- [ ] ReportDraftScreen: centered spinner + "Analizuję zdjęcie..." during Gemini call
- [ ] ReportDraftScreen: error card with retry button if Gemini fails
- [ ] Network error: "Brak połączenia z internetem. Sprawdź sieć i spróbuj ponownie."
- [ ] Gemini quota error: "Zbyt wiele zapytań. Spróbuj za chwilę."
- [ ] Generic error: "Coś poszło nie tak. Spróbuj ponownie."
- [ ] All error messages in Polish
- [ ] Snackbar for non-critical errors (e.g. location timeout)

## Files to Create/Modify

```
app/src/main/java/pl/warszawianin/
├── ui/
│   └── components/
│       ├── LoadingOverlay.kt         # CREATE — reusable full-screen loading
│       ├── ErrorCard.kt              # CREATE — error + retry button
│       └── LoadingSkeleton.kt        # CREATE — placeholder cards
├── ui/screens/ticketlist/
│   └── TicketListScreen.kt           # MODIFY
├── ui/screens/reportdraft/
│   └── ReportDraftScreen.kt          # MODIFY
│   └── ReportDraftViewModel.kt       # MODIFY — sealed UiState class
```

## Implementation Notes

- ViewModel UI state pattern:
  ```kotlin
  sealed class ReportDraftUiState {
      object Loading : ReportDraftUiState()
      data class Ready(val report: Report) : ReportDraftUiState()
      data class Error(val message: String) : ReportDraftUiState()
  }
  ```
- Use `CircularProgressIndicator` for loading spinners
- Error card design: red-ish surface, error icon, message text, "Spróbuj ponownie" button
- Snackbar host in Scaffold for transient messages
