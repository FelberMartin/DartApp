package com.example.dartapp.ui.stats.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.example.dartapp.databinding.ItemTableBinding
import com.example.dartapp.ui.stats.LegsViewModel
import com.example.dartapp.util.decimalToString

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class TableItemAdapter(
    private val values: List<TableItem>,
    private val viewModel: LegsViewModel
) : RecyclerView.Adapter<TableItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemTableBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.name
        holder.valueView.text = decimalToString(item.getValue(viewModel.legs.value))
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ItemTableBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameView: TextView = binding.itemNumber
        val valueView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + valueView.text + "'"
        }
    }

}