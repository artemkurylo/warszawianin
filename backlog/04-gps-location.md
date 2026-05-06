# Story 04 — GPS Location Capture

## Goal

Silently capture the device's GPS coordinates when a photo is taken, and store them in the report.

## Depends On

- Story 03 (Photo Capture)

## Acceptance Criteria

- [ ] When photo is captured/selected, request last known location from `FusedLocationProviderClient`
- [ ] If last location is stale (>2 min), request a fresh single location update
- [ ] Store `latitude` and `longitude` in the Report entity
- [ ] Works without location permission (just leaves lat/lng as null — graceful degradation)
- [ ] Does NOT block photo flow — if location takes too long, proceed with null coords
- [ ] Location is fetched in ViewModel coroutine, not on main thread

## Files to Create/Modify

```
app/src/main/java/pl/warszawianin/
├── data/
│   └── location/
│       └── LocationProvider.kt       # CREATE — wrapper around FusedLocation
├── di/
│   └── LocationModule.kt            # CREATE — Hilt provides FusedLocationProviderClient
├── ui/screens/photocapture/
│   └── PhotoCaptureViewModel.kt     # MODIFY — add location fetch
```

## Implementation Notes

- Inject `FusedLocationProviderClient` via Hilt module:
  ```kotlin
  @Provides
  fun provideFusedLocationClient(@ApplicationContext context: Context) =
      LocationServices.getFusedLocationProviderClient(context)
  ```
- Use `lastLocation` first, then `getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancelToken)` as fallback
- Timeout: 5 seconds max, then give up and use null
- Update the Report after location is obtained:
  ```kotlin
  reportDao.update(report.copy(latitude = lat, longitude = lng))
  ```
- Permission check is handled in Story 09, but this code should gracefully handle SecurityException
