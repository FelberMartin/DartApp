package com.example.dartapp.ui.screens.statistics

import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    navigationManager: NavigationManager
): NavigationViewModel(navigationManager) {


}