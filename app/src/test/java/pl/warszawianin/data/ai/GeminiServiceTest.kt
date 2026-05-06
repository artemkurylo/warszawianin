package pl.warszawianin.data.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class GeminiServiceParsingTest {

    @Test
    fun `AiDraft holds correct values`() {
        val draft = AiDraft(
            category = "drogi",
            title = "Dziura w chodniku",
            description = "Na chodniku jest dziura"
        )
        assertEquals("drogi", draft.category)
        assertEquals("Dziura w chodniku", draft.title)
        assertEquals("Na chodniku jest dziura", draft.description)
    }

    @Test
    fun `AiDraft equality works`() {
        val draft1 = AiDraft("drogi", "Title", "Desc")
        val draft2 = AiDraft("drogi", "Title", "Desc")
        assertEquals(draft1, draft2)
    }

    @Test
    fun `AiDraft copy works`() {
        val original = AiDraft("drogi", "Title", "Desc")
        val modified = original.copy(category = "zieleń")
        assertEquals("zieleń", modified.category)
        assertEquals("Title", modified.title)
    }

    @Test
    fun `AiDraft inequality for different categories`() {
        val draft1 = AiDraft("drogi", "Title", "Desc")
        val draft2 = AiDraft("zieleń", "Title", "Desc")
        assertNotEquals(draft1, draft2)
    }

    @Test
    fun `markdown fence stripping logic works`() {
        val text = "```json\n{\"key\": \"value\"}\n```"
        val cleaned = text.replace("```json", "").replace("```", "").trim()
        assertEquals("{\"key\": \"value\"}", cleaned)
    }

    @Test
    fun `markdown fence stripping handles no fences`() {
        val text = "{\"key\": \"value\"}"
        val cleaned = text.replace("```json", "").replace("```", "").trim()
        assertEquals("{\"key\": \"value\"}", cleaned)
    }
}
