package pl.warszawianin.ui.screens.ticketlist

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

sealed class TicketListUiState {
    object Loading : TicketListUiState()
    data class Ready(val reports: List<Report>) : TicketListUiState()
}

@HiltViewModel
class TicketListViewModel @Inject constructor(
    reportDao: ReportDao
) : ViewModel() {

    val uiState: StateFlow<TicketListUiState> = reportDao.getAll()
        .map { reports -> TicketListUiState.Ready(reports) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TicketListUiState.Loading
        )
}
