package com.ricko.belablok.ui.gameentry

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ricko.belablok.databinding.BottomSheetGameEntryBinding
import dagger.hilt.android.AndroidEntryPoint
import android.view.ViewTreeObserver.OnGlobalLayoutListener


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

        view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener { // For extending BottomSheet to full height in landscape
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val bottomSheet = dialog!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED

                behavior.addBottomSheetCallback(object : BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED && requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                            dismiss()
                    }
                    override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
                })
            }
        })

        viewModel.lastMatch.observe(viewLifecycleOwner) {} // For live updates
    }
}