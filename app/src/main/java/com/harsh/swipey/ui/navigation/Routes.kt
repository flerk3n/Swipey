package com.harsh.swipey.ui.navigation

/** Strongly-typed navigation destinations. */
sealed class Route(val path: String) {
    data object Welcome : Route("welcome")
    data object NicheStep : Route("onboarding/niche")
    data object TopicStep : Route("onboarding/topic")
    data object GoalStep : Route("onboarding/goal")
    data object Home : Route("home")

    /** Expanded Echo — takes an `ideaId` path segment. */
    data object Echo : Route("echo/{ideaId}") {
        fun buildPath(ideaId: String) = "echo/$ideaId"
    }

    data object Settings : Route("settings")
}
