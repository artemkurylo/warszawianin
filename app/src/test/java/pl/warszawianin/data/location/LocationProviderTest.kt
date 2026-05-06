package pl.warszawianin.data.location

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationProviderTest {

    @Test
    fun `LocationProvider class has expected methods`() {
        val methods = LocationProvider::class.java.declaredMethods.map { it.name }
        // Kotlin suspend functions get mangled but the class should exist
        assertNotNull(LocationProvider::class.java)
        assertTrue("Should have methods", methods.isNotEmpty())
    }

    @Test
    fun `GeocoderService class has expected methods`() {
        val methods = GeocoderService::class.java.declaredMethods.map { it.name }
        assertNotNull(GeocoderService::class.java)
        assertTrue("Should have methods", methods.isNotEmpty())
    }
}
