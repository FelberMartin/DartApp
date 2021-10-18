package com.example.dartapp.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.dartapp.databinding.FragmentMenuBinding
import com.example.dartapp.util.getNavOptions

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonTraining.setOnClickListener {
            val action = MenuFragmentDirections.actionMenuFragmentToModiFragment()
            findNavController().navigate(action, getNavOptions())
        }

        binding.buttonStats.setOnClickListener {
            val action = MenuFragmentDirections.actionMenuFramentToStatsFragment()
            findNavController().navigate(action, getNavOptions())
        }

        binding.buttonTest.setOnClickListener {
            val action = MenuFragmentDirections.actionMenuFramentToTestFragment()
            findNavController().navigate(action, getNavOptions())
        }

        binding.buttonSettings.setOnClickListener {
            val action = MenuFragmentDirections.actionMenuFramentToSettingsFragment()
            findNavController().navigate(action, getNavOptions())
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}