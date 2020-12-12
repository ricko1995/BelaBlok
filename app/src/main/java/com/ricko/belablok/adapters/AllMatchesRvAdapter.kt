package com.ricko.belablok.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ricko.belablok.databinding.ItemAllMatchesBinding
import com.ricko.belablok.db.MatchWithGames

class AllMatchesRvAdapter(private val listOfMatches: List<MatchWithGames>) : RecyclerView.Adapter<AllMatchesRvAdapter.MatchViewHolder>() {
    inner class MatchViewHolder(val binding: ItemAllMatchesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MatchViewHolder(
        ItemAllMatchesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val item = listOfMatches[position]
        holder.binding.matchesVar = item
        holder.binding.root.setOnClickListener {
            val rvAdapter = CurrentGameRvAdapter(null)
            holder.binding.rvGamesFromMatch.apply {
                if (adapter == null) {
                    adapter = rvAdapter
                }
                if (visibility == View.VISIBLE) {
                    val actualHeight = height
                    val animation: Animation = object : Animation() {
                        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                            if (interpolatedTime == 1f) {
                                visibility = View.GONE
                            } else layoutParams.height = actualHeight - (actualHeight * interpolatedTime).toInt()
                            requestLayout()
                        }
                    }
                    animation.duration = 200L
                    startAnimation(animation)
                } else {
                    rvAdapter.submitListOfGames(item.games.sortedByDescending { it.creationTime })
                    measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    val actualHeight = measuredHeight
                    layoutParams.height = 0

                    visibility = View.VISIBLE

                    val animation: Animation = object : Animation() {
                        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                            layoutParams.height =
                                if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (actualHeight * interpolatedTime).toInt()
                            requestLayout()

                        }
                    }
                    animation.duration = 200L
                    startAnimation(animation)
                    requestLayout()
                }
            }
//            holder.binding.rvGamesFromMatch.visibility = if (holder.binding.rvGamesFromMatch.visibility == View.VISIBLE) View.GONE else View.VISIBLE

        }
    }

    private fun initGamesRv() {

    }

    override fun getItemCount() = listOfMatches.size
}