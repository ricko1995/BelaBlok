package com.ricko.belablok.ui.masterfragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.ricko.belablok.R
import com.ricko.belablok.ui.allgames.AllMatchesFragment
import com.ricko.belablok.ui.currentgame.CurrentGameFragment

class MasterFragment : Fragment(R.layout.fragment_master_current_game) {
    private val currentGameFragment = CurrentGameFragment()
    private val allMatchesFragment = AllMatchesFragment()

    override fun onPause() {
        super.onPause()
        childFragmentManager.beginTransaction().apply {
            remove(currentGameFragment)
            remove(allMatchesFragment)
            commit()
        }
    }

    override fun onResume() {
        super.onResume()
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