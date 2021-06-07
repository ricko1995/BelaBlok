package com.ricko.belablok.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.ricko.belablok.R
import com.ricko.belablok.databinding.FragmentSettingsBinding
import com.ricko.belablok.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by activityViewModels()

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.run {
            viewModelSettings = viewModel
            mContext = requireContext()
            mActivity = requireActivity()
            lifecycleOwner = viewLifecycleOwner
            root
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenStarted {
            withTimeout(200){
                while (true){
                    delay(3)
                    binding.dropDownMenu.dismissDropDown()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            dataStore.edit {
                it[Constants.MAX_SCORE_KEY] = viewModel.maxScore.value!!.toInt()
            }
        }
    }
}