package com.example.dartapp.ui.stats.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dartapp.databinding.HeaderTableBinding
import com.example.dartapp.databinding.ItemTableBinding
import com.example.dartapp.game.gameModes.GameMode
import com.example.dartapp.ui.stats.LegsViewModel


class TableItemAdapter(
    private val values: List<TableItem>,
    private val viewModel: LegsViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val HEADER_VIEW = 0
    val CONTENT_VIEW = 1

    private var filteredLegs = viewModel.legs.value ?: listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == CONTENT_VIEW) {
            return ItemViewHolder(ItemTableBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ))
        }

        return HeaderViewHolder(HeaderTableBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemViewType(position: Int): Int {
        return if (values[position] is TableHeader) {
            HEADER_VIEW
        } else {
            CONTENT_VIEW
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = values[position]


        if (item is TableHeader) {
            with (holder as HeaderViewHolder) {
                nameView.text = item.name
            }
        } else {
            with (holder as ItemViewHolder) {
                nameView.text = item.name
                val v = item.getValue(filteredLegs)
                valueView.text = v
            }
        }

    }

    override fun getItemCount(): Int = values.size

    inner class ItemViewHolder(binding: ItemTableBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameView: TextView = binding.itemNumber
        val valueView: TextView = binding.content
    }

    inner class HeaderViewHolder(binding: HeaderTableBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameView: TextView = binding.headerTitleTextview
    }


    fun applyFilter(filter: GameMode.ID) {
        filteredLegs = viewModel.legs.value ?: listOf()
        if (filter != GameMode.ID.ALL)
            filteredLegs = filteredLegs.filter { leg -> leg.gameMode == filter.id }

        notifyDataSetChanged()
    }

}