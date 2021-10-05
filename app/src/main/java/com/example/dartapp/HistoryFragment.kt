package com.example.dartapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dartapp.database.Leg
import com.example.dartapp.database.LegDatabase
import com.example.dartapp.databinding.FragmentHistoryBinding
import com.example.dartapp.databinding.FragmentMenuBinding
import com.example.dartapp.recyclerViewAdapters.HistoryAdapter
import com.example.dartapp.util.App

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var history: ArrayList<Leg> = ArrayList()
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        Thread {
            loadHistory()
        }.start()

        adapter = HistoryAdapter(history)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    private fun loadHistory() {
        val context = App.instance.applicationContext
        val legTable = LegDatabase.getInstance(context).legDatabaseDao
        var liveData = legTable.getAllLegs()

        activity?.runOnUiThread {
            liveData.observe(viewLifecycleOwner) {
                history.removeAll { true }
                history.addAll(it)

                adapter.notifyDataSetChanged()
                Log.d("LegDatabase", "History Legs loaded")
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}