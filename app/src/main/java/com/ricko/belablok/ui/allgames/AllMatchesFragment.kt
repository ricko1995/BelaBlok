package com.ricko.belablok.ui.allgames

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ricko.belablok.R
import com.ricko.belablok.adapters.AllMatchesRvAdapter
import com.ricko.belablok.databinding.FragmentAllMatchesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllMatchesFragment : Fragment(R.layout.fragment_all_matches) {

    private lateinit var binding: FragmentAllMatchesBinding
    private val viewModel: AllMatchesViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAllMatchesBinding.inflate(inflater, container, false)
        return binding.run {
            viewModelVar = viewModel
            lifecycleOwner = viewLifecycleOwner
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        lifecycleScope.launch {
            val rvAdapter = AllMatchesRvAdapter(viewModel.matchesWithGames().sortedByDescending { it.match.creationTime })
            binding.rvAllMatches.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = rvAdapter
            }
        }
    }
}