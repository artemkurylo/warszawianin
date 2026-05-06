package pl.warszawianin.util

object CategoryUtils {
    val categories = listOf(
        "Drogi", "Oświetlenie", "Zieleń", "Czystość",
        "Infrastruktura", "Bezpieczeństwo", "Inne"
    )

    fun categoryToDepartment(category: String): String = when (category.lowercase()) {
        "drogi", "chodniki" -> "Zarząd Dróg Miejskich"
        "oświetlenie" -> "Stołeczne Centrum Oszczędzania Energii"
        "zieleń" -> "Zarząd Zieleni m.st. Warszawy"
        "czystość" -> "Miasto Stołeczne Warszawa – Czystość"
        "infrastruktura" -> "Zarząd Mienia m.st. Warszawy"
        "bezpieczeństwo" -> "Straż Miejska m.st. Warszawy"
        else -> "Urząd m.st. Warszawy"
    }
}
