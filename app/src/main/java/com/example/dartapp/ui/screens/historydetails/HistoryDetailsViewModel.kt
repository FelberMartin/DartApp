package com.example.dartapp.ui.screens.historydetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.data.persistent.database.LegDatabaseDao
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryDetailsViewModel @Inject constructor(
    navigationManager: NavigationManager,
    private val databaseDao: LegDatabaseDao,
) : NavigationViewModel(navigationManager){

    private val _leg = MutableLiveData<Leg?>(null)
    val leg: LiveData<Leg?> = _leg

    fun setLegId(legId: Long) {
        fetchLegFromDatabase(legId)
    }

    private fun fetchLegFromDatabase(legId: Long) {
        viewModelScope.launch {
            _leg.value = databaseDao.get(legId)
        }
    }
}