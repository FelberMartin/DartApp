package com.example.dartapp.views.chart.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.dartapp.databinding.FragmentPieChartBinding
import com.example.dartapp.views.chart.util.DataSet
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable


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

    var checkIconSize = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPieChartBinding.inflate(inflater, container, false)
        checkIconSize = (binding.chip7.chipDrawable as ChipDrawable).chipIconSize

        binding.legend.linkedChart = binding.chart
        binding.newData.setOnClickListener { binding.chart.data = DataSet.random(type = DataSet.Type.STRING, count = 3) }

        val chipGroup = binding.chipGroup

        for (view in chipGroup.children) {
            val chip = view as Chip
            chip.setOnCheckedChangeListener { buttonView, _ ->
                val index = chipGroup.indexOfChild(buttonView)
                chipGroup.removeView(buttonView)
                chipGroup.addView(buttonView, index)
            }
        }

        return binding.root
    }


}