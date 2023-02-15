package com.development_felber.dartapp.ui.screens.home.dialogs.new_feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.development_felber.dartapp.data.persistent.database.finished_leg.FinishedLegDao
import com.development_felber.dartapp.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MultiplayerAddedDialogViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val finishedLegDao: FinishedLegDao,
) : ViewModel() {

    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    init {
        viewModelScope.launch {
            val hasShownDialog = settingsRepository.getBooleanSettingFlow(SettingsRepository.BooleanSetting.HasShownMultiplayerAddedDialog).first()
            val hasFinishedLegs = finishedLegDao.getAllLegs().asFlow().first().isNotEmpty()
            _showDialog.value = !hasShownDialog && hasFinishedLegs == true
        }
    }

    fun dismissDialog() {
        _showDialog.value = false
        viewModelScope.launch {
            settingsRepository.setBooleanSetting(SettingsRepository.BooleanSetting.HasShownMultiplayerAddedDialog, true)
        }
    }
}