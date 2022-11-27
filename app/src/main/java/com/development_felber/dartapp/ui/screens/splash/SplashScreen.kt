package com.development_felber.dartapp.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.development_felber.dartapp.R
import com.development_felber.dartapp.ui.theme.DartAppTheme
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplashScreen(
    viewModel: SplashViewModel = hiltViewModel()
) {
    val scale = remember {
        Animatable(0f)
    }
    val alpha = remember {
        Animatable(0f)
    }

    val startDelay = 0L
    val duration = 900L
    LaunchedEffect(key1 = true, block = {
        delay(startDelay)
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = duration.toInt(),
                easing = EaseInOutCubic
            ),
        )
        delay(duration)
        viewModel.onSplashFinished()
    })

    LaunchedEffect(key1 = true, block = {
        delay(startDelay)
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = duration.toInt(),
            )
        )
    })

    SplashScreen(
        scale = scale.value,
        alpha = alpha.value
    )
}

@Composable
private fun SplashScreen(scale: Float = 1f, alpha: Float = 1f) {

    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .scale(scale)
            .alpha(alpha)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_icon_foreground_white),
                contentDescription = "AppIcon",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(96.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "My Dart Stats",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


@Preview
@Composable
fun SplashScreenPreview() {
    DartAppTheme() {
        SplashScreen()
    }
}