# Story 08 — Submit via Email ("Wyślij zgłoszenie")

## Goal

Compose and send the report via the system email app using `ACTION_SEND` intent with photo attachment. After sending, navigate to HistoryScreen.

## Depends On

- Story 06 (Report Draft Screen)
- Story 12 (History Screen — navigation target)

## Acceptance Criteria

- [ ] "Wyślij zgłoszenie" button fires an `ACTION_SEND` email intent pre-filled per template in IDEA.md
- [ ] To: `kontakt@um.warszawa.pl`
- [ ] Subject: `Zgłoszenie: {title}`
- [ ] Body: formatted text with category, location, description, sign-off
- [ ] Photo attached via FileProvider URI
- [ ] After intent fires, report status updated to `SENT` with `sentAt` timestamp
- [ ] User is navigated to **HistoryScreen** (not back to ticket list) — matches Figma flow
- [ ] Works with Gmail, Outlook, and default email apps
- [ ] If no email app is installed: Toast "Brak aplikacji email"

## Email Template (from IDEA.md)

```
Szanowni Państwo,

Zgłaszam następujący problem:

Kategoria: {category}
Lokalizacja: {address} (GPS: {lat}, {lng})

{description}

Zdjęcie w załączniku.

Z poważaniem,
Użytkownik aplikacji Warszawianin

---
Wysłano z aplikacji Warszawianin
```

## Files to Create/Modify

```
app/src/main/java/pl/warszawianin/
├── data/
│   └── email/
│       └── EmailComposer.kt          # CREATE — builds the Intent
├── ui/screens/reportdraft/
│   ├── ReportDraftScreen.kt          # MODIFY — wire "Wyślij zgłoszenie" button
│   └── ReportDraftViewModel.kt       # MODIFY — add submit action
├── navigation/
│   └── NavGraph.kt                   # MODIFY — add HistoryScreen destination
```

## Implementation Notes

- Use `Intent.ACTION_SEND` with:
  ```kotlin
  intent.type = "message/rfc822"
  intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("kontakt@um.warszawa.pl"))
  intent.putExtra(Intent.EXTRA_SUBJECT, "Zgłoszenie: $title")
  intent.putExtra(Intent.EXTRA_TEXT, bodyText)
  intent.putExtra(Intent.EXTRA_STREAM, photoContentUri)
  intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
  ```
- `photoContentUri` must come from `FileProvider.getUriForFile()` — reuse provider from Story 03
- Mark as SENT optimistically after `startActivity` (we can't know if user actually sent)
- Navigate to `history` route after marking SENT — from there user can tap back to Home
