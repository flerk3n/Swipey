package com.harsh.swipey.data

import android.content.Context

/**
 * SharedPreferences wrapper for onboarding: completion flag plus the chosen niche/goal so
 * the Profile screen can show them. No networking.
 */
class OnboardingPrefs(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isComplete: Boolean
        get() = prefs.getBoolean(KEY_COMPLETE, false)
        set(value) = prefs.edit().putBoolean(KEY_COMPLETE, value).apply()

    var nicheId: String?
        get() = prefs.getString(KEY_NICHE, null)
        set(value) = prefs.edit().putString(KEY_NICHE, value).apply()

    var goalId: String?
        get() = prefs.getString(KEY_GOAL, null)
        set(value) = prefs.edit().putString(KEY_GOAL, value).apply()

    private companion object {
        const val PREFS_NAME = "swipey_onboarding"
        const val KEY_COMPLETE = "onboarding_complete"
        const val KEY_NICHE = "selected_niche"
        const val KEY_GOAL = "selected_goal"
    }
}
