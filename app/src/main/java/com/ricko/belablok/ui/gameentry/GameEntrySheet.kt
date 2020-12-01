package com.ricko.belablok.ui.gameentry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ricko.belablok.databinding.BottomSheetGameEntryBinding

class GameEntrySheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetGameEntryBinding

    private val viewModel: GameEntryViewModel by viewModels()

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
        binding.player1Score.isSelected = true
    }
}