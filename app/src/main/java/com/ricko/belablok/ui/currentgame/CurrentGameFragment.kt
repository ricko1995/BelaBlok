package com.ricko.belablok.ui.currentgame

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ricko.belablok.R
import com.ricko.belablok.adapters.CurrentGameRvAdapter
import com.ricko.belablok.databinding.FragmentCurrentGameBinding
import com.ricko.belablok.db.Game
import com.ricko.belablok.ui.gameentry.GameEntrySheet
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.createBalloon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CurrentGameFragment : Fragment(R.layout.fragment_current_game), CurrentGameRvAdapter.OnItemClickListener {

    private lateinit var binding: FragmentCurrentGameBinding
    private val viewModel: CurrentGameViewModel by viewModels()
    lateinit var currentGameRvAdapter: CurrentGameRvAdapter

    private var bottomSheet: GameEntrySheet? = null

    override fun onPause() {
        bottomSheet?.dismiss()
        bottomSheet = null
        super.onPause()
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
                openBottomSheet()
            }
        }
        initRecyclerView()
        registerObservers()
    }

    private suspend fun openBottomSheet() {
        if (bottomSheet != null) return
        bottomSheet = GameEntrySheet() { bottomSheet = null } //Prevent Memory Leak
        withContext(Dispatchers.Main) { bottomSheet?.show(childFragmentManager, bottomSheet?.tag) }
    }

    private fun initRecyclerView() {
        currentGameRvAdapter = CurrentGameRvAdapter(this)
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
                currentGameRvAdapter.submitListOfGames(it.games.sortedByDescending { i -> i.creationTime })
                if (p1Sum > 1000 && p1Sum > p2Sum) {
                    declareWinner(viewModel.player1Name.value)
                }
                if (p2Sum > 1000 && p2Sum > p1Sum) {
                    declareWinner(viewModel.player2Name.value)
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

    override fun onItemClick(game: Game, position: Int, view: View) {
        createBalloon(requireContext()) {
            setArrowVisible(false)
            setAlpha(0.8f)
            setText("Izbriši igru")
            setPadding(8)
            setTextSize(26f)
            setLifecycleOwner(viewLifecycleOwner)
            setBalloonAnimation(BalloonAnimation.FADE)
            setOnBalloonClickListener {
                lifecycleScope.launch {
                    viewModel.deleteGame(game)
                    Snackbar.make(binding.root, "Igra je izbrisana!", 2000)
                        .setAction("Poništi") {
                            lifecycleScope.launch { viewModel.insertGame(game) }
                        }.show()
                }
            }
            setAutoDismissDuration(2000L)
            setDismissWhenClicked(true)
        }.show(view)
    }
}