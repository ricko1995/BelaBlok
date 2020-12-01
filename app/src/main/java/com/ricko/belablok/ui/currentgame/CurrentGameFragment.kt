package com.ricko.belablok.ui.currentgame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ricko.belablok.R
import com.ricko.belablok.adapters.CurrentGameRvAdapter
import com.ricko.belablok.databinding.FragmentCurrentGameBinding
import com.ricko.belablok.ui.gameentry.GameEntrySheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrentGameFragment : Fragment(R.layout.fragment_current_game) {

    private lateinit var binding: FragmentCurrentGameBinding
    private val viewModel: CurrentGameViewModel by viewModels()
    lateinit var currentGameRvAdapter: CurrentGameRvAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCurrentGameBinding.inflate(inflater, container, false)
        return binding.run {
            viewModelVar = viewModel
            lifecycleOwner = viewLifecycleOwner
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabOpenBottomSheet.setOnClickListener {
            currentGameRvAdapter.notifyDataSetChanged()
            GameEntrySheet().show(parentFragmentManager, null)
        }
        initRecyclerView()
        registerObservers()
    }

    private fun initRecyclerView() {
        currentGameRvAdapter = CurrentGameRvAdapter()
        binding.rvCurrentGame.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = currentGameRvAdapter
        }
    }

    private fun registerObservers() {
        viewModel.lastMatch.observe(viewLifecycleOwner) {
            if (it != null) {
                val p1Sum = it.games.map { i -> i.player1Score }.sum() + it.games.map { i -> i.player1Callings }.sum()
                val p2Sum = it.games.map { i -> i.player2Score }.sum() + it.games.map { i -> i.player2Callings }.sum()
                viewModel.player1Sum.value = p1Sum
                viewModel.player2Sum.value = p2Sum
                currentGameRvAdapter.submitListOfGames(it.games.reversed())
            } else {
                val p1Sum = 0
                val p2Sum = 0
                viewModel.player1Sum.value = p1Sum
                viewModel.player2Sum.value = p2Sum
                currentGameRvAdapter.submitListOfGames(emptyList())
            }
            binding.rvCurrentGame.smoothScrollToPosition(0)
        }
    }
}