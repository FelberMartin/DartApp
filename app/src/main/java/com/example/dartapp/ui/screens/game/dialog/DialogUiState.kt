package com.example.dartapp.ui.screens.game.dialog

data class DialogUiState(
    var exitDialogOpen: Boolean = false,
    var simpleDoubleAttemptsDialogOpen: Boolean = false,
    var doubleAttemptsDialogOpen: Boolean = false,
    var checkoutDialogOpen: Boolean = false,
) {
    fun anyDialogOpen() : Boolean {
        return exitDialogOpen || simpleDoubleAttemptsDialogOpen || doubleAttemptsDialogOpen || checkoutDialogOpen
    }
}