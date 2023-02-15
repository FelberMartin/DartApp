package com.development_felber.dartapp.ui.screens.table

import androidx.lifecycle.*
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLegDao
import com.development_felber.dartapp.ui.navigation.NavigationCommand
import com.development_felber.dartapp.ui.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val finishedLegDao: FinishedLegDao
) : ViewModel() {

    private var legs: List<FinishedLeg> = listOf()

    private val _totalItems = MutableLiveData<List<Pair<String, String>>>(listOf())
    val totalItems: LiveData<List<Pair<String, String>>> = _totalItems

    private val _averageItems = MutableLiveData<List<Pair<String, String>>>(listOf())
    val averageItems: LiveData<List<Pair<String, String>>> = _averageItems

    private val _distributionItems = MutableLiveData<List<Pair<String, String>>>()
    val distributionItems: LiveData<List<Pair<String, String>>> = _distributionItems

    init {
        updateItems()

        viewModelScope.launch {
            finishedLegDao.getAllSoloLegs().collect {
                legs = it
                updateItems()
            }
        }
    }

    private fun updateItems() {
        _totalItems.value = TableItem.totals.map {
                item -> Pair(item.name, item.getValue(legs))
        }
        _averageItems.value = TableItem.averages.map {
                item -> Pair(item.name, item.getValue(legs))
        }
        _distributionItems.value = TableItem.distribution.map {
                item -> Pair(item.name, item.getValue(legs))
        }
    }

    fun navigateBack() {
        navigationManager.navigate(NavigationCommand.Back)
    }
}