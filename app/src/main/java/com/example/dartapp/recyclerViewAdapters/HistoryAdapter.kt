package com.example.dartapp.recyclerViewAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dartapp.database.Leg
import com.example.dartapp.databinding.ItemHistoryBinding
import com.example.dartapp.game.gameModes.GameMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryAdapter(private var legs: ArrayList<Leg>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with (legs[position]) {
                val fromRaw = GameMode.ID.values().firstOrNull { it.value == gameMode }
                binding.title.text = fromRaw.toString()

                binding.avgTextView.text = String.format("%.2f", servesAvg)
                binding.servesCountTextView.text = servesCount.toString()

                val date = Date(endTime)
                val timeString = SimpleDateFormat("HH:mm").format(date)
                val dateString = SimpleDateFormat.getDateInstance().format(date)
                binding.timeTextView.text = timeString
                binding.dateTextView.text = dateString
            }
        }
    }

    override fun getItemCount(): Int {
        return legs.size
    }

}