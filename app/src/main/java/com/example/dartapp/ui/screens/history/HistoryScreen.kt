package com.example.dartapp.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.data.persistent.database.TestLegData
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.Background
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.shared.NavigationViewModel
import com.example.dartapp.ui.shared.RoundedTopAppBar
import com.example.dartapp.ui.theme.DartAppTheme

@Composable
fun HistoryScreen(
    viewModel: NavigationViewModel
) {
    Background {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            RoundedTopAppBar(
                title = "History",
                navigationViewModel = viewModel
            )

            Spacer(Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                val legs = TestLegData.createExampleLegs()
                for (leg in legs) {
                    HistoryItem(leg)
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(
    leg: Leg
) {
    MyCard() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

        }
    }
}

@Preview
@Composable
fun previewTableScreen() {
    DartAppTheme() {
        HistoryScreen(HistoryViewModel(NavigationManager()))
    }
}