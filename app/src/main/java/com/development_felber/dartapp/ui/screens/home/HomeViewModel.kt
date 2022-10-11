package com.development_felber.dartapp.ui.screens.home

import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.shared.NavigationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    navigationManager: NavigationManager
) : NavigationViewModel(navigationManager) {


}