package com.example.dartapp.ui.navigation.command

import androidx.navigation.NavController

abstract class NavigationCommand {

    var executed = false
        private set

    fun navigateOnceWith(navController: NavController) {
        if (!executed) {
            navigateWith(navController)
            executed = true
        }
    }

    protected abstract fun navigateWith(navController: NavController)

    companion object {
        val DO_NOTHING = object : NavigationCommand() {
            override fun navigateWith(navController: NavController) {}
        }

        val NAVIGATE_UP = object : NavigationCommand() {
            override fun navigateWith(navController: NavController) {
                navController.navigateUp()
            }
        }
    }
}