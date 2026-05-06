# Story 08 — Submit via Email ("Wyślij")

## Goal

Compose and send the report via the system email app using `ACTION_SEND` intent with photo attachment.

## Depends On

- Story 06 (Report Draft Screen)

## Acceptance Criteria

- [ ] "Wyślij" button composes an email intent pre-filled per template in IDEA.md
- [ ] To: `kontakt@um.warszawa.pl`
- [ ] Subject: `Zgłoszenie: {title}`
- [ ] Body: formatted text with category, location, description
- [ ] Photo attached via FileProvider URI
- [ ] After intent fires, report status updated to `SENT` with `sentAt` timestamp
- [ ] User is navigated back to TicketListScreen
- [ ] Works with Gmail, Outlook, and default email apps

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
│   ├── ReportDraftScreen.kt          # MODIFY — wire Wyślij button
│   └── ReportDraftViewModel.kt       # MODIFY — add submit action
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
- If no email app is available, show a Toast: "Brak aplikacji email"
