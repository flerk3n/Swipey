package com.harsh.swipey.data

import android.content.Context

/** SharedPreferences-backed settings toggles. No networking. */
class SettingsPrefs(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun bool(key: String, default: Boolean) = prefs.getBoolean(key, default)
    private fun setBool(key: String, value: Boolean) = prefs.edit().putBoolean(key, value).apply()

    var pushNotifications: Boolean
        get() = bool(KEY_PUSH, true)
        set(v) = setBool(KEY_PUSH, v)

    var dailyReminder: Boolean
        get() = bool(KEY_REMINDER, true)
        set(v) = setBool(KEY_REMINDER, v)

    var reducedMotion: Boolean
        get() = bool(KEY_REDUced_MOTION, false)
        set(v) = setBool(KEY_REDUced_MOTION, v)

    var notionConnected: Boolean
        get() = bool(KEY_NOTION, false)
        set(v) = setBool(KEY_NOTION, v)

    var calendarConnected: Boolean
        get() = bool(KEY_CALENDAR, false)
        set(v) = setBool(KEY_CALENDAR, v)

    private companion object {
        const val PREFS_NAME = "swipey_settings"
        const val KEY_PUSH = "push_notifications"
        const val KEY_REMINDER = "daily_reminder"
        const val KEY_REDUced_MOTION = "reduced_motion"
        const val KEY_NOTION = "notion_connected"
        const val KEY_CALENDAR = "calendar_connected"
    }
}
