package com.example.dartapp.ui.navigation.command

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController

open class NavigationDirectionCommand(
    val arguments: List<NamedNavArgument>,
    val destination: String
) : NavigationCommand() {

    override fun navigateWith(navController: NavController) {
        navController.navigate(destination)
    }
}