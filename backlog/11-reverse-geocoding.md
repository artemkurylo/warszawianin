# Story 11 — Reverse Geocoding

## Goal

Convert GPS coordinates into a human-readable Polish address string for display and email.

## Depends On

- Story 04 (GPS Location)

## Acceptance Criteria

- [ ] After obtaining lat/lng, resolve to street address via Android `Geocoder`
- [ ] Store result in `report.address` field
- [ ] Format: "ul. Marszałkowska 12" or "al. Jerozolimskie 45" (Polish street format)
- [ ] Fallback: if Geocoder fails, show raw coords "GPS: 52.2297, 21.0122"
- [ ] Non-blocking: doesn't prevent navigation or UI rendering
- [ ] Works offline if cached by system (best-effort)

## Files to Create/Modify

```
app/src/main/java/pl/warszawianin/
├── data/
│   └── location/
│       └── GeocoderService.kt        # CREATE
├── ui/screens/reportdraft/
│   └── ReportDraftViewModel.kt       # MODIFY — trigger geocoding
```

## Implementation Notes

- Use `android.location.Geocoder`:
  ```kotlin
  val geocoder = Geocoder(context, Locale("pl", "PL"))
  // API 33+ has async version:
  if (Build.VERSION.SDK_INT >= 33) {
      geocoder.getFromLocation(lat, lng, 1) { addresses ->
          // handle result
      }
  } else {
      // blocking call — run on Dispatchers.IO
      val addresses = geocoder.getFromLocation(lat, lng, 1)
  }
  ```
- Extract: `address.thoroughfare` (street) + `address.subThoroughfare` (number)
- If thoroughfare is null, try `address.getAddressLine(0)`
- Update report in Room after geocoding completes
- This can run in parallel with Gemini call — no need to wait for it
