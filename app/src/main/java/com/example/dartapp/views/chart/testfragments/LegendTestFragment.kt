package com.example.dartapp.views.chart.testfragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.example.dartapp.databinding.FragmentLegendTestBinding
import com.example.dartapp.databinding.FragmentPieChartBinding
import com.example.dartapp.views.chart.DataSet
import com.example.dartapp.views.chart.PieChart
import com.example.dartapp.views.chart.legend.Legend

/**
 * Testing class for the Chart Legend
 */
class LegendTestFragment : Fragment(), SeekBar.OnSeekBarChangeListener{

    private var _binding: FragmentLegendTestBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLegendTestBinding.inflate(inflater, container, false)

        val chart = PieChart(requireContext())
        chart.data = DataSet.random(type = DataSet.Type.STRING, count = 9)
        binding.legend.linkedChart = chart
//        binding.legend.mode = Legend.Mode.STACKED
//        binding.legend.indicatorShape = Legend.IndicatorShape.RECTANGLE

        binding.seekBar.setOnSeekBarChangeListener(this)
        binding.seekBar.min = 10
        binding.seekBar.max = 80

        return binding.root
    }

    override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
        val textSize = progress
        binding.label.text = "$textSize px"
        binding.legend.textSize = textSize.toFloat()
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }

}