package com.example.dartapp.ui.stats

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dartapp.database.Converters
import com.example.dartapp.database.Leg
import com.example.dartapp.database.LegDatabase
import com.example.dartapp.util.*
import com.example.dartapp.views.chart.util.DataPoint
import com.example.dartapp.views.chart.util.DataSet
import java.util.*

class LegsViewModel : ViewModel() {

    private val categoryLimits = listOf(0, 60, 100, 140, 180)

    lateinit var legs: LiveData<List<Leg>>
    val detailedLeg = MutableLiveData(Leg())

    val doublePercent = MutableLiveData("0%")
    val average = MutableLiveData("0.0")
    private var servesList = arrayListOf<Int>()

    val servesData = MutableLiveData(DataSet())
    val categoryData = MutableLiveData(DataSet())

    val weekDay = MutableLiveData("Mo")
    val timeString = MutableLiveData("04:20")
    val dateString = MutableLiveData("Jan 12, 2001")

    init {
        loadAll()
    }

    fun loadAll() {
        val context = App.instance.applicationContext
        val legTable = LegDatabase.getInstance(context).legDatabaseDao
        legs = legTable.getAllLegs()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun setDetailed(leg: Leg) {
        detailedLeg.value = leg

        var doubleCount = leg.doubleAttempts
        if (doubleCount == 0) doubleCount = 1
        doublePercent.value = String.format("%d%%", 100 / (doubleCount))

        average.value = String.format("%.1f", leg.servesAvg)

        servesList = Converters.toArrayListOfInts(leg.servesList)

        servesData.value = lineChartDataSet()
        categoryData.value = pieChartDataSet()

        val date = Date(leg.endTime)
        weekDay.value = date.weekDay()
        timeString.value = date.timeString()
        dateString.value = date.dateString()
    }


    private fun lineChartDataSet() : DataSet {
        val data = DataSet()
        data.dataPointXType = DataSet.Type.NUMBER
        servesList.forEachIndexed { index, i -> data.add(DataPoint(index, i)) }
        return data
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun pieChartDataSet() : DataSet {
        val data = DataSet()
        data.dataPointXType = DataSet.Type.STRING

        categorizeServes(categoryLimits, servesList).forEach { (k, v) -> data.add(DataPoint(k, v)) }

        return data
    }
}