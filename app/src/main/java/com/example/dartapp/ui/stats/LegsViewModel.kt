package com.example.dartapp.ui.stats

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dartapp.database.Converters
import com.example.dartapp.database.Leg
import com.example.dartapp.database.LegDatabase
import com.example.dartapp.util.GameUtil
import com.example.dartapp.util.dateString
import com.example.dartapp.util.timeString
import com.example.dartapp.util.weekDayString
import com.example.dartapp.views.chart.data.DataPoint
import com.example.dartapp.views.chart.util.DataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class LegsViewModel @Inject constructor(
    @Named("example_data_db") legDatabase: LegDatabase
): ViewModel() {

    private val legDatabaseDao = legDatabase.legDatabaseDao()

    private val categoryLimits = listOf(0, 60, 100, 140, 180)

    val isLoading: MutableLiveData<Boolean> = MutableLiveData(true)

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
        loadAllAsync()
    }

    private fun loadAllAsync() {
        isLoading.value = true
        Handler(Looper.getMainLooper()).postDelayed(this::loadAll, 0)
    }

    private fun loadAll() {
        legs = legDatabaseDao.getAllLegs()
        isLoading.value = false
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun setDetailed(leg: Leg) {
        detailedLeg.value = leg

        var doubleCount = leg.doubleAttempts
        if (doubleCount == 0) doubleCount = 1
        doublePercent.value = String.format("%d%%", 100 / (doubleCount))

        average.value = String.format("%.1f", leg.servesAvg)

        servesList = ArrayList(Converters.toListOfInts(leg.servesList))

        servesData.value = lineChartDataSet()
        categoryData.value = pieChartDataSet()

        val date = Date(leg.endTime)
        weekDay.value = date.weekDayString()
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

        GameUtil.countServesForCategories(servesList, categoryLimits).forEach { (limit, count) ->
            val s = if (limit != 180) "$limit+" else "180"
            data.add(DataPoint(s, count))
        }

        return data
    }
}