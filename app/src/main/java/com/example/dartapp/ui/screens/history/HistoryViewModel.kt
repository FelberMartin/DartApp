package com.example.dartapp.ui.screens.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.data.persistent.database.LegDatabaseDao
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.NavigationViewModel
import com.example.dartapp.util.categorized_sort.DateCategorizedSortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    navigationManager: NavigationManager,
    private val legDatabaseDao: LegDatabaseDao
) : NavigationViewModel(navigationManager){

    private val _categorizedLegsResult = MutableLiveData(CategorizedSortTypeBase.Result())
    val categorizedLegsResult: LiveData<CategorizedSortTypeBase.Result> = _categorizedLegsResult

    private val _selectedCategorizedSortType = MutableLiveData<CategorizedSortTypeBase>(CategorizedSortTypeBase.PlaceHolder)
    val selectedCategorizedSortType: LiveData<CategorizedSortTypeBase> = _selectedCategorizedSortType

    private val _sortDescending = MutableLiveData<Boolean>(CategorizedSortTypeBase.PlaceHolder.byDefaultDescending)
    val sortDescending: LiveData<Boolean> = _sortDescending

    private var legs: List<Leg> = listOf()

    init {
        setSelectedSortType(DateCategorizedSortType)

        viewModelScope.launch {
            legDatabaseDao.getAllLegs().asFlow().collect {
                legs = it
                sortLegs()
            }
        }
    }

    fun setSortDescending(descending: Boolean) {
        _sortDescending.value = descending
        sortLegs()
    }

    fun setSelectedSortType(categorizedSortType: CategorizedSortTypeBase) {
        _selectedCategorizedSortType.value = categorizedSortType
        setSortDescending(categorizedSortType.byDefaultDescending)
    }

    private fun sortLegs() {
        val result = _selectedCategorizedSortType.value!!.sortLegsCategorized(legs, sortDescending.value!!)
        _categorizedLegsResult.value = result
    }


}