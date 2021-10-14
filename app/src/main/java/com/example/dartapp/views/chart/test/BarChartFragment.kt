package com.example.dartapp.views.chart.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dartapp.databinding.FragmentBarChartBinding
import com.example.dartapp.views.chart.util.DataSet


class BarChartFragment : Fragment() {

    private var _binding: FragmentBarChartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentBarChartBinding.inflate(inflater, container, false)

        binding.newData.setOnClickListener { binding.chart.data = DataSet.random(type = DataSet.Type.STRING, count = 4) }
        binding.chart.link(binding.legend)

        return binding.root
    }

}