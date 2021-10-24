package com.example.dartapp.ui.stats

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.dartapp.util.Strings
import com.example.dartapp.views.chart.test.LegendTestFragment
import com.example.dartapp.views.chart.test.LineChartFragment


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        val title: String = Strings.get(TAB_TITLES[position])
        val fragment = when (title) {
            "History" -> HistoryFragment()
            "Table" -> TableFragment()
            "Graphs" -> LineChartFragment()
            else -> LegendTestFragment()
        }

        return fragment
    }

    override fun getItemCount(): Int {
        return TAB_TITLES.size
    }

}