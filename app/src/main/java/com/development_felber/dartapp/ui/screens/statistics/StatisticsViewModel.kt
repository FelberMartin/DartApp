package com.development_felber.dartapp.ui.screens.statistics

import androidx.lifecycle.*
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLegDao
import com.development_felber.dartapp.ui.navigation.NavigationCommand
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.util.graphs.filter.GamesLegFilter
import com.development_felber.dartapp.util.graphs.filter.LegFilterBase
import com.development_felber.dartapp.util.graphs.statistics.StatisticTypeBase
import com.development_felber.dartapp.util.graphs.statistics.linechart.AverageStatistic
import com.development_felber.dartapp.views.chart.util.DataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val finishedLegDao: FinishedLegDao
): ViewModel() {

    private val _statisticType = MutableLiveData<StatisticTypeBase>(StatisticTypeBase.PlaceHolderStatistic)
    val statisticType: LiveData<StatisticTypeBase> = _statisticType

    private val _selectedFilterCategory = MutableLiveData<LegFilterBase.Category>(LegFilterBase.Category.ByGameCount)
    val selectedFilterCategory: LiveData<LegFilterBase.Category> = _selectedFilterCategory

    private val _legFilter = MutableLiveData<LegFilterBase>(GamesLegFilter.last10)
    val legFilter: LiveData<LegFilterBase> = _legFilter

    private val _dataSet = MutableLiveData<DataSet>(DataSet())
    val dataSet: LiveData<DataSet> = _dataSet

    private var legs: List<FinishedLeg> = listOf()

    private val _noLegDataAvailable = MutableLiveData(true)
    val noLegDataAvailable: LiveData<Boolean> = _noLegDataAvailable


    init {
        _statisticType.value = AverageStatistic
        viewModelScope.launch {
            finishedLegDao.getAllSoloLegs().collect {
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

    fun navigateBack() {
        navigationManager.navigate(NavigationCommand.Back)
    }

    fun navigateToTable() {
        navigationManager.navigate(NavigationCommand.ToTable)
    }

    fun navigateToHistory() {
        navigationManager.navigate(NavigationCommand.ToHistory)
    }

}