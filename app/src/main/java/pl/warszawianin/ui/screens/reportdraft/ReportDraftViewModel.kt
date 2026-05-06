package pl.warszawianin.ui.screens.reportdraft

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import pl.warszawianin.data.ai.GeminiService
import pl.warszawianin.data.local.ReportDao
import pl.warszawianin.data.location.GeocoderService
import pl.warszawianin.data.model.Report
import pl.warszawianin.data.model.ReportStatus
import java.io.File
import javax.inject.Inject

sealed class ReportDraftUiState {
    object Loading : ReportDraftUiState()
    data class Ready(val report: Report) : ReportDraftUiState()
    data class Error(val message: String) : ReportDraftUiState()
}

@HiltViewModel
class ReportDraftViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val reportDao: ReportDao,
    private val geminiService: GeminiService,
    private val geocoderService: GeocoderService
) : ViewModel() {

    private val reportId: Long = savedStateHandle.get<String>("reportId")?.toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow<ReportDraftUiState>(ReportDraftUiState.Loading)
    val uiState: StateFlow<ReportDraftUiState> = _uiState

    private val _isRefining = MutableStateFlow(false)
    val isRefining: StateFlow<Boolean> = _isRefining

    init {
        loadReport()
    }

    private fun loadReport() {
        viewModelScope.launch {
            try {
                val report = reportDao.getById(reportId).firstOrNull()
                if (report == null) {
                    _uiState.value = ReportDraftUiState.Error("Zgłoszenie nie znalezione")
                    return@launch
                }

                // Start geocoding in parallel if needed
                // In demo mode, always use hardcoded address
                if (report.address == null) {
                    val updatedWithAddress = report.copy(address = "ul. Widok 16/1, Warszawa")
                    reportDao.update(updatedWithAddress)
                    if (updatedWithAddress.title.isBlank()) {
                        analyzeWithGemini(updatedWithAddress)
                    } else {
                        _uiState.value = ReportDraftUiState.Ready(updatedWithAddress)
                    }
                } else if (report.title.isBlank()) {
                    analyzeWithGemini(report)
                } else {
                    _uiState.value = ReportDraftUiState.Ready(report)
                }
            } catch (e: Exception) {
                _uiState.value = ReportDraftUiState.Error("Coś poszło nie tak. Spróbuj ponownie.")
            }
        }
    }

    private suspend fun geocodeAddress(report: Report) {
        val address = geocoderService.reverseGeocode(report.latitude!!, report.longitude!!)
        if (address != null) {
            val updated = report.copy(address = address)
            reportDao.update(updated)
            // Update UI if currently showing this report
            val current = _uiState.value
            if (current is ReportDraftUiState.Ready && current.report.id == report.id) {
                _uiState.value = ReportDraftUiState.Ready(current.report.copy(address = address))
            }
        }
    }

    private suspend fun analyzeWithGemini(report: Report) {
        _uiState.value = ReportDraftUiState.Loading
        try {
            val photoFile = getPhotoFile(report.photoUri)
                ?: throw IllegalStateException("Photo file not found")

            val draft = geminiService.analyzePhoto(
                photoFile = photoFile,
                latitude = report.latitude,
                longitude = report.longitude,
                address = report.address
            )

            val updated = report.copy(
                category = draft.category,
                title = draft.title,
                description = draft.description
            )
            reportDao.update(updated)
            _uiState.value = ReportDraftUiState.Ready(updated)
        } catch (e: Exception) {
            val msg = when {
                e.message?.contains("quota", ignoreCase = true) == true ->
                    "Zbyt wiele zapytań. Spróbuj za chwilę."
                e.message?.contains("network", ignoreCase = true) == true ||
                        e.message?.contains("connect", ignoreCase = true) == true ->
                    "Brak połączenia z internetem. Sprawdź sieć i spróbuj ponownie."
                else -> "Coś poszło nie tak. Spróbuj ponownie."
            }
            _uiState.value = ReportDraftUiState.Error(msg)
        }
    }

    fun retry() {
        viewModelScope.launch {
            val report = reportDao.getById(reportId).firstOrNull() ?: return@launch
            analyzeWithGemini(report)
        }
    }

    fun refine(feedback: String) {
        viewModelScope.launch {
            val current = (_uiState.value as? ReportDraftUiState.Ready)?.report ?: return@launch
            _isRefining.value = true
            try {
                val photoFile = getPhotoFile(current.photoUri)
                    ?: throw IllegalStateException("Photo not found")

                val draft = geminiService.refine(
                    photoFile = photoFile,
                    currentCategory = current.category,
                    currentTitle = current.title,
                    currentDescription = current.description,
                    userFeedback = feedback,
                    latitude = current.latitude,
                    longitude = current.longitude,
                    address = current.address
                )

                val updated = current.copy(
                    category = draft.category,
                    title = draft.title,
                    description = draft.description
                )
                reportDao.update(updated)
                _uiState.value = ReportDraftUiState.Ready(updated)
            } catch (e: Exception) {
                // Keep current state, just stop refining
            } finally {
                _isRefining.value = false
            }
        }
    }

    fun markAsSent() {
        viewModelScope.launch {
            val current = (_uiState.value as? ReportDraftUiState.Ready)?.report ?: return@launch
            val updated = current.copy(
                status = ReportStatus.SENT,
                sentAt = System.currentTimeMillis()
            )
            reportDao.update(updated)
            _uiState.value = ReportDraftUiState.Ready(updated)
        }
    }

    private fun getPhotoFile(photoUri: String): File? {
        val uri = Uri.parse(photoUri)
        val path = uri.path ?: return null
        val file = File(path)
        return if (file.exists()) file else null
    }
}
