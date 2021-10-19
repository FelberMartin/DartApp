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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class HistoryAdapter(private val lifecycleOwner: LifecycleOwner, private val viewModel: LegsViewModel) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    var listener: ((Leg) -> Unit)? = null

    init {
        viewModel.legs.observe(lifecycleOwner) {
            this.notifyDataSetChanged()
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
                binding.title.text = fromRaw.toString()

                binding.avgTextView.text = String.format("%.2f", servesAvg)
                binding.servesCountTextView.text = dartCount.toString()

                val date = Date(endTime)
                val timeString = SimpleDateFormat("HH:mm").format(date)
                val dateString = SimpleDateFormat.getDateInstance().format(date)
                binding.timeTextView.text = timeString
                binding.dateTextView.text = dateString

                val weekday = SimpleDateFormat("EE").format(date)
                binding.weekdayTextView.text = weekday

                binding.layout.setOnClickListener { listener?.invoke(this) }
            }
        }
    }

    override fun getItemCount(): Int {
        return viewModel.legs.value?.size ?: 0
    }


}