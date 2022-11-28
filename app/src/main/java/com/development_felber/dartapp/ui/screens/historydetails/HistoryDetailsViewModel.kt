package com.development_felber.dartapp.ui.screens.historydetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.development_felber.dartapp.data.persistent.database.leg.Leg
import com.development_felber.dartapp.data.persistent.database.leg.LegDao
import com.development_felber.dartapp.ui.navigation.NavigationCommand
import com.development_felber.dartapp.ui.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryDetailsViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val databaseDao: LegDao,
) : ViewModel(){

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

    fun navigateBack() {
        navigationManager.navigate(NavigationCommand.Back)
    }
}