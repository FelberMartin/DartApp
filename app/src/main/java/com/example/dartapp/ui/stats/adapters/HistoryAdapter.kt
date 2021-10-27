package com.example.dartapp.ui.stats.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.dartapp.database.Leg
import com.example.dartapp.databinding.ItemHistoryBinding
import com.example.dartapp.game.gameModes.GameMode
import com.example.dartapp.ui.stats.LegsViewModel
import com.example.dartapp.util.Strings
import com.example.dartapp.util.dateString
import com.example.dartapp.util.timeString
import com.example.dartapp.util.weekDay
import java.util.*

class HistoryAdapter(private val lifecycleOwner: LifecycleOwner, private val viewModel: LegsViewModel) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    var listener: ((Leg) -> Unit)? = null

    init {
        viewModel.legs.observe(lifecycleOwner) {
            this.notifyDataSetChanged()
        }

        viewModel.doublePercent.observe(lifecycleOwner) {
            Log.d("History", "percent updated")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val leg = viewModel.legs.value?.get(position) ?: Leg()
            with (leg) {
                val fromRaw = GameMode.ID.values().firstOrNull { it.value == gameMode }
                binding.title.text = fromRaw?.stringRes?.let { Strings.get(it) }

                binding.avgTextView.text = String.format("%.2f", servesAvg)
                binding.servesCountTextView.text = dartCount.toString()

                val date = Date(endTime)
                binding.timeTextView.text = date.timeString()
                binding.dateTextView.text = date.dateString()
                binding.weekdayTextView.text = date.weekDay()

                binding.layout.setOnClickListener { listener?.invoke(this) }
            }
        }
    }

    override fun getItemCount(): Int {
        return viewModel.legs.value?.size ?: 0
    }


}