package com.ricko.belablok.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.ricko.belablok.R
import com.ricko.belablok.db.Game

class CurrentGameRvAdapter : RecyclerView.Adapter<CurrentGameRvAdapter.MyVh>() {
    inner class MyVh(itemView: View): RecyclerView.ViewHolder(itemView){
        var tv1: TextView = itemView.findViewById<MaterialTextView>(R.id.textView555)
        var tv2: TextView = itemView.findViewById<MaterialTextView>(R.id.textView6)
    }
//    class GameViewHolder private constructor(val binding: ItemCurrentGameBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(game: Game) {
//            binding.currentGameRecyclerVar = game
//            binding.executePendingBindings()
//        }
//        companion object {
//            fun from(parent: ViewGroup): GameViewHolder {
//                val layoutInflater = LayoutInflater.from(parent.context)
//                val binding = ItemCurrentGameBinding.inflate(layoutInflater, parent, false)
//                return GameViewHolder(binding)
//            }
//        }
//    }

    private val diffUtil = object : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Game, newItem: Game) = oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, diffUtil)

    fun submitListOfGames(games: List<Game>) = differ.submitList(games)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyVh(
        LayoutInflater.from(parent.context).inflate(R.layout.item_current_game, parent, false)
//        ItemCurrentGameBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
    )

    override fun onBindViewHolder(holder: MyVh, position: Int) {
        val item = differ.currentList[position]
        holder.tv1.text = item.player1Score.toString()
        holder.tv2.text = item.player2Score.toString()
    }

    override fun getItemCount() = differ.currentList.size
}