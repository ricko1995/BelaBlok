package com.ricko.belablok.ui.currentgame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ricko.belablok.R
import com.ricko.belablok.adapters.CurrentGameRvAdapter
import com.ricko.belablok.databinding.FragmentCurrentGameBinding
import com.ricko.belablok.ui.gameentry.GameEntrySheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CurrentGameFragment : Fragment(R.layout.fragment_current_game) {

    private lateinit var binding: FragmentCurrentGameBinding
    private val viewModel: CurrentGameViewModel by viewModels()
    lateinit var currentGameRvAdapter: CurrentGameRvAdapter

    private var bottomSheet: GameEntrySheet? = null

    override fun onDestroy() {
        super.onDestroy()
        bottomSheet = null
    }

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
            lifecycleScope.launch(Dispatchers.IO) {
                if (viewModel.player1Sum.value!! > 1000 ||
                    viewModel.player2Sum.value!! > 1000 ||
                    viewModel.lastMatch.value == null
                ) viewModel.createNewMatch()

                bottomSheet = GameEntrySheet() { bottomSheet = null } //Prevent Memory Leak

                withContext(Dispatchers.Main) { bottomSheet?.show(childFragmentManager, bottomSheet?.tag) }
            }
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
//            binding.p1score.isSelected = false
//            binding.p2score.isSelected = false
            if (it != null) {
                val p1Sum = it.games.map { i -> i.player1Score }.sum() + it.games.map { i -> i.player1Callings }.sum()
                val p2Sum = it.games.map { i -> i.player2Score }.sum() + it.games.map { i -> i.player2Callings }.sum()
                viewModel.player1Sum.value = p1Sum
                viewModel.player2Sum.value = p2Sum
                currentGameRvAdapter.submitListOfGames(it.games.reversed())
                if (p1Sum > 1000 && p1Sum > p2Sum) {
                    declareWinner(viewModel.player1Name.value)
//                    binding.p1score.isSelected = true
                }
                if (p2Sum > 1000 && p2Sum > p1Sum) {
                    declareWinner(viewModel.player2Name.value)
//                    binding.p2score.isSelected = true
                }
            } else {
                viewModel.player1Sum.value = 0
                viewModel.player2Sum.value = 0
                currentGameRvAdapter.submitListOfGames(emptyList())
            }
            binding.rvCurrentGame.smoothScrollToPosition(0)
        }
    }

    private fun declareWinner(playerName: String?) {
        bottomSheet?.dismiss()
        bottomSheet = null
        Toast.makeText(requireContext(), "$playerName su pobjedili!!!", Toast.LENGTH_LONG).show()
    }
}