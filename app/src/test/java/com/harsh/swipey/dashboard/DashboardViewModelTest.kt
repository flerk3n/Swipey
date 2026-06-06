package com.harsh.swipey.dashboard

import com.harsh.swipey.MainDispatcherRule
import com.harsh.swipey.data.SavedIdeasStore
import com.harsh.swipey.data.model.SeedIdeas
import com.harsh.swipey.ui.screens.dashboard.DashboardUiState
import com.harsh.swipey.ui.screens.dashboard.DashboardViewModel
import com.harsh.swipey.util.FakeIdeaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() = SavedIdeasStore.clear()

    @After
    fun tearDown() = SavedIdeasStore.clear()

    @Test
    fun `initial state reflects current saved store`() {
        SavedIdeasStore.add(SeedIdeas.deck[0])
        SavedIdeasStore.add(SeedIdeas.deck[1])

        val vm = DashboardViewModel(FakeIdeaRepository())
        val state = vm.uiState.value

        assertEquals(2, state.savedCount)
        assertEquals(2, state.streak)
        assertTrue(state.greeting.isNotBlank())
    }

    @Test
    fun `progress is clamped to daily target`() {
        val full = DashboardUiState(
            savedIdeas = SeedIdeas.deck, // 20 > target
            dailyTarget = 5,
        )
        assertEquals(1f, full.progress, 0.0001f)

        val partial = DashboardUiState(
            savedIdeas = SeedIdeas.deck.take(2),
            dailyTarget = 5,
        )
        assertEquals(0.4f, partial.progress, 0.0001f)
    }

    @Test
    fun `empty state has zero progress`() {
        assertEquals(0f, DashboardUiState().progress, 0.0001f)
    }
}
