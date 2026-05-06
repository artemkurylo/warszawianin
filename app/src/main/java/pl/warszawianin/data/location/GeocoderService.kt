package pl.warszawianin.data.location

import android.content.Context
import android.location.Geocoder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class GeocoderService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun reverseGeocode(latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale("pl", "PL"))
            if (Build.VERSION.SDK_INT >= 33) {
                reverseGeocodeAsync(geocoder, latitude, longitude)
            } else {
                reverseGeocodeBlocking(geocoder, latitude, longitude)
            }
        } catch (e: Exception) {
            null
        }
    }

    @Suppress("NewApi")
    private suspend fun reverseGeocodeAsync(
        geocoder: Geocoder,
        latitude: Double,
        longitude: Double
    ): String? = suspendCancellableCoroutine { cont ->
        geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
            val address = addresses.firstOrNull()
            cont.resume(formatAddress(address))
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun reverseGeocodeBlocking(
        geocoder: Geocoder,
        latitude: Double,
        longitude: Double
    ): String? = withContext(Dispatchers.IO) {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        val address = addresses?.firstOrNull()
        formatAddress(address)
    }

    private fun formatAddress(address: android.location.Address?): String? {
        if (address == null) return null
        val street = address.thoroughfare
        val number = address.subThoroughfare
        return when {
            street != null && number != null -> "$street $number"
            street != null -> street
            else -> address.getAddressLine(0)
        }
    }
}
