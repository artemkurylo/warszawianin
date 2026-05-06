package pl.warszawianin.ui.screens.ticketlist

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import pl.warszawianin.data.local.ReportDao
import pl.warszawianin.data.model.Report
import pl.warszawianin.data.model.ReportStatus

@OptIn(ExperimentalCoroutinesApi::class)
class TicketListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var reportDao: ReportDao

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        reportDao = mockk()
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun createReport(title: String = "Test") = Report(
        id = 1,
        photoUri = "file:///test.jpg",
        latitude = 52.0,
        longitude = 21.0,
        address = "ul. Test",
        category = "Drogi",
        title = title,
        description = "Desc",
        status = ReportStatus.DRAFT,
        createdAt = System.currentTimeMillis(),
        sentAt = null
    )

    @Test
    fun `initial state is Loading`() = runTest {
        every { reportDao.getAll() } returns flowOf(emptyList())
        val viewModel = TicketListViewModel(reportDao)

        assertEquals(TicketListUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `emits Ready state with reports from dao`() = runTest {
        val reports = listOf(createReport("Report 1"))
        every { reportDao.getAll() } returns flowOf(reports)

        val viewModel = TicketListViewModel(reportDao)

        viewModel.uiState.test {
            // Skip initial Loading
            val first = awaitItem()
            if (first is TicketListUiState.Loading) {
                val ready = awaitItem() as TicketListUiState.Ready
                assertEquals(1, ready.reports.size)
                assertEquals("Report 1", ready.reports[0].title)
            } else {
                val ready = first as TicketListUiState.Ready
                assertEquals(1, ready.reports.size)
            }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `emits Ready with empty list when no reports`() = runTest {
        every { reportDao.getAll() } returns flowOf(emptyList())

        val viewModel = TicketListViewModel(reportDao)

        viewModel.uiState.test {
            val first = awaitItem()
            if (first is TicketListUiState.Loading) {
                val ready = awaitItem() as TicketListUiState.Ready
                assertTrue(ready.reports.isEmpty())
            } else {
                assertTrue((first as TicketListUiState.Ready).reports.isEmpty())
            }
            cancelAndConsumeRemainingEvents()
        }
    }
}
