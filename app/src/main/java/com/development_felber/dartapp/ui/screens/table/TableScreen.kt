@file:OptIn(ExperimentalMaterial3Api::class)

package com.development_felber.dartapp.ui.screens.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.development_felber.dartapp.data.persistent.database.finished_leg.FakeFinishedLegDao
import com.development_felber.dartapp.ui.navigation.NavigationManager
import com.development_felber.dartapp.ui.shared.BackTopAppBar
import com.development_felber.dartapp.ui.shared.MyCard
import com.development_felber.dartapp.ui.theme.DartAppTheme
import com.development_felber.dartapp.util.extensions.observeAsStateNonOptional

@Composable
fun TableScreen(
    viewModel: TableViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { BackTopAppBar(
            title = "Table",
            onBackClicked = viewModel::navigateBack,
            scrollBehavior = scrollBehavior
        )},
        content = { innerPadding ->

            val totalItems by viewModel.totalItems.observeAsStateNonOptional()
            val averageItems by viewModel.averageItems.observeAsStateNonOptional()
            val distributionItems by viewModel.distributionItems.observeAsStateNonOptional()

            LazyColumn(contentPadding = innerPadding) {
                item { Spacer(Modifier.height(18.dp)) }

                item { Table(
                        header = "Totals",
                        items = totalItems
                    )
                }
                item { Table(
                        header = "Averages",
                        items = averageItems
                    )
                }
                item { Table(
                        header = "Serve Distribution",
                        items = distributionItems
                    )
                }

                item { Spacer(Modifier.height(16.dp)) }
            }

        }
    )
}

@Composable
private fun Table(
    header: String,
    items: List<Pair<String, String>>
) {
    MyCard(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = header,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            Spacer(Modifier.height(4.dp))

            for (item in items) {
                TableItem(item)
            }

            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun TableItem(
    item: Pair<String, String>
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Text(
            text = item.first,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = item.second,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Right,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun PreviewTableScreen() {
    DartAppTheme() {
        TableScreen(viewModel = TableViewModel(NavigationManager(), FakeFinishedLegDao(fillWithTestData = true)))
    }
}

