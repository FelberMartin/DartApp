package com.example.dartapp.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dartapp.R
import com.example.dartapp.databinding.FragmentGraphBinding
import com.example.dartapp.graphs.statistics.StatisticTypeBase
import com.example.dartapp.graphs.versus.VersusTypeBase
import com.example.dartapp.views.chart.EChartType
import com.example.dartapp.views.chart.util.DataSet


class GraphFragment : Fragment(), AdapterView.OnItemSelectedListener{

    private var _binding: FragmentGraphBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val statTypes = StatisticTypeBase.all

    private lateinit var viewModel: LegsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        
        _binding = FragmentGraphBinding.inflate(layoutInflater)

        val vm: LegsViewModel by activityViewModels()
        this.viewModel = vm

        initStatsSpinner()

        val dataSet = DataSet.random(count = 8, randomX = false)
        binding.chartHolder.dataSet = DataSet()

        return binding.root
    }

    private fun initStatsSpinner() {
        val statsSpinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statTypes.map { type -> type.name }
        )
        statsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStats.apply {
            adapter = statsSpinnerAdapter
            onItemSelectedListener = this@GraphFragment
            setSelection(0)
        }
    }

    private fun getSelectedStatType() : StatisticTypeBase {
        return statTypes[binding.spinnerStats.selectedItemPosition]
    }

    private fun getSelectedVersusType() : VersusTypeBase {
        val statType = getSelectedStatType()
        val pos = binding.spinnerVersus.selectedItemPosition
        return statType.getAvailableVersusTypes()[pos]
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent?.id == R.id.spinner_stats) {
            binding.chartHolder.chartType = getSelectedStatType().chartType
            updateVersusSpinner()
        } else if (parent?.id == R.id.spinner_versus) {
            val legs = viewModel.legs.value ?: listOf()
            val dataSet = getSelectedVersusType().buildDataSet(legs, getSelectedStatType()::reduceLegsToNumber)
            binding.chartHolder.dataSet = dataSet
        }

        updateUI()
    }

    private fun updateVersusSpinner() {
        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            getSelectedStatType().getAvailableVersusTypes().map { type -> type.name }
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(binding.spinnerVersus) {
            adapter = spinnerAdapter
            onItemSelectedListener = this@GraphFragment
            setSelection(0)
        }

    }

    private fun updateUI() {
        updateLegend()

        val noData = binding.chartHolder.dataSet.isEmpty()
        binding.cvNoData.visibility = if (noData) View.VISIBLE else View.GONE
    }

    private fun updateLegend() {
        val showLegend = getSelectedStatType().chartType == EChartType.PIE_CHART
        binding.legend.apply {
            linkedChart = binding.chartHolder.chart
            visibility = if (showLegend) View.VISIBLE else View.GONE
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}
