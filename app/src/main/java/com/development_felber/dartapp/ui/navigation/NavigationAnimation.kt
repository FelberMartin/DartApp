package com.development_felber.dartapp.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

object NavigationAnimation {

    const val DEFAULT_DURATION = 500

    val defaultEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { it / 2 },
            animationSpec = tween(
                durationMillis = DEFAULT_DURATION,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(animationSpec = tween(DEFAULT_DURATION))
    }

    val defaultExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { -it/2 },
            animationSpec = tween(
                durationMillis = DEFAULT_DURATION,
                easing = FastOutSlowInEasing
            )
        ) + fadeOut(animationSpec = tween(DEFAULT_DURATION))
    }

    val defaultPopEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { -it/2 },
            animationSpec = tween(
                durationMillis = DEFAULT_DURATION,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(animationSpec = tween(DEFAULT_DURATION))
    }

    val defaultPopExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { it/2 },
            animationSpec = tween(
                durationMillis = DEFAULT_DURATION,
                easing = FastOutSlowInEasing
            )
        ) + fadeOut(animationSpec = tween(DEFAULT_DURATION))
    }
}