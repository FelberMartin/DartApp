@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dartapp.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dartapp.data.persistent.database.Converters
import com.example.dartapp.data.persistent.database.FakeLegDatabaseDao
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.data.persistent.database.TestLegData
import com.example.dartapp.ui.navigation.NavigationDirections
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.screens.statistics.NoDataWarning
import com.example.dartapp.ui.shared.BackTopAppBar
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.theme.DartAppTheme
import com.example.dartapp.util.extensions.observeAsStateNonOptional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun HistoryScreenEntry(viewModel: HistoryViewModel) {
    val legs by viewModel.legs.observeAsStateNonOptional()
    HistoryScreen(legs = legs, viewModel = viewModel)
}

@Composable
fun HistoryScreen(
    legs: List<Leg>,
    viewModel: HistoryViewModel
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { BackTopAppBar(
            title = "History",
            navigationViewModel = viewModel,
            scrollBehavior = scrollBehavior
        )},
        content = { innerPadding ->
            if (legs.isEmpty()) {
                NoDataWarning("You first have to train to explore your history.")
            } else {
                LazyColumn(
                    contentPadding = innerPadding,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    for (leg in legs) {
                        item {
                            HistoryItem(
                                leg = leg,
                                onSeeMorePressed = {
                                    viewModel.navigate(NavigationDirections.HistoryDetails.navigationCommand(leg.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun HistoryItem(
    leg: Leg,
    onSeeMorePressed: (Leg) -> Unit
) {
    MyCard(
        onClick = { onSeeMorePressed(leg) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .padding(start = 8.dp)
        ) {
            val date = Converters.toLocalDateTime(leg.endTime)
            WeekDayLabel(date)
            DateAndTimeLabels(date)
            LegShortInfo(leg)
            SeeMoreIconButton {
                onSeeMorePressed(leg)
            }
        }
    }
}

@Composable
fun WeekDayLabel(
    date: LocalDateTime
) {
    val weekdayFormatter = DateTimeFormatter.ofPattern("EEE")

    Text(
        text = weekdayFormatter.format(date),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun DateAndTimeLabels(
    date: LocalDateTime
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dateFormatter.format(date),
            style = MaterialTheme.typography.labelMedium
        )

        Text(
            text = timeFormatter.format(date),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun LegShortInfo(
    leg: Leg
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val average = leg.servesAvg
        Text(
            text = "Avg: ${String.format("%.2f", average)}",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelMedium
        )

        Text(
            text = "501",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun SeeMoreIconButton(
    onIconButtonPressed: () -> Unit
) {
    IconButton(
        onClick = onIconButtonPressed,
    ) {
        Icon(
            imageVector = Icons.Default.NavigateNext,
            contentDescription = "Details",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun PreviewTableScreen() {
    DartAppTheme() {
        val viewModel = HistoryViewModel(NavigationManager(), FakeLegDatabaseDao(fillWithTestData = true))
        val legs = TestLegData.createExampleLegs()
        HistoryScreen(legs, viewModel)
    }
}