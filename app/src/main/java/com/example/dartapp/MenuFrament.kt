package com.example.dartapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.dartapp.databinding.FragmentMenuBinding
import kotlin.random.Random

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MenuFrament : Fragment() {

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
            val action = MenuFramentDirections.actionMenuFragmentToModiFragment()
            findNavController().navigate(action)
        }

//        binding.buttonFirst.setOnClickListener {
//            val count = binding.counterTv.text.toString().toInt()
//            val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(count)
//            findNavController().navigate(action)
//        }
//
//        binding.buttonToast.setOnClickListener {
//            val myToast = Toast.makeText(context, "Toastbrot", Toast.LENGTH_SHORT)
//            myToast.show()
//        }
//        binding.buttonCount.setOnClickListener {
//            val counterStr = binding.counterTv.text.toString()
//            val counter = counterStr.toInt() + 1
//            binding.counterTv.text = counter.toString();
//        }
//        binding.buttonRandom.setOnClickListener {
//            val rnd = Random.nextInt()
//            binding.counterTv.text = rnd.toString()
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}