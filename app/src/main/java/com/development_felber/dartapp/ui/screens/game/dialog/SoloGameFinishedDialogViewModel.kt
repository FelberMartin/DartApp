package com.development_felber.dartapp.ui.screens.game.dialog

import androidx.lifecycle.*
import com.development_felber.dartapp.data.persistent.database.Converters
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLeg
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLegDao
import com.development_felber.dartapp.data.repository.SettingsRepository
import com.development_felber.dartapp.data.repository.SettingsRepository.BooleanSetting
import com.development_felber.dartapp.ui.navigation.NavigationCommand
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.screens.game.GameViewModel
import com.development_felber.dartapp.util.WhileUiSubscribed
import com.development_felber.dartapp.util.graphs.filter.GamesLegFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SoloGameFinishedDialogViewModel(
    private val navigationManager: NavigationManager,
    private val databaseDao: FinishedLegDao,
    settingsRepository: SettingsRepository,
    private val callingViewModel: GameViewModel
) : ViewModel(){

    private val _leg = MutableStateFlow<FinishedLeg?>(null)
    val leg = _leg.asStateFlow()

    private val _last10GamesAverage = MutableStateFlow(0.0)
    val last10GamesAverage = _last10GamesAverage.asStateFlow()

    val showStats = settingsRepository
        .getBooleanSettingFlow(BooleanSetting.ShowStatsAfterLegFinished)
        .stateIn(viewModelScope, WhileUiSubscribed, false)

    init {
        viewModelScope.launch {
            databaseDao.getAllLegs().asFlow().collect { allLegs ->
                val sortedByDate = allLegs.sortedBy { Converters.toLocalDateTime(it.endTime) }
                _leg.value = sortedByDate.lastOrNull()
                val tenGamesBefore = GamesLegFilter("Temporary", 11).filterLegs(sortedByDate).dropLast(1)
                _last10GamesAverage.value = tenGamesBefore.map { leg -> leg.average }.average()
            }
        }
    }

    fun onMoreDetailsClicked() {
        callingViewModel.dismissGameFinishedDialog(temporary = true)
        navigationManager.navigate(NavigationCommand.ToHistoryDetails(leg.value!!.id))
    }
}