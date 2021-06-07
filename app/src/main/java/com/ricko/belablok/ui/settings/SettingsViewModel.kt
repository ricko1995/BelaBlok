package com.ricko.belablok.ui.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ToggleButton
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.android.material.switchmaterial.SwitchMaterial
import com.ricko.belablok.ui.masterfragment.MasterFragmentDirections
import com.ricko.belablok.util.Constants.LANGUAGE_KEY
import com.ricko.belablok.util.Constants.MAX_SCORE_KEY
import com.ricko.belablok.util.Constants.THEME_KEY
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsViewModel @ViewModelInject constructor(
    @ActivityContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
) : ViewModel(), Observable {

    val isDarkThemeOn = MutableLiveData(true)

    @Bindable
    val currentLanguage = MutableLiveData("")

    @Bindable
    val maxScore = MutableLiveData("1000")


    @Bindable
    val currPos = MutableLiveData(-1)

    init {
        currPos.observeForever{
            println("/////////////////////////////////////////////////////$it")
        }
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
        viewModelScope.launch {
            dataStore.data.map { it[MAX_SCORE_KEY] ?: 1000 }.collect { mScore ->
                maxScore.value = mScore.toString()
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
                } else if (it == "French" || it == "Francuski" || it == "Français") {
                    dataStore.edit { pref ->
                        pref[LANGUAGE_KEY] = "fr"
                    }
                } else if (it == "German" || it == "Njemački" || it == "Deutsche") {
                    dataStore.edit { pref ->
                        pref[LANGUAGE_KEY] = "de"
                    }
                }
            }
        }
    }

    fun View.restoreDefaultSettings() {
        currentLanguage.value = "English"
    }

    fun View.themeChange() {
        if (isDarkThemeOn.value!!) viewModelScope.launch {
            dataStore.edit {
                it[THEME_KEY] = "dark"
            }
            findNavController().popBackStack()
            findNavController().navigate(MasterFragmentDirections.actionMasterFragmentToSettingsFragment())
        } else viewModelScope.launch {
            dataStore.edit {
                it[THEME_KEY] = "light"
            }
            findNavController().popBackStack()
            findNavController().navigate(MasterFragmentDirections.actionMasterFragmentToSettingsFragment())
        }
    }

    private fun rateApp() {
        try {
            val rateIntent = rateIntentForUrl("market://details")
            context.startActivity(rateIntent)
        } catch (e: ActivityNotFoundException) {
            val rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details")
            context.startActivity(rateIntent)
        }
    }

    private fun rateIntentForUrl(url: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(java.lang.String.format("%s?id=%s", url, context.packageName)))
        val flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        intent.addFlags(flags)
        return intent
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}