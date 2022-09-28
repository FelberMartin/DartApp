package com.example.dartapp.ui.screens.table

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.data.persistent.database.LegDatabaseDao
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableViewModel @Inject constructor(
    navigationManager: NavigationManager,
    private val legDatabaseDao: LegDatabaseDao
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
            legDatabaseDao.getAllLegs().asFlow().collect {
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