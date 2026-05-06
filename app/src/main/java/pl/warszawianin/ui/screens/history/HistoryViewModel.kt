package pl.warszawianin.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pl.warszawianin.data.local.ReportDao
import pl.warszawianin.data.model.Report
import javax.inject.Inject

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    data class Ready(val reports: List<Report>) : HistoryUiState()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    reportDao: ReportDao
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = reportDao.getAll()
        .map { reports -> HistoryUiState.Ready(reports.filter { it.status == pl.warszawianin.data.model.ReportStatus.SENT }) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryUiState.Loading
        )
}
