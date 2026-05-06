package pl.warszawianin.util

import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryUtilsTest {

    @Test
    fun `categoryToDepartment maps drogi correctly`() {
        assertEquals("Zarząd Dróg Miejskich", CategoryUtils.categoryToDepartment("Drogi"))
    }

    @Test
    fun `categoryToDepartment maps drogi case-insensitive`() {
        assertEquals("Zarząd Dróg Miejskich", CategoryUtils.categoryToDepartment("drogi"))
    }

    @Test
    fun `categoryToDepartment maps oswietlenie correctly`() {
        assertEquals(
            "Stołeczne Centrum Oszczędzania Energii",
            CategoryUtils.categoryToDepartment("Oświetlenie")
        )
    }

    @Test
    fun `categoryToDepartment maps zielen correctly`() {
        assertEquals(
            "Zarząd Zieleni m.st. Warszawy",
            CategoryUtils.categoryToDepartment("Zieleń")
        )
    }

    @Test
    fun `categoryToDepartment maps czystosc correctly`() {
        assertEquals(
            "Miasto Stołeczne Warszawa – Czystość",
            CategoryUtils.categoryToDepartment("Czystość")
        )
    }

    @Test
    fun `categoryToDepartment maps infrastruktura correctly`() {
        assertEquals(
            "Zarząd Mienia m.st. Warszawy",
            CategoryUtils.categoryToDepartment("Infrastruktura")
        )
    }

    @Test
    fun `categoryToDepartment maps bezpieczenstwo correctly`() {
        assertEquals(
            "Straż Miejska m.st. Warszawy",
            CategoryUtils.categoryToDepartment("Bezpieczeństwo")
        )
    }

    @Test
    fun `categoryToDepartment maps unknown to default`() {
        assertEquals("Urząd m.st. Warszawy", CategoryUtils.categoryToDepartment("Inne"))
    }

    @Test
    fun `categoryToDepartment maps empty string to default`() {
        assertEquals("Urząd m.st. Warszawy", CategoryUtils.categoryToDepartment(""))
    }

    @Test
    fun `categories list contains all expected items`() {
        assertEquals(7, CategoryUtils.categories.size)
        assert(CategoryUtils.categories.contains("Drogi"))
        assert(CategoryUtils.categories.contains("Oświetlenie"))
        assert(CategoryUtils.categories.contains("Zieleń"))
        assert(CategoryUtils.categories.contains("Czystość"))
        assert(CategoryUtils.categories.contains("Infrastruktura"))
        assert(CategoryUtils.categories.contains("Bezpieczeństwo"))
        assert(CategoryUtils.categories.contains("Inne"))
    }
}
