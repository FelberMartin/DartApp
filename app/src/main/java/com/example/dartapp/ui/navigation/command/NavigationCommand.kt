package com.example.dartapp.ui.navigation.command

import androidx.navigation.NavController

interface NavigationCommand {

    fun navigateWith(navController: NavController)

    companion object {
        val DO_NOTHING = object : NavigationCommand {
            override fun navigateWith(navController: NavController) {}
        }

        val NAVIGATE_UP = object : NavigationCommand {
            override fun navigateWith(navController: NavController) {
                navController.navigateUp()
            }
        }
    }
}