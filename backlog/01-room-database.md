# Story 01 — Room Database + Report Entity

## Goal

Set up Room persistence with the `Report` entity and DAO so other features can store/retrieve reports.

## Acceptance Criteria

- [ ] `Report` data class annotated with `@Entity(tableName = "reports")`
- [ ] `ReportStatus` enum: `DRAFT`, `SENT`
- [ ] `ReportDao` interface with: `getAll(): Flow<List<Report>>`, `getById(id: Long): Flow<Report?>`, `insert(report: Report): Long`, `update(report: Report)`, `delete(report: Report)`
- [ ] `AppDatabase` abstract class extending `RoomDatabase`, annotated properly
- [ ] Hilt `@Module` providing the database singleton and DAO
- [ ] App builds cleanly with `./gradlew assembleDebug`

## Data Model (from IDEA.md)

```kotlin
@Entity(tableName = "reports")
data class Report(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val photoUri: String,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val category: String,
    val title: String,
    val description: String,
    val status: ReportStatus,
    val createdAt: Long,
    val sentAt: Long?
)

enum class ReportStatus { DRAFT, SENT }
```

## Files to Create

```
app/src/main/java/pl/warszawianin/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── ReportDao.kt
│   │   └── Converters.kt       # TypeConverter for ReportStatus
│   └── model/
│       ├── Report.kt
│       └── ReportStatus.kt
├── di/
│   └── DatabaseModule.kt
```

## Implementation Notes

- Database name: `"warszawianin_db"`
- Use `@TypeConverters` for the enum (store as String)
- Provide via Hilt `@SingletonComponent` with `@Provides`
- `getAll()` should order by `createdAt DESC`
