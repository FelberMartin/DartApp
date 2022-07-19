package com.example.dartapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dartapp.persistent.settings.InMemorySettingsStore
import com.example.dartapp.persistent.settings.options.AppearanceOption
import com.example.dartapp.ui.shared.Background
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.shared.RoundedTopAppBar
import com.example.dartapp.ui.theme.DartAppTheme
import com.example.dartapp.ui.values.Padding

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    Background {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            RoundedTopAppBar(
                title = "Settings"
            )

            Section(title = "Appearance") {

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
                modifier = Modifier.padding(vertical = Padding.MediumPadding, horizontal = Padding.LargePadding),
                color = MaterialTheme.colorScheme.background,
                content = content
            )
        }
    }
}

@Composable
private fun AppearanceSection(appearanceOption: AppearanceOption, onAppearanceOptionChange: (AppearanceOption) -> Unit)  {
    for (option in AppearanceOption.values()) {

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
        modifier = Modifier.fillMaxSize()
    ) {
        Row() {
            Text("Ask for Double")
            Switch(checked = askForDouble, onCheckedChange = onAskForDoubleChange)
        }
        Row() {
            Text("Ask for Checkout")
            Switch(checked = askForCheckout, onCheckedChange = onAskForCheckoutChange)
        }
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
fun SettingsScreenPreview() {
    val settingsViewModel = SettingsViewModel(InMemorySettingsStore())
    DartAppTheme() {
        SettingsScreen(viewModel = settingsViewModel)
    }
}
