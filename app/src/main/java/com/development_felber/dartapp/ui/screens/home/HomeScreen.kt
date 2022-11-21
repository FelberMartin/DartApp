@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.development_felber.dartapp.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.development_felber.dartapp.R
import com.development_felber.dartapp.data.persistent.database.leg.FakeLegDao
import com.development_felber.dartapp.ui.navigation.NavigationDirections
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.screens.statistics.StatisticsChart
import com.development_felber.dartapp.ui.screens.statistics.StatisticsViewModel
import com.development_felber.dartapp.ui.shared.Background
import com.development_felber.dartapp.ui.shared.MyCard
import com.development_felber.dartapp.ui.shared.TowSegmentSingleSelectButton
import com.development_felber.dartapp.ui.shared.extensions.withDropShadow
import com.development_felber.dartapp.ui.theme.DartAppTheme

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    statisticsViewModel: StatisticsViewModel
) {
    var easterEggActivated by remember { mutableStateOf(false) }
    val isMultiplayer by homeViewModel.isMultiplayer.collectAsState()

    Background {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
//                .padding(top = 24.dp, bottom = 12.dp)
                .fillMaxSize(),
        ) {
            SettingsRow(onSettingsClicked = { homeViewModel.navigate(NavigationDirections.Settings) })
            StatisticsCard(homeViewModel, statisticsViewModel)
            AppIconAndName(
                onToggleEasterEgg = { easterEggActivated = !easterEggActivated }
            )
            PlayButtonAndModeSelection(
                onTrainClicked = homeViewModel::onTrainClicked,
                easterEggActivated = easterEggActivated,
                isMultiplayer = isMultiplayer,
                onMultiplayerChange = homeViewModel::setMultiplayer
            )
        }
    }
}

@Composable
private fun SettingsRow(
    onSettingsClicked: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = onSettingsClicked,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun StatisticsCard(
    homeViewModel: HomeViewModel,
    statisticsViewModel: StatisticsViewModel
) {
    MyCard {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatisticsPreview(statisticsViewModel)

            FilledTonalButton(
                onClick = { homeViewModel.navigate(NavigationDirections.Statistics) }
            ) {
                Icon(
                    imageVector = Icons.Filled.StackedLineChart,
                    contentDescription = null
                )
                Text(
                    text = "more Stats",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun StatisticsPreview(
    statisticsViewModel: StatisticsViewModel,
) {
    Text(
        text = "Average"
    )

    Box(
        modifier = Modifier
            .scale(0.8f)
            .fillMaxHeight(0.35f)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        StatisticsChart(statisticsViewModel, interactionEnabled = false)
    }
}

@Composable
private fun AppIconAndName(
    onToggleEasterEgg: () -> Unit = {}
) {
    val lastClicks by remember { mutableStateOf(mutableListOf<Long>()) }
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            lastClicks.add(System.currentTimeMillis())
            val last5Clicks = lastClicks.takeLast(5)
            val deltaMillis = last5Clicks.last() - last5Clicks.first()
            if (last5Clicks.size == 5 && deltaMillis < 1000) {
                onToggleEasterEgg()
                lastClicks.clear()
            }
        }
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_icon_foreground_white),
            contentDescription = "App Icon",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .size(64.dp)
                .clip(MaterialTheme.shapes.medium),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
        )

        Text(
            text = "My Dart Stats",
            style = MaterialTheme.typography.titleLarge.withDropShadow(),
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun PlayButtonAndModeSelection(
    onTrainClicked: () -> Unit,
    easterEggActivated: Boolean,
    isMultiplayer: Boolean,
    onMultiplayerChange: (Boolean) -> Unit
) {
    val trainEmojis = listOf("🚆", "🚅", "🚄", "🚂", "🚉")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = onTrainClicked,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(44.dp),
//            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 5.dp)
        ) {
            Text(
                text = "${if (easterEggActivated) trainEmojis.random() else ""}Train",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        SoloMultiplayerSelection(
            isSolo = !isMultiplayer, onChange = { onMultiplayerChange(!it) }
        )
//        SoloModeInformation()
    }
}

@Composable
fun SoloMultiplayerSelection(
    isSolo: Boolean,
    onChange: (Boolean) -> Unit,
) {
    TowSegmentSingleSelectButton(
        text1 = "Solo",
        text2 = "Multi",
        selectedIndex = if (isSolo) 0 else 1,
        onSelectedIndexChange =  { onChange(it == 0) },
        modifier = Modifier.scale(0.8f)
    )
}

@Composable
private fun SoloModeInformation() {
    var showInfo by remember { mutableStateOf(false) }
    var lastDismissTime by remember { mutableStateOf(0L) }

    Box(Modifier.height(60.dp)) {
        TextButton(
            onClick = {
                val delta = System.currentTimeMillis() - lastDismissTime
                val clickCausedByDismissClick = delta < 100
                if (!clickCausedByDismissClick) {
                    showInfo = !showInfo
                }
            },
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Solo",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(start = 6.dp, top = 2.dp)
                        .size(18.dp)
                )
            }
        }

        if (showInfo) {
            Popup(
                alignment = Alignment.BottomCenter,
                onDismissRequest = { showInfo = false; lastDismissTime = System.currentTimeMillis() },
            ) {
                Box(Modifier
                    .clickable {
                        showInfo = false; lastDismissTime = System.currentTimeMillis()
                    }
                    .background(
                        color = MaterialTheme.colorScheme.inverseSurface,
                        shape = MaterialTheme.shapes.medium
                    )) {
                    Text(
                        text = "Multiplayer coming soon™",
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 380, heightDp = 780)
@Composable
fun DefaultPreview() {
    DartAppTheme {
        val navManager = NavigationManager()
        val homeViewModel = HomeViewModel(navManager)
        val statisticsViewModel = StatisticsViewModel(navManager, FakeLegDao(fillWithTestData = true))
        HomeScreen(homeViewModel, statisticsViewModel)
    }
}