package com.development_felber.dartapp.ui.screens.game.dialog

import androidx.lifecycle.*
import com.development_felber.dartapp.data.persistent.database.leg.Leg
import com.development_felber.dartapp.data.persistent.database.leg.LegDao
import com.development_felber.dartapp.data.repository.SettingsRepository
import com.development_felber.dartapp.data.repository.SettingsRepository.BooleanSetting
import com.development_felber.dartapp.ui.navigation.NavigationCommand
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.screens.game.GameViewModel
import com.development_felber.dartapp.util.graphs.filter.GamesLegFilter
import kotlinx.coroutines.launch

class LegFinishedDialogViewModel(
    private val navigationManager: NavigationManager,
    val leg: Leg,
    private val databaseDao: LegDao,
    private val settingsRepository: SettingsRepository,
    private val callingViewModel: GameViewModel
) : ViewModel(){

    private val _last10GamesAverage = MutableLiveData(0.0)
    val last10GamesAverage: LiveData<Double> = _last10GamesAverage

    val showStats: LiveData<Boolean> = settingsRepository
        .getBooleanSettingFlow(BooleanSetting.ShowStatsAfterLegFinished).asLiveData()


    init {
        viewModelScope.launch {
            databaseDao.getAllLegs().asFlow().collect { allLegs ->
                // Do not include the just finished leg (which is at the end of the list).
                val tenGamesBefore = GamesLegFilter("Temporary", 11).filterLegs(allLegs).dropLast(1)
                _last10GamesAverage.value = tenGamesBefore.map { leg -> leg.average }.average()
            }
        }
    }

    fun onMoreDetailsClicked() {
        callingViewModel.dismissLegFinishedDialog(temporary = true)
        navigationManager.navigate(NavigationCommand.ToHistoryDetails(leg.id))
    }
}