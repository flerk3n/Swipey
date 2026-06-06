package com.harsh.swipey.onboarding

import com.harsh.swipey.ui.screens.onboarding.OnboardingViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OnboardingViewModelTest {

    private lateinit var vm: OnboardingViewModel

    @Before
    fun setUp() {
        vm = OnboardingViewModel()
    }

    @Test
    fun `initial state has seed-backed defaults`() {
        val state = vm.uiState.value
        // Defaults pre-select a seeded niche + topic + goal so a demo can tap through.
        assertEquals("tech", state.selectedNicheId)
        assertTrue(state.canContinueNiche)
        assertTrue(state.selectedTopics.isNotEmpty())
        assertTrue(state.canContinueTopics)
        assertTrue(state.canContinueGoal)
    }

    @Test
    fun `selecting niche enables continue and resolves niche`() {
        vm.onNicheSelected("gaming")
        val state = vm.uiState.value
        assertTrue(state.canContinueNiche)
        assertEquals("gaming", state.selectedNiche?.id)
    }

    @Test
    fun `changing niche clears chosen topics`() {
        // The default niche (tech) starts with a topic selected.
        assertTrue(vm.uiState.value.selectedTopics.isNotEmpty())

        vm.onNicheSelected("finance")
        assertTrue(vm.uiState.value.selectedTopics.isEmpty())
    }

    @Test
    fun `toggling a topic adds then removes it`() {
        // Switch to a niche with no pre-selected topics to test a clean toggle cycle.
        vm.onNicheSelected("finance")
        vm.onTopicToggled("Budgeting & saving")
        assertTrue(vm.uiState.value.selectedTopics.contains("Budgeting & saving"))
        assertTrue(vm.uiState.value.canContinueTopics)

        vm.onTopicToggled("Budgeting & saving")
        assertFalse(vm.uiState.value.selectedTopics.contains("Budgeting & saving"))
        assertFalse(vm.uiState.value.canContinueTopics)
    }

    @Test
    fun `selecting goal enables continue`() {
        vm.onGoalSelected("consistent")
        assertTrue(vm.uiState.value.canContinueGoal)
        assertEquals("consistent", vm.uiState.value.selectedGoalId)
    }

    @Test
    fun `re-selecting same niche keeps topics`() {
        vm.onNicheSelected("tech")
        vm.onTopicToggled("Cybersecurity")
        vm.onNicheSelected("tech")
        assertTrue(vm.uiState.value.selectedTopics.contains("Cybersecurity"))
    }
}
