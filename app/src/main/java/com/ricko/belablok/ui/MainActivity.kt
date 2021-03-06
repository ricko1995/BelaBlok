package com.ricko.belablok.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.ricko.belablok.R
import com.ricko.belablok.ui.settings.SettingsViewModel
import com.ricko.belablok.util.Constants.LANGUAGE_KEY
import com.ricko.belablok.util.Constants.THEME_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    override fun onCreate(savedInstanceState: Bundle?) {
        setMyTheme()
        setLanguage()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        setupActionBarWithNavController(navHostFragment.navController)
    }

    private fun setLanguage() = lifecycleScope.launchWhenStarted {
        dataStore.data.map {
            it[LANGUAGE_KEY] ?: ""
        }.collect {
            if (it == resources.configuration.locale.language) return@collect
            val locale = Locale(it)
            Locale.setDefault(locale)
            resources.configuration.setLocale(locale)
            resources.updateConfiguration(resources.configuration, resources.displayMetrics)
            recreate()
        }
    }

    private fun setMyTheme() = lifecycleScope.launchWhenCreated {
        dataStore.data.map {
            it[THEME_KEY] ?: "dark"
        }.collect {
            if (it == "dark") {
                viewModel.isDarkThemeOn.value = true
                setTheme(R.style.Theme_BelaBlok_Dark)
            } else if (it == "light") {
                viewModel.isDarkThemeOn.value = false
                setTheme(R.style.Theme_BelaBlok)
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.navHostFragment).navigateUp() || super.onSupportNavigateUp()
    }
}