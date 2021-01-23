package com.ricko.belablok.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ricko.belablok.databinding.ItemCurrentGameBinding
import com.ricko.belablok.db.Game

class CurrentGameRvAdapter(private val onItemClickListener: OnItemClickListener?) : RecyclerView.Adapter<CurrentGameRvAdapter.GameViewHolder>() {
    inner class GameViewHolder(val binding: ItemCurrentGameBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Game, newItem: Game) = oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, diffUtil)

    fun submitListOfGames(games: List<Game>) = differ.submitList(games)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GameViewHolder(
        ItemCurrentGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.binding.currentGameRecyclerVar = item
        holder.binding.root.setOnClickListener {
            onItemClickListener?.onItemClick(item, position, holder.itemView)
        }
    }

    override fun getItemCount() = differ.currentList.size

    interface OnItemClickListener {
        fun onItemClick(game: Game, position: Int, view: View)
    }
}