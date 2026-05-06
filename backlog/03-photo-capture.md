# Story 03 — Photo Capture (Camera + Gallery)

## Goal

Implement the PhotoCaptureScreen with actual camera capture and gallery picker. Save photo to app-private storage and create a DRAFT report in Room.

## Depends On

- Story 01 (Room Database)

## Acceptance Criteria

- [ ] "Zrób zdjęcie" button launches CameraX capture (or `ActivityResultContracts.TakePicture`)
- [ ] "Wybierz z galerii" button launches Photo Picker (`PickVisualMedia`)
- [ ] Photo is copied/saved to app-internal storage (survives gallery deletion)
- [ ] After photo is obtained, a new `Report` with status `DRAFT` is inserted into Room
- [ ] Navigation proceeds to ReportDraftScreen with the new report's ID
- [ ] Camera preview is shown in the screen (stretch — can use simple buttons for MVP)
- [ ] Works on API 26+

## Files to Create/Modify

```
app/src/main/java/pl/warszawianin/ui/screens/photocapture/
├── PhotoCaptureScreen.kt        # MODIFY — replace placeholder
└── PhotoCaptureViewModel.kt     # CREATE
```

## Implementation Notes

- Use `ActivityResultContracts.TakePicture()` for camera — simpler than full CameraX viewfinder for MVP
- Use `ActivityResultContracts.PickVisualMedia()` for gallery
- Save photos to `context.filesDir/photos/` with UUID filename
- On photo obtained:
  ```kotlin
  val report = Report(
      photoUri = savedFile.toUri().toString(),
      latitude = null,  // filled by Story 04
      longitude = null,
      address = null,
      category = "",
      title = "",
      description = "",
      status = ReportStatus.DRAFT,
      createdAt = System.currentTimeMillis(),
      sentAt = null
  )
  val id = reportDao.insert(report)
  // navigate to report_draft/{id}
  ```
- Need `FileProvider` in AndroidManifest for camera URI on API 26+
- Add `<provider>` to manifest with `file_paths.xml` resource

## Resources to Create

```
app/src/main/res/xml/file_paths.xml
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <files-path name="photos" path="photos/" />
</paths>
```
