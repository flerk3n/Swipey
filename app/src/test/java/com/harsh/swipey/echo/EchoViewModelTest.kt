package com.harsh.swipey.echo

import androidx.lifecycle.SavedStateHandle
import com.harsh.swipey.MainDispatcherRule
import com.harsh.swipey.data.SavedIdeasStore
import com.harsh.swipey.data.model.SeedIdeas
import com.harsh.swipey.ui.screens.echo.EchoViewModel
import com.harsh.swipey.util.FakeIdeaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EchoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val seed = SeedIdeas.deck.first()

    @Before
    fun setUp() = SavedIdeasStore.clear()

    @After
    fun tearDown() = SavedIdeasStore.clear()

    private fun vmFor(id: String): EchoViewModel {
        // EchoViewModel resolves the idea from the saved cache; seed it when the id is known.
        SeedIdeas.deck.firstOrNull { it.id == id }?.let { SavedIdeasStore.add(it) }
        return EchoViewModel(
            SavedStateHandle(mapOf("ideaId" to id)),
            FakeIdeaRepository(SeedIdeas.deck),
        )
    }

    @Test
    fun `resolves idea from seed deck`() {
        val vm = vmFor(seed.id)
        assertEquals(seed.id, vm.uiState.value.idea?.id)
        assertEquals(seed.tags, vm.uiState.value.tags)
    }

    @Test
    fun `unknown id yields null idea`() {
        val vm = vmFor("nope")
        assertEquals(null, vm.uiState.value.idea)
    }

    @Test
    fun `generate types out the full description`() = runTest {
        val vm = vmFor(seed.id)
        vm.onGenerate()
        advanceUntilIdle()
        assertEquals(seed.description, vm.uiState.value.displayedGenerated)
        assertFalse(vm.uiState.value.isGenerating)
    }

    @Test
    fun `adding a tag prepends hash and dedupes`() {
        val vm = vmFor(seed.id)
        val before = vm.uiState.value.tags.size

        vm.onTagAdded("Growth")
        assertTrue(vm.uiState.value.tags.contains("#Growth"))
        assertEquals(before + 1, vm.uiState.value.tags.size)

        vm.onTagAdded("#Growth")
        assertEquals(before + 1, vm.uiState.value.tags.size)
    }

    @Test
    fun `toggling an existing tag removes it`() {
        val vm = vmFor(seed.id)
        val tag = seed.tags.first()
        vm.onTagToggled(tag)
        assertFalse(vm.uiState.value.tags.contains(tag))
    }

    @Test
    fun `blank tag is ignored`() {
        val vm = vmFor(seed.id)
        val before = vm.uiState.value.tags.size
        vm.onTagAdded("   ")
        assertEquals(before, vm.uiState.value.tags.size)
    }
}
