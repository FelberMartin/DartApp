@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.dartapp.ui.screens.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
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
                HistoryScreenContent(innerPadding, legs, viewModel)
            }
        }
    )
}

@Composable
private fun HistoryScreenContent(
    innerPadding: PaddingValues,
    legs: List<Leg>,
    viewModel: HistoryViewModel
) {
    // TODO: Animate the reorder transition. For some reason this does not work, see TestApplication project.
    LazyColumn(
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        item {
            SortChipGroup(viewModel = viewModel)
        }

        item { Spacer(Modifier.height(12.dp)) }

        items(legs, key = { it.id }) { leg ->
            HistoryItem(
                leg = leg,
                onSeeMorePressed = {
                    viewModel.navigate(NavigationDirections.HistoryDetails.navigationCommand(leg.id))
                }
            )
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun SortChipGroup(
    viewModel: HistoryViewModel
) {
    val selectedSortType by viewModel.selectedSortType.observeAsStateNonOptional()
    val sortDescending by viewModel.sortDescending.observeAsStateNonOptional()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        SortType.all.forEachIndexed { index, sortType ->
            val selected = sortType == selectedSortType
            val descending = if (selected) sortDescending else sortType.byDefaultDescending
            item {
                FilterChip(
                    selected = selected,
                    onClick = {
                        if (selected) {
                            viewModel.setSortDescending(!sortDescending)
                        } else {
                            viewModel.setSelectedSortType(sortType)
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (descending) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    },
                    label = {
                        Text(text = sortType.name)
                    },
                )
            }
        }
    }
}


@Composable
private fun LazyItemScope.HistoryItem(
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
                .padding(start = 24.dp)
                .animateItemPlacement()
        ) {
            val date = Converters.toLocalDateTime(leg.endTime)
            DartsCounter(leg.dartCount)

            Column(
                modifier = Modifier.width(130.dp)
            ) {
                DateAndTimeLabels(date)
                LegShortInfo(leg)
            }

            SeeMoreIconButton {
                onSeeMorePressed(leg)
            }
        }
    }
}

@Composable
fun DartsCounter(
    dartCount: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$dartCount",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Darts",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }

}

@Composable
private fun DateAndTimeLabels(
    date: LocalDateTime
) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = timeFormatter.format(date),
            style = MaterialTheme.typography.labelMedium
        )

        Text(
            text = dateFormatter.format(date),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun LegShortInfo(
    leg: Leg
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Average:",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = String.format("%.1f", leg.average),
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