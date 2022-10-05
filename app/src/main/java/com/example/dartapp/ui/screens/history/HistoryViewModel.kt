package com.example.dartapp.ui.screens.history

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
class HistoryViewModel @Inject constructor(
    navigationManager: NavigationManager,
    private val legDatabaseDao: LegDatabaseDao
) : NavigationViewModel(navigationManager){

    private val _legs = MutableLiveData<List<Leg>>(listOf())
    val legs: LiveData<List<Leg>> = _legs

    private val _selectedSortType = MutableLiveData<SortType>(SortType.PlaceHolderSortType)
    val selectedSortType: LiveData<SortType> = _selectedSortType

    private val _sortDescending = MutableLiveData<Boolean>(SortType.PlaceHolderSortType.byDefaultDescending)
    val sortDescending: LiveData<Boolean> = _sortDescending


    init {
        setSelectedSortType(SortType.DateSortType)

        viewModelScope.launch {
            legDatabaseDao.getAllLegs().asFlow().collect {
                _legs.value = it
                sortLegs()
            }
        }
    }

    fun setSortDescending(descending: Boolean) {
        _sortDescending.value = descending
        sortLegs()
    }

    fun setSelectedSortType(sortType: SortType) {
        _selectedSortType.value = sortType
        setSortDescending(sortType.byDefaultDescending)
    }

    private fun sortLegs() {
        _legs.value = _selectedSortType.value!!.sortLegs(legs.value!!, sortDescending.value!!)
    }


}