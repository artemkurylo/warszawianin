package pl.warszawianin.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pl.warszawianin.data.model.Report
import pl.warszawianin.data.model.ReportStatus

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ReportDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: ReportDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.reportDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    private fun createReport(
        title: String = "Test report",
        status: ReportStatus = ReportStatus.DRAFT,
        createdAt: Long = System.currentTimeMillis()
    ) = Report(
        photoUri = "file:///test/photo.jpg",
        latitude = 52.2297,
        longitude = 21.0122,
        address = "ul. Testowa 1",
        category = "Drogi",
        title = title,
        description = "Test description",
        status = status,
        createdAt = createdAt,
        sentAt = null
    )

    @Test
    fun `insert returns generated ID`() = runTest {
        val id = dao.insert(createReport())
        assert(id > 0)
    }

    @Test
    fun `getAll returns reports ordered by createdAt DESC`() = runTest {
        dao.insert(createReport(title = "Old", createdAt = 1000L))
        dao.insert(createReport(title = "New", createdAt = 2000L))

        val reports = dao.getAll().first()
        assertEquals(2, reports.size)
        assertEquals("New", reports[0].title)
        assertEquals("Old", reports[1].title)
    }

    @Test
    fun `getById returns correct report`() = runTest {
        val id = dao.insert(createReport(title = "Find me"))
        val report = dao.getById(id).first()
        assertNotNull(report)
        assertEquals("Find me", report!!.title)
    }

    @Test
    fun `getById returns null for non-existent ID`() = runTest {
        val report = dao.getById(999L).first()
        assertNull(report)
    }

    @Test
    fun `update modifies the report`() = runTest {
        val id = dao.insert(createReport(title = "Original"))
        val report = dao.getById(id).first()!!
        dao.update(report.copy(title = "Updated", status = ReportStatus.SENT))

        val updated = dao.getById(id).first()!!
        assertEquals("Updated", updated.title)
        assertEquals(ReportStatus.SENT, updated.status)
    }

    @Test
    fun `delete removes the report`() = runTest {
        val id = dao.insert(createReport())
        val report = dao.getById(id).first()!!
        dao.delete(report)

        val deleted = dao.getById(id).first()
        assertNull(deleted)
    }

    @Test
    fun `getAll returns empty list when no reports`() = runTest {
        val reports = dao.getAll().first()
        assertEquals(0, reports.size)
    }

    @Test
    fun `insert preserves all fields`() = runTest {
        val original = createReport().copy(
            latitude = 52.123,
            longitude = 21.456,
            address = "al. Jerozolimskie 100",
            category = "Oświetlenie",
            sentAt = 999L
        )
        val id = dao.insert(original)
        val loaded = dao.getById(id).first()!!

        assertEquals(original.photoUri, loaded.photoUri)
        assertEquals(original.latitude, loaded.latitude)
        assertEquals(original.longitude, loaded.longitude)
        assertEquals(original.address, loaded.address)
        assertEquals(original.category, loaded.category)
        assertEquals(original.title, loaded.title)
        assertEquals(original.description, loaded.description)
        assertEquals(original.status, loaded.status)
        assertEquals(original.sentAt, loaded.sentAt)
    }
}
