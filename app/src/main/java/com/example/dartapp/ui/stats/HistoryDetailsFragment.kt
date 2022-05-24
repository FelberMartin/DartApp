package com.example.dartapp.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dartapp.databinding.FragmentHistoryDetailsBinding
import com.example.dartapp.game.gameModes.GameMode
import com.example.dartapp.util.Strings

class HistoryDetailsFragment() : Fragment() {

    private var _binding: FragmentHistoryDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHistoryDetailsBinding.inflate(inflater, container, false)

        val viewModel: LegsViewModel by activityViewModels()
        binding.viewModel = viewModel

        binding.legend.linkedChart = binding.pieChart
        viewModel.categoryData.observe(viewLifecycleOwner) {
            binding.pieChart.data = it
        }

        viewModel.servesData.observe(viewLifecycleOwner) {
            binding.lineChart.data = it
        }

        // The title shown in the navigation controller on the top
        val gameModeId = viewModel.detailedLeg.value?.gameMode ?: GameMode.Type.X01.id
        val gameMode = GameMode.Type.fromId(gameModeId)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = Strings.get(gameMode.stringRes)

        return binding.root
    }
}