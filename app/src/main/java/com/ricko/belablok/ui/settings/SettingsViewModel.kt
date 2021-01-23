package com.ricko.belablok.ui.settings

import android.content.Context
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.ricko.belablok.R
import com.ricko.belablok.ui.MainActivity
import com.ricko.belablok.ui.masterfragment.MasterFragmentDirections
import dagger.hilt.android.qualifiers.ActivityContext
import java.util.*

class SettingsViewModel @ViewModelInject constructor(
    @ActivityContext private val context1: Context
) : ViewModel(), Observable {

    @Bindable
    val currentLanguage = MutableLiveData(context1.resources.getStringArray(R.array.languages)[0])

    fun setLan() {
//        currentLanguage.postValue(context.resources.getStringArray(R.array.languages)[0])
    }

    fun View.changeLanguage(fragmentActivity: FragmentActivity) {

        val locale = Locale("hr")
        Locale.setDefault(locale)
        resources.configuration.setLocale(locale)
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)
        fragmentActivity.recreate()
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}