@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.dartapp.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.dartapp.R
import com.example.dartapp.ui.navigation.NavigationDirections
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.Background
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.shared.extensions.withDropShadow
import com.example.dartapp.ui.theme.DartAppTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    Background {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp).padding(top = 24.dp, bottom = 12.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            SettingsRow(onSettingsClicked = { viewModel.navigate(NavigationDirections.Settings) })
            StatisticsCard(viewModel)
            AppIconAndName()
            PlayButtonAndModeSelection(onPlayClicked = { viewModel.navigate(NavigationDirections.Game) })
        }
    }


}

@Composable
private fun SettingsRow(
    onSettingsClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        IconButton(
            onClick = onSettingsClicked,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun StatisticsCard(
    viewModel: HomeViewModel
) {
    MyCard {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Average"
            )

            Image(
                painter = painterResource(id = R.drawable.graph_placeholder),
                contentDescription = "Preview of statistics",
                modifier = Modifier.height(240.dp)
            )

            OutlinedButton(
                onClick = { viewModel.navigate(NavigationDirections.Statistics) }
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
private fun AppIconAndName() {
    Row(
        modifier = Modifier.height(62.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ElevatedCard(elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)) {
            Image(
                painter = painterResource(id = R.drawable.dartappicon),
                contentDescription = "App Icon",
                contentScale = ContentScale.FillHeight
            )
        }

        Text(
            text = "Dart Stats",
            style = MaterialTheme.typography.titleLarge.withDropShadow(),
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun PlayButtonAndModeSelection(
    onPlayClicked: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = onPlayClicked,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(44.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 5.dp)
        ) {
            Text(
                text = "Play",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        SoloModeInformation()
    }
}

@Composable
private fun SoloModeInformation() {
    var showInfo by remember { mutableStateOf(false) }

    Box(Modifier.height(60.dp)) {
        TextButton(
            onClick = { showInfo = true },
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
                onDismissRequest = { showInfo = false }
            ) {
                Box(Modifier
                    .clickable { showInfo = false }
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
        HomeScreen(
            viewModel = HomeViewModel(NavigationManager())
        )
    }
}