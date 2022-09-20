package com.example.dartapp.ui.screens.statistics

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dartapp.R
import com.example.dartapp.ui.navigation.NavigationDirections
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.Background
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.shared.RoundedTopAppBar
import com.example.dartapp.ui.theme.DartAppTheme

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel
) {
    Background {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            RoundedTopAppBar(
                title = "Statistics",
                navigationViewModel = viewModel
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp)
            ) {
                MainStatisticsCard()

                Spacer(Modifier.height(24.dp))

                OtherStatistics(viewModel)
            }

        }
    }
}


@Composable
private fun MainStatisticsCard() {
    MyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Graph()

            StatisticSection()
            Divider(color = MaterialTheme.colorScheme.outline)
            XAxisSection()
            Divider(color = MaterialTheme.colorScheme.outline)
            FilterOptionSection()
        }
    }
}

@Composable
private fun Graph() {
    Image(
        painter = painterResource(id = R.drawable.graph_placeholder),
        contentDescription = "Preview of statistics",
        modifier = Modifier
            .height(240.dp)
            .padding(32.dp)
    )

//    AndroidView(
//        modifier = Modifier.padding(32.dp),
//        factory = { context ->
//
//        }
//    )
}

@Composable
private fun ColumnScope.StatisticSection() {
    SectionTitle(
        icon = Icons.Default.BarChart,
        title = "Statistic"
    )
}

@Composable
private fun ColumnScope.XAxisSection() {
    SectionTitle(
        icon = Icons.Default.TextRotationNone,
        title = "X-Axis"
    )
}

@Composable
private fun ColumnScope.FilterOptionSection() {
    SectionTitle(
        icon = Icons.Default.Tune,
        title = "Filter Options"
    )
}


@Composable
private fun SectionTitle(
    icon: ImageVector,
    title: String
) {
    Row() {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge
        )
    }
}


@Composable
private fun OtherStatistics(
    viewModel: StatisticsViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Other Statistics",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BigIconButton(
                title = "History",
                icon = Icons.Default.History,
                onClick = { viewModel.navigate(NavigationDirections.History) }
            )

            Spacer(Modifier.width(50.dp))

            BigIconButton(
                title = "Table",
                icon = Icons.Default.Toc,
                onClick = { viewModel.navigate(NavigationDirections.Table) }
            )
        }
    }
}

@Composable
private fun BigIconButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    MyCard(
        modifier = Modifier
            .size(96.dp)
            .clickable { onClick.invoke() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}


@Preview(widthDp = 380, heightDp = 780)
@Composable
private fun StatisticsScreenPreview() {
    DartAppTheme() {
        val viewModel = StatisticsViewModel(NavigationManager())
        StatisticsScreen(viewModel)
    }
}