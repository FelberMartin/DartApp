package com.example.dartapp.ui.stats.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.dartapp.databinding.HeaderTableBinding

import com.example.dartapp.databinding.ItemTableBinding
import com.example.dartapp.ui.stats.LegsViewModel


/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class TableItemAdapter(
    private val values: List<TableItem>,
    private val viewModel: LegsViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val HEADER_VIEW = 0
    val CONTENT_VIEW = 1

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
                holder.nameView.text = item.name
                holder.valueView.text = item.getValue(viewModel.legs.value)
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

}