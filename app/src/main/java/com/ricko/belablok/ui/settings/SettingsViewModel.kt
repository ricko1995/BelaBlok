package com.ricko.belablok.ui.settings

import android.content.Context
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ricko.belablok.util.Constants.LANGUAGE_KEY
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsViewModel @ViewModelInject constructor(
    @ActivityContext private val context1: Context,
    private val dataStore: DataStore<Preferences>,
) : ViewModel(), Observable {

    @Bindable
    val currentLanguage = MutableLiveData<String?>()


    init {
        viewModelScope.launch {
            dataStore.data.map { it[LANGUAGE_KEY] ?: "en" }.collect {
                currentLanguage.value = when (it) {
                    "en" -> "English"
                    "hr" -> "Hrvatski"
                    "fr" -> "Français"
                    "de" -> "Deutsche"
                    else -> null
                }
            }
        }
        currentLanguage.observeForever {
            viewModelScope.launch {
                if (it == "Croatian" || it == "Hrvatski") {
                    dataStore.edit { pref ->
                        pref[LANGUAGE_KEY] = "hr"
                    }
                } else if (it == "English" || it == "Engleski") {
                    dataStore.edit { pref ->
                        pref[LANGUAGE_KEY] = "en"
                    }
                } else if (it == "French" || it == "Français") {
                    dataStore.edit { pref ->
                        pref[LANGUAGE_KEY] = "fr"
                    }
                } else if (it == "German" || it == "Deutsche") {
                    dataStore.edit { pref ->
                        pref[LANGUAGE_KEY] = "de"
                    }
                }
            }
        }
    }

    fun restoreDefaultSettings() {
        currentLanguage.value = "English"
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}