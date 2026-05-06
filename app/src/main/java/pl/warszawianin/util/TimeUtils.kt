package pl.warszawianin.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object TimeUtils {
    fun relativeTime(timestampMillis: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestampMillis

        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            minutes < 1 -> "przed chwilą"
            minutes < 60 -> "$minutes min temu"
            hours < 24 -> "$hours godz. temu"
            days == 1L -> "wczoraj"
            days < 7 -> "$days dni temu"
            else -> {
                val sdf = SimpleDateFormat("d MMM yyyy", Locale("pl", "PL"))
                sdf.format(Date(timestampMillis))
            }
        }
    }

    fun formatDate(timestampMillis: Long): String {
        val sdf = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("pl", "PL"))
        return sdf.format(Date(timestampMillis))
    }

    fun formatDateShort(timestampMillis: Long): String {
        val sdf = SimpleDateFormat("d MMMM yyyy", Locale("pl", "PL"))
        return sdf.format(Date(timestampMillis))
    }
}
