package com.example.dartapp.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dartapp.R
import com.example.dartapp.util.Navigation


class LoadingFragment : Fragment() {

    private lateinit var viewModel: LegsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val vm: LegsViewModel by activityViewModels()
        this.viewModel = vm

        vm.isLoading.observe(viewLifecycleOwner) {
            if (it == false) {
                val action = LoadingFragmentDirections.actionLoadingFragmentToStatsFragment()
                val builder = Navigation.getDefaultNavOptionsBuilder()
                builder.setPopUpTo(R.id.MenuFrament, false)
                findNavController().navigate(action, builder.build())
            }
        }


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }


}