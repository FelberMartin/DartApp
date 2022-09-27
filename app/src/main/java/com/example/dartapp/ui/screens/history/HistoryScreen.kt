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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dartapp.data.persistent.database.Converters
import com.example.dartapp.data.persistent.database.FakeLegDatabaseDao
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.ui.navigation.NavigationDirections
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.Background
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.shared.RoundedTopAppBar
import com.example.dartapp.ui.theme.DartAppTheme
import com.example.dartapp.util.extensions.observeAsStateNonOptional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel
) {
    Background {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            RoundedTopAppBar(
                title = "History",
                navigationViewModel = viewModel
            )

            val legs by viewModel.legs.observeAsStateNonOptional()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                item { Spacer(Modifier.height(32.dp)) }

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
        HistoryScreen(HistoryViewModel(NavigationManager(), FakeLegDatabaseDao(fillWithTestData = true)))
    }
}