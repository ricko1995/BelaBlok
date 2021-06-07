package com.ricko.belablok.util

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    val LANGUAGE_KEY = stringPreferencesKey("LANGUAGE_KEY")
    val THEME_KEY = stringPreferencesKey("THEME_KEY")
    val MAX_SCORE_KEY = intPreferencesKey("MAX_SCORE_KEY")
}