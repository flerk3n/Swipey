package com.harsh.swipey.flashcard

import com.harsh.swipey.MainDispatcherRule
import com.harsh.swipey.data.SavedIdeasStore
import com.harsh.swipey.data.model.SeedIdeas
import com.harsh.swipey.ui.screens.flashcard.FlashcardUiState
import com.harsh.swipey.ui.screens.flashcard.FlashcardViewModel
import com.harsh.swipey.ui.screens.flashcard.SwipeDirection
import com.harsh.swipey.util.FakeIdeaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlashcardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() = SavedIdeasStore.clear()

    @After
    fun tearDown() = SavedIdeasStore.clear()

    private fun newVm() = FlashcardViewModel(FakeIdeaRepository(SeedIdeas.deck))

    private fun content(vm: FlashcardViewModel): FlashcardUiState.Content =
        vm.uiState.value as FlashcardUiState.Content

    @Test
    fun `loads full deck after init`() = runTest {
        val vm = newVm()
        advanceUntilIdle()
        assertTrue(vm.uiState.value is FlashcardUiState.Content)
        assertEquals(SeedIdeas.deck.size, content(vm).cards.size)
    }

    @Test
    fun `dismiss removes the top card without saving`() = runTest {
        val vm = newVm()
        advanceUntilIdle()
        val top = content(vm).cards.first()

        vm.onSwiped(top, SwipeDirection.Left)
        advanceUntilIdle()

        assertEquals(SeedIdeas.deck.size - 1, content(vm).cards.size)
        assertTrue(SavedIdeasStore.saved.value.isEmpty())
    }

    @Test
    fun `save adds the idea to the store`() = runTest {
        val vm = newVm()
        advanceUntilIdle()
        val top = content(vm).cards.first()

        vm.onSwiped(top, SwipeDirection.Up)
        advanceUntilIdle()

        assertTrue(SavedIdeasStore.isSaved(top.id))
        assertEquals(SeedIdeas.deck.size - 1, content(vm).cards.size)
    }

    @Test
    fun `like adds the idea to the store`() = runTest {
        val vm = newVm()
        advanceUntilIdle()
        val top = content(vm).cards.first()

        vm.onSwiped(top, SwipeDirection.Right)
        advanceUntilIdle()

        assertTrue(SavedIdeasStore.isSaved(top.id))
    }

    @Test
    fun `swiping through the whole deck reaches Empty`() = runTest {
        val vm = newVm()
        advanceUntilIdle()
        repeat(SeedIdeas.deck.size) {
            (vm.uiState.value as? FlashcardUiState.Content)?.cards?.firstOrNull()?.let {
                vm.onSwiped(it, SwipeDirection.Left)
            }
        }
        advanceUntilIdle()
        assertEquals(FlashcardUiState.Empty, vm.uiState.value)
    }

    @Test
    fun `reset reloads the deck`() = runTest {
        val vm = newVm()
        advanceUntilIdle()
        repeat(SeedIdeas.deck.size) {
            (vm.uiState.value as? FlashcardUiState.Content)?.cards?.firstOrNull()?.let {
                vm.onSwiped(it, SwipeDirection.Left)
            }
        }
        advanceUntilIdle()
        assertEquals(FlashcardUiState.Empty, vm.uiState.value)

        vm.reset()
        advanceUntilIdle()
        assertEquals(SeedIdeas.deck.size, content(vm).cards.size)
    }

    @Test
    fun `deck load failure surfaces Error state`() = runTest {
        val repo = FakeIdeaRepository(SeedIdeas.deck).apply { failDeck = true }
        val vm = FlashcardViewModel(repo)
        advanceUntilIdle()
        assertTrue(vm.uiState.value is FlashcardUiState.Error)
    }
}
