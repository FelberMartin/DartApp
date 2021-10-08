package com.example.dartapp.ui.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dartapp.databinding.FragmentPieChartBinding


/**
 * A simple [Fragment] subclass.
 * Use the [PieChartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PieChartFragment : Fragment() {

    private var _binding: FragmentPieChartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPieChartBinding.inflate(inflater, container, false)

        binding.legend.linkedChart = binding.chart

        return binding.root
    }

}