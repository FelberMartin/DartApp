package com.example.dartapp.views.chart.testfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.example.dartapp.databinding.FragmentLineChartBinding
import com.example.dartapp.databinding.FragmentPieChartBinding
import com.example.dartapp.views.chart.DataSet


class LineChartFragment : Fragment() {

    private var _binding: FragmentLineChartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLineChartBinding.inflate(inflater, container, false)
        val chart = binding.chart

        binding.reload.setOnClickListener { chart.data = DataSet.random(count = 8) }

        binding.xStartZero.isChecked = chart.xStartAtZero
        binding.yStartZero.isChecked = chart.yStartAtZero
        binding.xAutoSpace.isChecked = chart.xEdgeAutoPadding
        binding.yAutoSpace.isChecked = chart.yEdgeAutoPadding
        binding.smooth.isChecked = chart.smoothedLine

        binding.xStartZero.setOnCheckedChangeListener { _, b -> chart.xStartAtZero = b; chart.reload() }
        binding.yStartZero.setOnCheckedChangeListener { _, b -> chart.yStartAtZero = b; chart.reload() }
        binding.xAutoSpace.setOnCheckedChangeListener { _, b -> chart.xEdgeAutoPadding = b; chart.reload() }
        binding.yAutoSpace.setOnCheckedChangeListener { _, b -> chart.yEdgeAutoPadding = b; chart.reload() }
        binding.smooth.setOnCheckedChangeListener { _, b -> chart.smoothedLine = b; chart.reload() }


        return binding.root
    }

}