package com.example.dartapp.ui.screens.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.dartapp.util.graphs.filter.GamesLegFilter
import com.example.dartapp.util.graphs.filter.LegFilterBase
import com.example.dartapp.util.graphs.statistics.PointsPerServeAverage
import com.example.dartapp.util.graphs.statistics.StatisticTypeBase
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.data.persistent.database.LegDatabaseDao
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.NavigationViewModel
import com.example.dartapp.views.chart.util.DataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    navigationManager: NavigationManager,
    private val legDatabaseDao: LegDatabaseDao
): NavigationViewModel(navigationManager) {

    private val _statisticType = MutableLiveData<StatisticTypeBase>(PointsPerServeAverage())
    val statisticType: LiveData<StatisticTypeBase> = _statisticType

    private val _selectedFilterCategory = MutableLiveData<LegFilterBase.Category>(LegFilterBase.Category.ByGameCount)
    val selectedFilterCategory: LiveData<LegFilterBase.Category> = _selectedFilterCategory

    private val _legFilter = MutableLiveData<LegFilterBase>(GamesLegFilter.last10)
    val legFilter: LiveData<LegFilterBase> = _legFilter

    private val _dataSet = MutableLiveData<DataSet>(DataSet())
    val dataSet: LiveData<DataSet> = _dataSet

    private var legs: List<Leg> = listOf()

    private val _noLegDataAvailable = MutableLiveData(true)
    val noLegDataAvailable: LiveData<Boolean> = _noLegDataAvailable


    init {
        viewModelScope.launch {
            legDatabaseDao.getAllLegs().asFlow().collect {
                println("Collected legs (size = ${it.size})")
                legs = it
                _noLegDataAvailable.value = legs.isEmpty()
                rebuildDataSet()
            }
        }
    }

    fun setStatisticType(type: StatisticTypeBase) {
        _statisticType.value = type
        if (type.availableFilterCategories.contains(selectedFilterCategory.value)) {
            rebuildDataSet()
        } else {
            setSelectedFilterCategory(type.availableFilterCategories.first())
        }
    }

    fun setSelectedFilterCategory(filterCategory: LegFilterBase.Category) {
        _selectedFilterCategory.value = filterCategory
        setLegFilter(filterCategory.filterOptions.first())
    }

    fun setLegFilter(filter: LegFilterBase) {
        _legFilter.value = filter
        rebuildDataSet()
    }

    fun update() {
        rebuildDataSet()
    }

    private fun rebuildDataSet() {
        _dataSet.value = statisticType.value!!.buildDataSet(legs, legFilter.value!!)
    }

}