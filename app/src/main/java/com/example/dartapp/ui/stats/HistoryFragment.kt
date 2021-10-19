package com.example.dartapp.ui.stats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dartapp.database.Leg
import com.example.dartapp.database.LegDatabase
import com.example.dartapp.databinding.FragmentHistoryBinding
import com.example.dartapp.ui.stats.adapters.HistoryAdapter
import com.example.dartapp.util.App
import com.example.dartapp.util.getNavOptions

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: HistoryAdapter

    private lateinit var viewModel: LegsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val vm: LegsViewModel by activityViewModels()
        this.viewModel = vm

        adapter = HistoryAdapter(viewLifecycleOwner, viewModel)
        adapter.listener = ::onClick
        binding.recyclerView.adapter = adapter

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        binding.recyclerView.setEmptyView(binding.emptyLabel)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onClick(leg: Leg) {
        viewModel.setDetailed(leg)

        val action = StatsFragmentDirections.actionStatsFragmentToHistoryDetailsFragment()
        findNavController().navigate(action, getNavOptions())
    }
}