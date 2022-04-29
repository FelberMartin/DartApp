package com.example.dartapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dartapp.databinding.FragmentMenuBinding
import com.example.dartapp.util.Navigation

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
    ): View {

        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonTraining.setOnClickListener {
            val action = MenuFragmentDirections.actionMenuFragmentToModiFragment()
            findNavController().navigate(action, Navigation.getDefaultNavOptions())
        }

        binding.buttonStats.setOnClickListener {
            val action = MenuFragmentDirections.actionMenuFramentToLoadingFragment()
            findNavController().navigate(action, Navigation.getDefaultNavOptions())
        }

        binding.buttonTest.setOnClickListener {
            val action = MenuFragmentDirections.actionMenuFramentToTestFragment()
            findNavController().navigate(action, Navigation.getDefaultNavOptions())
        }

        binding.buttonSettings.setOnClickListener {
            val action = MenuFragmentDirections.actionMenuFramentToSettingsFragment()
            findNavController().navigate(action, Navigation.getDefaultNavOptions())
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}