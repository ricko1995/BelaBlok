package com.ricko.belablok.ui.currentgame

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ricko.belablok.R
import com.ricko.belablok.adapters.CurrentGameRvAdapter
import com.ricko.belablok.databinding.FragmentCurrentGameBinding
import com.ricko.belablok.db.Game
import com.ricko.belablok.ui.gameentry.GameEntrySheet
import com.ricko.belablok.ui.masterfragment.MasterFragmentDirections
import com.ricko.belablok.util.Constants.MAX_SCORE_KEY
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.createBalloon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class CurrentGameFragment : Fragment(R.layout.fragment_current_game), CurrentGameRvAdapter.OnItemClickListener {

    private lateinit var binding: FragmentCurrentGameBinding
    private val viewModel: CurrentGameViewModel by viewModels()
    lateinit var currentGameRvAdapter: CurrentGameRvAdapter

    private var bottomSheet: GameEntrySheet? = null

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    override fun onPause() {
        bottomSheet?.dismiss()
        bottomSheet = null
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                findNavController().navigate(MasterFragmentDirections.actionMasterFragmentToSettingsFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCurrentGameBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
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
                dataStore.data.map { it[MAX_SCORE_KEY] ?: 1000 }.collect { maxScore ->
                    if (viewModel.player1Sum.value!! > maxScore || viewModel.player2Sum.value!! > maxScore || viewModel.lastMatch.value == null) {
                        viewModel.createNewMatch()
                    }
                    openBottomSheet()
                }
            }
        }
        initRecyclerView()
        registerObservers()
    }

    private suspend fun openBottomSheet() {
        if (bottomSheet != null) return
        bottomSheet = GameEntrySheet { bottomSheet = null } //Prevent Memory Leak
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
            lifecycleScope.launchWhenStarted {
                viewModel.apply {
                    dataStore.data.map { it[MAX_SCORE_KEY] ?: 1000 }.collect { mScore ->
                        if (it != null) {
                            val p1Sum = it.games.map { i -> i.player1Score }.sum() + it.games.map { i -> i.player1Callings }.sum()
                            val p2Sum = it.games.map { i -> i.player2Score }.sum() + it.games.map { i -> i.player2Callings }.sum()
                            viewModel.player1Sum.value = p1Sum
                            viewModel.player2Sum.value = p2Sum
                            currentGameRvAdapter.submitListOfGames(it.games.sortedByDescending { i -> i.creationTime })
                            if (p1Sum > mScore && p1Sum > p2Sum && !viewModel.isGameOver) {
                                declareWinner(viewModel.player1Name.value)
                            }
                            if (p2Sum > mScore && p2Sum > p1Sum && !viewModel.isGameOver) {
                                declareWinner(viewModel.player2Name.value)
                            }
                        } else {
                            viewModel.player1Sum.value = 0
                            viewModel.player2Sum.value = 0
                            currentGameRvAdapter.submitListOfGames(emptyList())
                        }
                        binding.rvCurrentGame.scrollToPosition(0)

                        isP1ScoreBoxSelected.value = false
                        isP2ScoreBoxSelected.value = false
                        if (player1Sum.value!! > mScore && player1Sum.value!! > player2Sum.value!! && player1Sum.value != player2Sum.value) {
                            isP1ScoreBoxSelected.value = true
                            isP2ScoreBoxSelected.value = false
                        }
                        if (player2Sum.value!! > mScore && player2Sum.value!! > player1Sum.value!! && player1Sum.value != player2Sum.value) {
                            isP2ScoreBoxSelected.value = true
                            isP1ScoreBoxSelected.value = false
                        }
                    }
                }
            }

        }
    }

    private fun declareWinner(playerName: String?) {
        bottomSheet?.dismiss()
        bottomSheet = null
        if (viewModel.isGameOver) return
        Toast.makeText(requireContext(), "$playerName ${getString(R.string.has_won_text)}", Toast.LENGTH_LONG).show()
        viewModel.isGameOver = true
    }

    override fun onItemClick(game: Game, position: Int, view: View) = createBalloon(requireContext()) {
        setArrowVisible(false)
        setAlpha(0.8f)
        setText(getString(R.string.delete_game_text))
        setPadding(8)
        setTextSize(26f)
        setLifecycleOwner(viewLifecycleOwner)
        setBalloonAnimation(BalloonAnimation.FADE)
        setOnBalloonClickListener {
            lifecycleScope.launch {

                dataStore.data.map { it[MAX_SCORE_KEY] ?: 1000 }.collect { maxScore ->
                    viewModel.deleteGame(game)
                    delay(100)
                    if ((viewModel.player1Sum.value!! < maxScore && viewModel.player2Sum.value!! < maxScore) || viewModel.player1Sum == viewModel.player2Sum)
                        viewModel.isGameOver = false
                    Snackbar.make(binding.root, getString(R.string.game_deleted_text), 2000)
                        .setAction(getString(R.string.undo_text)) {
                            lifecycleScope.launch { viewModel.insertGame(game) }
                        }.show()
                }
            }
        }
        setAutoDismissDuration(2000L)
        setDismissWhenClicked(true)
    }.show(view)

}