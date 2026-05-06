package pl.warszawianin.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

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
