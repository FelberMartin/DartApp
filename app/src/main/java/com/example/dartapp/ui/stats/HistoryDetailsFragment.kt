package com.example.dartapp.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dartapp.databinding.FragmentHistoryDetailsBinding

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

        return binding.root
    }
}