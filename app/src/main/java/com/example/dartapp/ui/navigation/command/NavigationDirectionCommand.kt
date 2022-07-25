package com.example.dartapp.ui.navigation.command

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController

class NavigationDirectionCommand(
    val destination: String,
    val arguments: List<NamedNavArgument>
) : NavigationCommand {

    override fun navigateWith(navController: NavController) {
        navController.navigate(destination)
    }
}