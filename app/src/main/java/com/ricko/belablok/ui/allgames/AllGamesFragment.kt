package com.ricko.belablok.ui.allgames

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ricko.belablok.R
import com.ricko.belablok.databinding.FragmentAllGamesBinding

class AllGamesFragment : Fragment(R.layout.fragment_all_games) {

    private lateinit var binding: FragmentAllGamesBinding
    private val viewModel: AllGamesViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAllGamesBinding.inflate(inflater, container, false)
        return binding.run {
            viewModelVar = viewModel
            lifecycleOwner = viewLifecycleOwner
            root
        }
    }
}