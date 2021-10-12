package com.example.dartapp.views.chart.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.dartapp.databinding.FragmentTestBinding
import com.example.dartapp.ui.stats.HistoryFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

val TEST_TAB_TITLES = listOf("LineChart", "PieChart", "BarChart", "Legend")

class TestFragment : Fragment() {

    private lateinit var _binding: FragmentTestBinding

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTestBinding.inflate(inflater, container, false)

        // Setup of the TabLayout
        val adapter = TestAdapter(this)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = adapter
        val tabLayout: TabLayout = binding.tabs
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = TEST_TAB_TITLES[position]
        }.attach()


        return binding.root
    }

}

class TestAdapter(private val fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        val title: String = TEST_TAB_TITLES[position]
        val fragment = when (title) {
            "BarChart" -> BarChartFragment()
            "PieChart" -> PieChartFragment()
            "LineChart" -> LineChartFragment()
            "Legend" -> LegendTestFragment()
            else -> LegendTestFragment()
        }

        return fragment
    }

    override fun getItemCount(): Int {
        return TEST_TAB_TITLES.size
    }

}