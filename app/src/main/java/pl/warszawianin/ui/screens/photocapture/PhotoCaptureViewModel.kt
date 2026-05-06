package pl.warszawianin.ui.screens.photocapture

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.warszawianin.data.local.ReportDao
import pl.warszawianin.data.location.LocationProvider
import pl.warszawianin.data.mock.MockNeighbourData
import pl.warszawianin.data.model.Report
import pl.warszawianin.data.model.ReportStatus
import pl.warszawianin.util.DemoPhotoUtils
import java.io.File
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject

sealed class PhotoCaptureState {
    object Idle : PhotoCaptureState()
    object Saving : PhotoCaptureState()
    data class Done(val reportId: Long) : PhotoCaptureState()
    data class Error(val message: String) : PhotoCaptureState()
}

@HiltViewModel
class PhotoCaptureViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val reportDao: ReportDao,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _state = MutableStateFlow<PhotoCaptureState>(PhotoCaptureState.Idle)
    val state: StateFlow<PhotoCaptureState> = _state

    private val photosDir: File
        get() = File(context.filesDir, "photos").also { it.mkdirs() }

    fun createPhotoUri(): Uri {
        val file = File(photosDir, "${UUID.randomUUID()}.jpg")
        return androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun getPhotoFileFromUri(uri: Uri): File? {
        // For camera URIs that point to our files dir
        return uri.path?.let { File(it) }?.takeIf { it.exists() }
    }

    fun onPhotoTaken(uri: Uri) {
        viewModelScope.launch {
            _state.value = PhotoCaptureState.Saving
            try {
                // The photo is already saved at the URI we created
                val path = uri.path ?: throw IllegalStateException("Invalid URI")
                val file = File(path)
                if (!file.exists()) throw IllegalStateException("Photo file not found")

                // Get location (non-blocking, 5s timeout)
                val location = locationProvider.getCurrentLocation()

                val report = Report(
                    photoUri = file.toUri().toString(),
                    latitude = location?.latitude,
                    longitude = location?.longitude,
                    address = null,
                    category = "",
                    title = "",
                    description = "",
                    status = ReportStatus.DRAFT,
                    createdAt = System.currentTimeMillis(),
                    sentAt = null
                )
                val id = reportDao.insert(report)
                _state.value = PhotoCaptureState.Done(id)
            } catch (e: Exception) {
                _state.value = PhotoCaptureState.Error("Nie udało się zapisać zdjęcia")
            }
        }
    }

    fun onGalleryPhotoPicked(uri: Uri) {
        viewModelScope.launch {
            _state.value = PhotoCaptureState.Saving
            try {
                // Copy gallery photo to app storage
                val destFile = File(photosDir, "${UUID.randomUUID()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input: InputStream ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                } ?: throw IllegalStateException("Cannot read selected photo")

                val location = locationProvider.getCurrentLocation()

                val report = Report(
                    photoUri = destFile.toUri().toString(),
                    latitude = location?.latitude,
                    longitude = location?.longitude,
                    address = null,
                    category = "",
                    title = "",
                    description = "",
                    status = ReportStatus.DRAFT,
                    createdAt = System.currentTimeMillis(),
                    sentAt = null
                )
                val id = reportDao.insert(report)
                _state.value = PhotoCaptureState.Done(id)
            } catch (e: Exception) {
                _state.value = PhotoCaptureState.Error("Nie udało się zapisać zdjęcia")
            }
        }
    }

    fun useDemoPhoto() {
        viewModelScope.launch {
            _state.value = PhotoCaptureState.Saving
            try {
                val demoFile = DemoPhotoUtils.copyDemoPhotoToStorage(
                    context, MockNeighbourData.PRIMARY_DEMO_PHOTO
                )

                val location = locationProvider.getCurrentLocation()

                val report = Report(
                    photoUri = demoFile.toUri().toString(),
                    latitude = location?.latitude ?: 52.2297,
                    longitude = location?.longitude ?: 21.0122,
                    address = "ul. Marszałkowska 12",
                    category = "",
                    title = "",
                    description = "",
                    status = ReportStatus.DRAFT,
                    createdAt = System.currentTimeMillis(),
                    sentAt = null
                )
                val id = reportDao.insert(report)
                _state.value = PhotoCaptureState.Done(id)
            } catch (e: Exception) {
                _state.value = PhotoCaptureState.Error("Nie udało się załadować zdjęcia demo")
            }
        }
    }
}
