package com.development_felber.dartapp.ui.screens.table

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.development_felber.dartapp.data.persistent.database.leg.Leg
import com.development_felber.dartapp.data.persistent.database.leg.LegDao
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.shared.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableViewModel @Inject constructor(
    navigationManager: NavigationManager,
    private val legDao: LegDao
) : NavigationViewModel(navigationManager) {

    private var legs: List<Leg> = listOf()

    private val _totalItems = MutableLiveData<List<Pair<String, String>>>(listOf())
    val totalItems: LiveData<List<Pair<String, String>>> = _totalItems

    private val _averageItems = MutableLiveData<List<Pair<String, String>>>(listOf())
    val averageItems: LiveData<List<Pair<String, String>>> = _averageItems

    private val _distributionItems = MutableLiveData<List<Pair<String, String>>>()
    val distributionItems: LiveData<List<Pair<String, String>>> = _distributionItems

    init {
        updateItems()

        viewModelScope.launch {
            legDao.getAllLegs().asFlow().collect {
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
}