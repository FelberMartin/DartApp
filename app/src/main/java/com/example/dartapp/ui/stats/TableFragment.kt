package com.example.dartapp.ui.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dartapp.R
import com.example.dartapp.databinding.FragmentTableBinding
import com.example.dartapp.ui.stats.adapters.TableItem
import com.example.dartapp.ui.stats.adapters.TableItemAdapter

/**
 * A fragment representing a list of Items.
 */
class TableFragment : Fragment() {

    private var _binding: FragmentTableBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTableBinding.inflate(inflater, container, false)

        val vm: LegsViewModel by activityViewModels()

        with(binding.list) {
            layoutManager = LinearLayoutManager(context)
            adapter = TableItemAdapter(TableItem.items() ,vm)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}