package com.harsh.swipey.data

/**
 * Maps the app's onboarding niche ids to the backend's seed niches. Only the niches we ship
 * seed data for are mapped; anything else returns null so the deck falls back to showing all
 * ideas (the demo never goes empty).
 */
object NicheMapping {

    private val onboardingToBackend = mapOf(
        "tech" to "tech_reviews",
        "gaming" to "gaming",
        "fashion" to "fashion",
    )

    /** Backend niche for an onboarding niche id, or null if there's no seed for it. */
    fun backendNiche(onboardingNicheId: String?): String? =
        onboardingToBackend[onboardingNicheId]
}
