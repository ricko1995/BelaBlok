package com.ricko.belablok.ui.gameentry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ricko.belablok.databinding.BottomSheetGameEntryBinding
import com.ricko.belablok.ui.currentgame.CurrentGameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameEntrySheet(val onCloseCallback: () -> Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetGameEntryBinding

    private val viewModel: GameEntryViewModel by viewModels()

    override fun onDestroy() {
        super.onDestroy()
        onCloseCallback()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomSheetGameEntryBinding.inflate(inflater, container, false)
        return binding.run {
            viewModelVar = viewModel
            lifecycleOwner = viewLifecycleOwner
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.lastMatch.observe(viewLifecycleOwner) {}
    }
}