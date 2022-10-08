package com.example.dartapp.ui.screens.game.dialog

import androidx.lifecycle.*
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.data.persistent.database.LegDatabaseDao
import com.example.dartapp.data.repository.SettingsRepository
import com.example.dartapp.data.repository.SettingsRepository.BooleanSetting
import com.example.dartapp.ui.navigation.NavigationDirections
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.NavigationViewModel
import com.example.dartapp.util.graphs.filter.GamesLegFilter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LegFinishedDialogViewModel(
    private val navigationManager: NavigationManager,
    val leg: Leg,
    private val databaseDao: LegDatabaseDao,
    private val settingsRepository: SettingsRepository
) : NavigationViewModel(
    navigationManager
){

    private val _last10GamesAverage = MutableLiveData(0.0)
    val last10GamesAverage: LiveData<Double> = _last10GamesAverage

    var showStats = settingsRepository.getBooleanSettingFlow(BooleanSetting.ShowStatsAfterLegFinished).asLiveData()
    var showServeDistribution = true
    var showAverage = true
    var showDartCount = true
    var showDoubleRate = true
    var showCheckout = true
    var showDetailsLinkButton = true

    init {
        viewModelScope.launch {
            databaseDao.getAllLegs().asFlow().collect { allLegs ->
                // Do not include the just finished leg (which is at the end of the list).
                val tenGamesBefore = GamesLegFilter("Temporary", 11).filterLegs(allLegs).dropLast(1)
                _last10GamesAverage.value = tenGamesBefore.map { leg -> leg.average }.average()
            }
            
            initVariablesFromSettings()
        }
    }
    
    private suspend fun initVariablesFromSettings() {
        showStats = settingsRepository.getBooleanSettingFlow(BooleanSetting.ShowStatsAfterLegFinished).first()
        showServeDistribution = settingsRepository.getBooleanSettingFlow(BooleanSetting.ShowServeDistribution).first()
        showAverage = settingsRepository.getBooleanSettingFlow(BooleanSetting.ShowAverage).first()
        showDartCount = settingsRepository.getBooleanSettingFlow(BooleanSetting.ShowDartCount).first()
        showDoubleRate = settingsRepository.getBooleanSettingFlow(BooleanSetting.ShowDoubleRate).first()
        showCheckout = settingsRepository.getBooleanSettingFlow(BooleanSetting.ShowCheckout).first()
        showDetailsLinkButton = settingsRepository.getBooleanSettingFlow(BooleanSetting.ShowDetailsLinkButton).first()
    }

    fun onMoreDetailsClicked() {
        navigate(NavigationDirections.HistoryDetails.navigationCommand(leg.id))
    }
}