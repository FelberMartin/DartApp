package com.example.dartapp.ui.stats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dartapp.database.Converters
import com.example.dartapp.database.Leg
import com.example.dartapp.database.LegDatabase
import com.example.dartapp.util.App
import com.example.dartapp.views.chart.util.DataPoint
import com.example.dartapp.views.chart.util.DataSet

class LegsViewModel : ViewModel() {

    private val categoryLimits = listOf(60, 100, 140, 180)

    lateinit var legs: LiveData<List<Leg>>
    val detailedLeg = MutableLiveData(Leg())

    val doublePercent = MutableLiveData("0%")
    private var servesList = arrayListOf<Int>()

    val servesData = MutableLiveData(DataSet())
    val categoryData = MutableLiveData(DataSet())


    init {
        loadAll()
    }

    fun loadAll() {
        val context = App.instance.applicationContext
        val legTable = LegDatabase.getInstance(context).legDatabaseDao
        legs = legTable.getAllLegs()

    }


    fun setDetailed(leg: Leg) {
        detailedLeg.value = leg

        var doubleCount = leg.doubleAttempts
        if (doubleCount == 0) doubleCount = 1
        doublePercent.value = String.format("%d%%", 100 * doubleCount / 100)

        servesList = Converters.toArrayListOfInts(leg.servesList)

        servesData.value = lineChartDataSet()
        categoryData.value = pieChartDataSet()
    }


    private fun lineChartDataSet() : DataSet {
        val data = DataSet()
        data.dataPointXType = DataSet.Type.NUMBER
        servesList.forEachIndexed { index, i -> data.add(DataPoint(index, i)) }
        return data
    }

    private fun pieChartDataSet() : DataSet {
        val data = DataSet()
        data.dataPointXType = DataSet.Type.STRING

        for ((index, limit) in categoryLimits.withIndex()) {
            var string = "$limit+"
            var count = 0
            if (limit != 180)
                count = servesList.count { s -> limit <= s && s < categoryLimits[index + 1] }
            else {
                string = "$limit"
                count = servesList.count { s -> s == 180 }
            }

            data.add(DataPoint(string, count))
        }

        return data
    }
}