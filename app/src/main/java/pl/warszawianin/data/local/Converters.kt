package pl.warszawianin.data.local

import androidx.room.TypeConverter
import pl.warszawianin.data.model.ReportStatus

class Converters {
    @TypeConverter
    fun fromReportStatus(status: ReportStatus): String = status.name

    @TypeConverter
    fun toReportStatus(value: String): ReportStatus = ReportStatus.valueOf(value)
}
