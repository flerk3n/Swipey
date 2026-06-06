package com.harsh.swipey.data

import com.harsh.swipey.data.model.IdeaModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SavedIdeasStoreTest {

    private fun idea(id: String) = IdeaModel(
        id = id,
        category = "TEST",
        title = "Title $id",
        description = "Description $id",
    )

    @Before
    fun setUp() = SavedIdeasStore.clear()

    @After
    fun tearDown() = SavedIdeasStore.clear()

    @Test
    fun `add inserts idea`() {
        SavedIdeasStore.add(idea("a"))
        assertEquals(1, SavedIdeasStore.saved.value.size)
        assertTrue(SavedIdeasStore.isSaved("a"))
    }

    @Test
    fun `add is deduplicated by id`() {
        SavedIdeasStore.add(idea("a"))
        SavedIdeasStore.add(idea("a"))
        assertEquals(1, SavedIdeasStore.saved.value.size)
    }

    @Test
    fun `add prepends most-recent first`() {
        SavedIdeasStore.add(idea("a"))
        SavedIdeasStore.add(idea("b"))
        assertEquals(listOf("b", "a"), SavedIdeasStore.saved.value.map { it.id })
    }

    @Test
    fun `remove deletes by id`() {
        SavedIdeasStore.add(idea("a"))
        SavedIdeasStore.add(idea("b"))
        SavedIdeasStore.remove("a")
        assertFalse(SavedIdeasStore.isSaved("a"))
        assertTrue(SavedIdeasStore.isSaved("b"))
    }

    @Test
    fun `clear empties the store`() {
        SavedIdeasStore.add(idea("a"))
        SavedIdeasStore.clear()
        assertTrue(SavedIdeasStore.saved.value.isEmpty())
    }
}
