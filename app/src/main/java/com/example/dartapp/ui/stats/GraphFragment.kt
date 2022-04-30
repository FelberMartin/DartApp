package com.example.dartapp.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.dartapp.databinding.FragmentGraphBinding
import com.example.dartapp.graphs.statistics.StatisticTypeBase


class GraphFragment : Fragment(), AdapterView.OnItemSelectedListener{

    private var _binding: FragmentGraphBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGraphBinding.inflate(layoutInflater)

        initStatsSpinner()

        return binding.root
    }

    private fun initStatsSpinner() {
        val statsSpinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item, StatisticTypeBase.all.map { type -> type.name }
        )
        statsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStats.apply {
            adapter = statsSpinnerAdapter
            onItemSelectedListener = this@GraphFragment
            setSelection(0)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}
