package com.example.dartapp.ui.screens.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dartapp.chartstuff.graphs.filter.GamesLegFilter
import com.example.dartapp.chartstuff.graphs.filter.LegFilterBase
import com.example.dartapp.chartstuff.graphs.filter.TimeLegFilter
import com.example.dartapp.chartstuff.graphs.statistics.PointsPerServeAverage
import com.example.dartapp.chartstuff.graphs.statistics.StatisticTypeBase
import com.example.dartapp.data.persistent.database.LegDatabaseDao
import com.example.dartapp.graphs.versus.GamesVersusType
import com.example.dartapp.graphs.versus.TimeVersusType
import com.example.dartapp.graphs.versus.VersusTypeBase
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.NavigationViewModel
import com.example.dartapp.views.chart.util.DataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    navigationManager: NavigationManager,
    private val legDatabaseDao: LegDatabaseDao
): NavigationViewModel(navigationManager) {

    // TODO: use LiveData instead of states

    private val _statisticType = MutableLiveData<StatisticTypeBase>(PointsPerServeAverage())
    val statisticType: LiveData<StatisticTypeBase> = _statisticType

    private val _versusType = MutableLiveData<VersusTypeBase>(GamesVersusType())
    val versusType: LiveData<VersusTypeBase> = _versusType

    private val _legFilter = MutableLiveData<LegFilterBase>(GamesLegFilter())
    val legFilter: LiveData<LegFilterBase> = _legFilter

    private val _dataSet = MutableLiveData<DataSet>(DataSet())
    val dataSet: LiveData<DataSet> = _dataSet

    init {
        rebuildDataSet()
    }

    fun setStatisticType(type: StatisticTypeBase) {
        _statisticType.value = type
        setVersusType(type.getAvailableVersusTypes()[0])
    }

    fun setLegFilter(filter: LegFilterBase) {
        _legFilter.value = filter
        rebuildDataSet()
    }

    fun setVersusType(type: VersusTypeBase) {
        _versusType.value = type
        when (type) {
            is GamesVersusType -> setLegFilter(GamesLegFilter())
            is TimeVersusType -> setLegFilter(TimeLegFilter())
        }
    }


    private fun rebuildDataSet() {
        val legs = legDatabaseDao.getAllLegs()
        val filteredLegs = legFilter.value!!.filterLegs(legs)
        _dataSet.value = statisticType.value!!.buildDataSet(filteredLegs, versusType.value!!)
        println("Rebuilt dataset")
    }

}