package com.ricko.belablok.ui.masterfragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ricko.belablok.R
import com.ricko.belablok.ui.allgames.AllMatchesFragment
import com.ricko.belablok.ui.currentgame.CurrentGameFragment

class MasterFragment : Fragment(R.layout.fragment_master_current_game) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentGameFragment = CurrentGameFragment()
        val allMatchesFragment = AllMatchesFragment()
        childFragmentManager.beginTransaction().apply {
            if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                add(R.id.flCurrentGamePlaceholder, currentGameFragment)
                add(R.id.flAllMatchesPlaceholder, allMatchesFragment)
                commit()
            } else {
                add(R.id.flCurrentGamePlaceholder, currentGameFragment)
                commit()
            }
        }
    }
}