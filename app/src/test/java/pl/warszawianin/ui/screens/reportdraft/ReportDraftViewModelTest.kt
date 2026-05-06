package pl.warszawianin.ui.screens.reportdraft

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import pl.warszawianin.data.ai.GeminiService
import pl.warszawianin.data.local.ReportDao
import pl.warszawianin.data.location.GeocoderService
import pl.warszawianin.data.model.Report
import pl.warszawianin.data.model.ReportStatus
import androidx.lifecycle.SavedStateHandle
import android.content.Context

@OptIn(ExperimentalCoroutinesApi::class)
class ReportDraftViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var reportDao: ReportDao
    private lateinit var geminiService: GeminiService
    private lateinit var geocoderService: GeocoderService
    private lateinit var context: Context

    private val testReport = Report(
        id = 1,
        photoUri = "file:///data/user/0/pl.warszawianin/files/photos/test.jpg",
        latitude = 52.2297,
        longitude = 21.0122,
        address = null,
        category = "Drogi",
        title = "Dziura w chodniku",
        description = "Na chodniku jest dziura",
        status = ReportStatus.DRAFT,
        createdAt = System.currentTimeMillis(),
        sentAt = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        reportDao = mockk(relaxed = true)
        geminiService = mockk(relaxed = true)
        geocoderService = mockk(relaxed = true)
        context = mockk(relaxed = true)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(reportId: String = "1"): ReportDraftViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("reportId" to reportId))
        return ReportDraftViewModel(savedStateHandle, context, reportDao, geminiService, geocoderService)
    }

    @Test
    fun `initial state is Loading`() = runTest {
        every { reportDao.getById(1L) } returns flowOf(testReport)
        val viewModel = createViewModel()
        assertEquals(ReportDraftUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `shows Ready state when report has title`() = runTest {
        every { reportDao.getById(1L) } returns flowOf(testReport)
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Expected Ready but got $state", state is ReportDraftUiState.Ready)
        assertEquals("Dziura w chodniku", (state as ReportDraftUiState.Ready).report.title)
    }

    @Test
    fun `shows Error when report not found`() = runTest {
        every { reportDao.getById(1L) } returns flowOf(null)
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ReportDraftUiState.Error)
    }

    @Test
    fun `markAsSent updates report status to SENT`() = runTest {
        every { reportDao.getById(1L) } returns flowOf(testReport)
        coEvery { reportDao.update(any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.markAsSent()
        advanceUntilIdle()

        coVerify {
            reportDao.update(match { it.status == ReportStatus.SENT && it.sentAt != null })
        }
    }

    @Test
    fun `sets hardcoded address when report has no address`() = runTest {
        every { reportDao.getById(1L) } returns flowOf(testReport.copy(address = null))
        coEvery { reportDao.update(any()) } returns Unit

        val viewModel = createViewModel()
        advanceUntilIdle()

        // Should update with hardcoded demo address
        coVerify { reportDao.update(match { it.address == "ul. Widok 16/1, Warszawa" }) }
    }

    @Test
    fun `skips geocoding when report already has address`() = runTest {
        every { reportDao.getById(1L) } returns flowOf(testReport.copy(address = "ul. Existing"))

        val viewModel = createViewModel()
        advanceUntilIdle()

        coVerify(exactly = 0) { geocoderService.reverseGeocode(any(), any()) }
    }
}
