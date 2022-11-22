package com.development_felber.dartapp.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

@OptIn(ExperimentalAnimationApi::class)
object NavigationAnimation {

    val DEFAULT_DURATION = 500

    val defaultEnter: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { it / 2 },
            animationSpec = tween(
                durationMillis = DEFAULT_DURATION,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(animationSpec = tween(DEFAULT_DURATION))
    }

    val defaultExit: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { -it/2 },
            animationSpec = tween(
                durationMillis = DEFAULT_DURATION,
                easing = FastOutSlowInEasing
            )
        ) + fadeOut(animationSpec = tween(DEFAULT_DURATION))
    }

    val defaultPopEnter: AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { -it/2 },
            animationSpec = tween(
                durationMillis = DEFAULT_DURATION,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(animationSpec = tween(DEFAULT_DURATION))
    }

    val defaultPopExit: AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { it/2 },
            animationSpec = tween(
                durationMillis = DEFAULT_DURATION,
                easing = FastOutSlowInEasing
            )
        ) + fadeOut(animationSpec = tween(DEFAULT_DURATION))
    }


}