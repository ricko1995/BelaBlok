package com.ricko.belablok.ui.currentgame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ricko.belablok.R
import com.ricko.belablok.databinding.FragmentCurrentGameBinding
import com.ricko.belablok.ui.gameentry.GameEntrySheet

class CurrentGameFragment : Fragment(R.layout.fragment_current_game) {

    private lateinit var binding: FragmentCurrentGameBinding
    private val viewModel: CurrentGameViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCurrentGameBinding.inflate(inflater, container, false)
        return binding.run {
            viewModelVar = viewModel
            lifecycleOwner = viewLifecycleOwner
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}