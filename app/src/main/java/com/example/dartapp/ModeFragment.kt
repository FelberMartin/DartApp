package com.example.dartapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dartapp.databinding.FragmentModiBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ModeFragment : Fragment() {

    private var _binding: FragmentModiBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentModiBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listeners for all Buttons
        binding.layout.children.filter { it is Button }.forEach {
            val button = it as Button
            button.setOnClickListener {
                val extraString: String = resources.getString(R.string.extra_string_mode)
                val modeString = button.text as String

                val action = ModeFragmentDirections.actionModeFragmentToTrainingActivity(modeString)
                findNavController().navigate(action)

//                val intent = Intent(activity, TrainingActivity::class.java)
//                intent.putExtra(extraString, modeString)
//                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}