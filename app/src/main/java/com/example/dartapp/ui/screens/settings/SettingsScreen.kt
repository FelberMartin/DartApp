@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dartapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dartapp.data.AppearanceOption
import com.example.dartapp.data.persistent.keyvalue.InMemoryKeyValueStorage
import com.example.dartapp.data.repository.SettingsRepository
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.BackTopAppBar
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.theme.DartAppTheme
import com.example.dartapp.ui.values.Padding

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val appearanceOption = viewModel.appearanceOption.observeAsState(AppearanceOption.SYSTEM)
    val askForDouble = viewModel.askForDouble.observeAsState(true)
    val askForCheckout = viewModel.askForCheckout.observeAsState(true)
    val showStatsAfterLeg = viewModel.showStatsAfterLeg.observeAsState(initial = true)

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { BackTopAppBar(
            title = "Settings",
            navigationViewModel = viewModel,
            scrollBehavior = scrollBehavior
        )},
        content = { innerPadding ->
            LazyColumn(contentPadding = innerPadding) {
                item { Section(title = "Appearance") {
                    AppearanceSection(
                        appearanceOption = appearanceOption,
                        onAppearanceOptionChange = viewModel::changeAppearanceOption
                    )
                }}

                item { Section(title = "Training") {
                    TrainingSection(
                        askForDouble = askForDouble,
                        onAskForDoubleChange = viewModel::changeAskForDouble,
                        askForCheckout = askForCheckout,
                        onAskForCheckoutChange = viewModel::changeAskForCheckout,
                        showStatsAfterLeg = showStatsAfterLeg,
                        onShowStatsAfterLegChange = viewModel::changeShowStatsAfterLeg
                    )
                }}
            }
        }
    )
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Padding.MediumPadding)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(10.dp)
        )

        MyCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Surface(
                modifier = Modifier.padding(vertical = Padding.MediumPadding, horizontal = Padding.MediumPadding),
                color = MaterialTheme.colorScheme.background,
                content = content
            )
        }
    }
}

@Composable
private fun AppearanceSection(
    appearanceOption: State<AppearanceOption>,
    onAppearanceOptionChange: (AppearanceOption) -> Unit
)  {
    Column() {
        for (option in AppearanceOption.values()) {
            val selected = option == appearanceOption.value
            AppearanceOption(onAppearanceOptionChange, option, selected)
        }
    }

}

@Composable
private fun AppearanceOption(
    onAppearanceOptionChange: (AppearanceOption) -> Unit,
    option: AppearanceOption,
    selected: Boolean
) {
    TextButton(
        onClick = { onAppearanceOptionChange(option) },
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = option.text,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .weight(1f)
            )

            RadioButton(
                selected = selected,
                onClick = null,  // Handled in Row
            )
        }
    }

}


@Composable
private fun TrainingSection(
    askForDouble: State<Boolean>,
    onAskForDoubleChange: (Boolean) -> Unit,
    askForCheckout: State<Boolean>,
    onAskForCheckoutChange: (Boolean) -> Unit,
    showStatsAfterLeg: State<Boolean>,
    onShowStatsAfterLegChange: (Boolean) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BooleanOption(
            text = "Ask for double attempts",
            info = "When disabled, will not calculate a double rate for trainings.",
            enabled = askForDouble.value,
            onEnabledChange = onAskForDoubleChange
        )
        BooleanOption(
            text = "Ask for checkout",
            info = "When disabled, defaults to three darts thrown in the last serve.",
            enabled = askForCheckout.value,
            onEnabledChange = onAskForCheckoutChange
        )

        Divider(Modifier.padding(4.dp))

        BooleanOption(
            text = "Show statistics after Leg",
            info = "Whether to show some statistics after finishing a leg.",
            enabled = showStatsAfterLeg.value,
            onEnabledChange = onShowStatsAfterLegChange
        )
    }
}

@Composable
private fun BooleanOption(
    text: String,
    info: String,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit
) {
    TextButton(
        onClick = { onEnabledChange(!enabled) },
        shape = MaterialTheme.shapes.medium)
    {
        Column() {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .weight(1f)
                )

                Switch(
                    checked = enabled,
                    onCheckedChange = null, // Handled in column
                    modifier = Modifier.scale(0.8f)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = info,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
fun SettingsScreenPreview() {
    val settingsViewModel = SettingsViewModel(SettingsRepository(InMemoryKeyValueStorage()), NavigationManager())
    DartAppTheme() {
        SettingsScreen(viewModel = settingsViewModel)
    }
}
