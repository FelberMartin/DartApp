package com.example.dartapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dartapp.databinding.FragmentModesBinding
import com.example.dartapp.game.gameModes.GameMode

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ModeFragment : Fragment() {

    private var _binding: FragmentModesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentModesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listeners for all Buttons
        binding.layout.children.filter { it is Button }.forEach {
            val button = it as Button
            button.setOnClickListener {
                // TODO: replace this with the actual value depending on the button pressed.
                //       For this change the layout of this Fragment to use a recycler view instead of a linear layout.
                val gameModeTypeId = GameMode.Type.X01.id

                val action = ModeFragmentDirections.actionModeFragmentToTrainingActivity(gameModeTypeId)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}