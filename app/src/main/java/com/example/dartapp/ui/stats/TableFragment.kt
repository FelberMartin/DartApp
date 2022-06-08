package com.example.dartapp.ui.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dartapp.databinding.FragmentTableBinding
import com.example.dartapp.ui.stats.adapters.TableItem
import com.example.dartapp.ui.stats.adapters.TableItemAdapter

import android.widget.ArrayAdapter
import com.example.dartapp.game.gameModes.GameMode
import com.example.dartapp.util.resources.Strings


/**
 * A fragment representing a list of Items.
 */
class TableFragment : Fragment(), AdapterView.OnItemSelectedListener {


    private var _binding: FragmentTableBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var tableAdapter: TableItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTableBinding.inflate(inflater, container, false)

        val vm: LegsViewModel by activityViewModels()

        tableAdapter = TableItemAdapter(TableItem.items() ,vm)
        with(binding.list) {
            layoutManager = LinearLayoutManager(context)
            adapter = tableAdapter
        }

        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item, GameMode.Type.values().map { Strings.get(it.stringRes) }
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = spinnerAdapter
        binding.spinner.onItemSelectedListener = this

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val selectedItem = GameMode.Type.values()[position]
        val filter = GameMode.Type.values().first { it == selectedItem }
        tableAdapter.applyFilter(filter)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}

}