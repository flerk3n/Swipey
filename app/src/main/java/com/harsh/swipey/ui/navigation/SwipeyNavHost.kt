package com.harsh.swipey.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.harsh.swipey.data.OnboardingPrefs
import com.harsh.swipey.data.ServiceLocator
import com.harsh.swipey.data.SettingsPrefs
import com.harsh.swipey.ui.screens.MainScreen
import com.harsh.swipey.ui.screens.echo.ExpandedEchoScreen
import com.harsh.swipey.ui.screens.settings.SettingsScreen
import com.harsh.swipey.ui.screens.onboarding.GoalStepScreen
import com.harsh.swipey.ui.screens.onboarding.NicheStepScreen
import com.harsh.swipey.ui.screens.onboarding.OnboardingViewModel
import com.harsh.swipey.ui.screens.onboarding.TopicStepScreen
import com.harsh.swipey.ui.screens.onboarding.WelcomeScreen
import com.harsh.swipey.ui.theme.LocalReducedMotion
import kotlinx.coroutines.launch

/**
 * Root composable hosting the single centralized [NavHost].
 * Onboarding completion is persisted so returning users start on [Route.Home].
 */
@Composable
fun SwipeyApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefs = remember { OnboardingPrefs(context) }
    val reducedMotion = remember { SettingsPrefs(context).reducedMotion }
    val scope = rememberCoroutineScope()

    val onboardingViewModel: OnboardingViewModel = viewModel()

    val startDestination = if (prefs.isComplete) Route.Home.path else Route.Welcome.path

    CompositionLocalProvider(LocalReducedMotion provides reducedMotion) {
        NavHost(navController = navController, startDestination = startDestination) {

        composable(Route.Welcome.path) {
            WelcomeScreen(
                onGetStarted = { navController.navigate(Route.NicheStep.path) },
            )
        }

        composable(Route.NicheStep.path) {
            val state by onboardingViewModel.uiState.collectAsStateWithLifecycle()
            NicheStepScreen(
                selectedNicheId = state.selectedNicheId,
                onNicheSelected = onboardingViewModel::onNicheSelected,
                canContinue = state.canContinueNiche,
                onContinue = { navController.navigate(Route.TopicStep.path) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Route.TopicStep.path) {
            val state by onboardingViewModel.uiState.collectAsStateWithLifecycle()
            TopicStepScreen(
                niche = state.selectedNiche,
                selectedTopics = state.selectedTopics,
                onTopicToggled = onboardingViewModel::onTopicToggled,
                canContinue = state.canContinueTopics,
                onContinue = { navController.navigate(Route.GoalStep.path) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Route.GoalStep.path) {
            val state by onboardingViewModel.uiState.collectAsStateWithLifecycle()
            GoalStepScreen(
                selectedGoalId = state.selectedGoalId,
                onGoalSelected = onboardingViewModel::onGoalSelected,
                canContinue = state.canContinueGoal,
                onFinish = {
                    prefs.isComplete = true
                    prefs.nicheId = state.selectedNicheId
                    prefs.goalId = state.selectedGoalId
                    // Best-effort sync to the backend (source of truth across reinstalls).
                    scope.launch {
                        runCatching {
                            ServiceLocator.profileRepository.completeOnboarding(
                                nicheId = state.selectedNicheId,
                                goalId = state.selectedGoalId,
                            )
                        }
                    }
                    navController.navigate(Route.Home.path) {
                        popUpTo(Route.Welcome.path) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Route.Home.path) {
            MainScreen(
                onOpenEcho = { ideaId ->
                    navController.navigate(Route.Echo.buildPath(ideaId))
                },
                onOpenSettings = { navController.navigate(Route.Settings.path) },
            )
        }

        // Expanded Echo — ideaId passed as a path argument.
        composable(
            route = Route.Echo.path,
            arguments = listOf(navArgument("ideaId") { type = NavType.StringType }),
        ) {
            ExpandedEchoScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Route.Settings.path) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        }
    }
}
