package com.example.dartapp.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dartapp.persistent.settings.InMemorySettingsStore
import com.example.dartapp.persistent.settings.options.AppearanceOption
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.Background
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.shared.RoundedTopAppBar
import com.example.dartapp.ui.theme.DartAppTheme
import com.example.dartapp.ui.values.Padding

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    Background {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            RoundedTopAppBar(
                title = "Settings",
                navigationViewModel = viewModel
            )

            Section(title = "Appearance") {
                AppearanceSection(
                    appearanceOption = viewModel.appearanceOption,
                    onAppearanceOptionChange = viewModel::changeAppearanceOption
                )
            }

            Section(title = "Training") {
                TrainingSection(
                    askForDouble = viewModel.askForDouble,
                    onAskForDoubleChange = viewModel::changeAskForDouble,
                    askForCheckout = viewModel.askForCheckout,
                    onAskForCheckoutChange = viewModel::changeAskForCheckout
                )
            }
        }
    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppearanceSection(
    appearanceOption: AppearanceOption,
    onAppearanceOptionChange: (AppearanceOption) -> Unit
)  {
    Column() {
        for (option in AppearanceOption.values()) {
            val selected = option == appearanceOption
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onAppearanceOptionChange(option)
                }
            ) {
                Text(
                    text = option.text,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 10.dp)
                        .padding(start = Padding.MediumPadding)
                )

                RadioButton(
                    selected = selected,
                    onClick = { /* Handled in row */ }
                )
            }
        }
    }

}


@Composable
private fun TrainingSection(
    askForDouble: Boolean,
    onAskForDoubleChange: (Boolean) -> Unit,
    askForCheckout: Boolean,
    onAskForCheckoutChange: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BooleanOption(
            text = "Ask for double attempts",
            info = "When disabled, will not calculate a double rate for trainings.",
            enabled = askForDouble,
            onEnabledChange = onAskForDoubleChange
        )
        BooleanOption(
            text = "Ask for checkout",
            info = "When disabled, defaults to three darts thrown in the last serve.",
            enabled = askForCheckout,
            onEnabledChange = onAskForCheckoutChange
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
    Column(
        modifier = Modifier.clickable {
            onEnabledChange(!enabled)
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = Padding.MediumPadding)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp)
            )

            Switch(
                checked = enabled,
                onCheckedChange = {  /* Handled in column */ },
                modifier = Modifier.scale(0.7f)
            )
        }
        Text(
            text = info,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = Padding.MediumPadding)
        )
    }

}

@Preview(widthDp = 360, heightDp = 800)
@Composable
fun SettingsScreenPreview() {
    val settingsViewModel = SettingsViewModel(InMemorySettingsStore(), NavigationManager())
    DartAppTheme() {
        SettingsScreen(viewModel = settingsViewModel)
    }
}
