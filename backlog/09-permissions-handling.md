# Story 09 — Permissions Handling

## Goal

Properly request and handle runtime permissions for Camera and Location before using those features.

## Depends On

- Story 03 (Photo Capture — needs CAMERA)
- Story 04 (GPS Location — needs ACCESS_FINE_LOCATION)

## Acceptance Criteria

- [ ] Camera permission requested before opening camera (not needed for gallery picker)
- [ ] Location permission requested before capturing GPS
- [ ] Shows rationale dialog if permission was previously denied
- [ ] Gracefully degrades: no camera permission → gallery-only mode; no location → null coords
- [ ] Permissions requested at point-of-use (not all at app launch)
- [ ] Works correctly with "Don't ask again" scenario (opens Settings)
- [ ] Accompanist Permissions library OR manual `rememberLauncherForActivityResult` — either is fine

## Files to Create/Modify

```
app/src/main/java/pl/warszawianin/
├── ui/screens/photocapture/
│   └── PhotoCaptureScreen.kt        # MODIFY — add permission flow before camera
├── util/
│   └── PermissionUtils.kt           # CREATE — reusable permission helpers
```

## Implementation Notes

- Use `rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())` in Compose
- Flow for camera:
  1. User taps "Zrób zdjęcie"
  2. Check `CAMERA` permission
  3. If granted → open camera
  4. If denied + shouldShowRationale → show dialog explaining why
  5. If denied + !shouldShowRationale → show "Otwórz ustawienia" button
- Flow for location: request `ACCESS_FINE_LOCATION` when entering PhotoCaptureScreen
  - If denied, proceed without location (non-blocking)
- Rationale texts (in Polish):
  - Camera: "Potrzebujemy dostępu do aparatu, aby zrobić zdjęcie problemu"
  - Location: "Lokalizacja pomoże dokładnie wskazać miejsce problemu"
