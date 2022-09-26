@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dartapp.ui.screens.statistics

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Toc
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.children
import com.example.dartapp.chartstuff.graphs.filter.LegFilterBase
import com.example.dartapp.chartstuff.graphs.statistics.StatisticTypeBase
import com.example.dartapp.data.persistent.database.FakeLegDatabaseDao
import com.example.dartapp.ui.navigation.NavigationDirections
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.Background
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.shared.RoundedTopAppBar
import com.example.dartapp.ui.theme.DartAppTheme
import com.example.dartapp.util.extensions.observeAsStateNonOptional
import com.example.dartapp.views.chart.BarChart
import com.example.dartapp.views.chart.EChartType
import com.example.dartapp.views.chart.LineChart
import com.example.dartapp.views.chart.PieChart
import com.example.dartapp.views.chart.legend.Legend
import com.example.dartapp.views.chart.util.DataSet

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

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp)
            ) {
                item {
                    MainStatisticsCard(viewModel)
                }

                item {
                    Spacer(Modifier.height(24.dp))
                    OtherStatistics(viewModel)
                    Spacer(Modifier.height(40.dp))
                }
            }

        }
    }
}


@Composable
private fun MainStatisticsCard(
    viewModel: StatisticsViewModel
) {
    MyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(vertical = 16.dp)
        ) {
            Box(Modifier.height(400.dp)) {
                Graph(viewModel)
            }

            Box(Modifier.padding(horizontal = 16.dp)) {
                StatisticSection(viewModel)
            }

            Divider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(vertical = 4.dp))

            Box(Modifier.padding(horizontal = 16.dp)) {
                FilterOptionSection(viewModel)
            }
        }
    }
}

@Composable
private fun Graph(
    viewModel: StatisticsViewModel
) {
    val statisticType by viewModel.statisticType.observeAsStateNonOptional()
    val selectedFilterCategory by viewModel.selectedFilterCategory.observeAsStateNonOptional()
    val showXMarkers = selectedFilterCategory == LegFilterBase.Category.ByTime

    val dataSet by viewModel.dataSet.observeAsStateNonOptional()

    println("Recomposed graph")
    when (statisticType.chartType) {
        EChartType.LINE_CHART -> LineGraph(dataSet, showXMarkers)
        EChartType.BAR_CHART -> BarGraph(dataSet)
        EChartType.PIE_CHART -> PieGraph(dataSet)
    }
}

@Composable
fun LineGraph(
    dataSet: DataSet,
    showXMarkers: Boolean
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            LineChart(context).apply {
                showVerticalGrid = false
                data = dataSet
            }
        },
        update = { chart ->
            chart.data = dataSet
            chart.showXAxisMarkers = showXMarkers
        }
    )
}

@Composable
fun BarGraph(
    dataSet: DataSet
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            BarChart(context).apply {
                data = dataSet
            }
        },
        update = { chart -> chart.data = dataSet }
    )
}

@Composable
fun PieGraph(
    dataSet: DataSet
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(0.95f),
            factory = { context ->
                val chart = PieChart(context).apply {
                    data = dataSet
                }
                val legend = Legend(context).apply {
                    linkedChart = chart

                }
                LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    addView(chart, 0)
                    addView(legend, 1)
                    gravity = Gravity.CENTER_HORIZONTAL
                }
            },
            update = { layout ->
                val chart = layout.children.first { view -> view is PieChart } as PieChart
                chart.data = dataSet
                val legend = layout.children.first { view -> view is Legend } as Legend
                legend.apply {
                    val params = layoutParams as ViewGroup.MarginLayoutParams
                    params.width = 600
                    params.topMargin = 10
                    layoutParams = params

                }
            }
        )
    }
}

@Composable
private fun StatisticSection(
    viewModel: StatisticsViewModel
) {
    Column {
        SectionTitle(
            icon = Icons.Default.BarChart,
            title = "Statistic"
        )

        val statisticType by viewModel.statisticType.observeAsStateNonOptional()
        println("Current Stat type at composable is ${statisticType.name}")
        SingleSelectChipGroup(
            itemLabels = StatisticTypeBase.all.map { x -> x.name },
            selectedIndex = StatisticTypeBase.all.indexOf(statisticType),
            onSelectionIndexChanged = {
                viewModel.setStatisticType(StatisticTypeBase.all[it])
            }
        )
    }
}

@Composable
fun SingleSelectChipGroup(
    itemLabels: List<String>,
    selectedIndex: Int,
    onSelectionIndexChanged: (Int) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemLabels.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            item {
                FilterChip(selected = selected, onClick = { if (!selected) onSelectionIndexChanged(index) }, label = {
                    Text(text = label)
                })
            }
        }
    }
}

@Composable
private fun FilterOptionSection(
    viewModel: StatisticsViewModel
) {
    Column {
        SectionTitle(
            icon = Icons.Default.Tune,
            title = "Filter Options"
        )

        Spacer(Modifier.height(8.dp))

        val statisticType by viewModel.statisticType.observeAsStateNonOptional()
        val selectedFilterCategory by viewModel.selectedFilterCategory.observeAsStateNonOptional()
        val legFilter by viewModel.legFilter.observeAsStateNonOptional()

        FilterOptionsSubTitle(text = "filter category")
        SingleSelectChipGroup(
            itemLabels = statisticType.availableFilterCategories.map { x -> x.displayedName },
            selectedIndex = statisticType.availableFilterCategories.indexOf(selectedFilterCategory),
            onSelectionIndexChanged = { index ->
                val newlySelected = statisticType.availableFilterCategories[index]
                viewModel.setSelectedFilterCategory(newlySelected)
            }
        )

        FilterOptionsSubTitle(text = "limit by")
        SingleSelectChipGroup(
            itemLabels = selectedFilterCategory.filterOptions.map { x -> x.name },
            selectedIndex = selectedFilterCategory.filterOptions.indexOf(legFilter),
            onSelectionIndexChanged = { index ->
                val newlySelected = selectedFilterCategory.filterOptions[index]
                viewModel.setLegFilter(newlySelected)
            }
        )

    }
}

@Composable
private fun FilterOptionsSubTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(vertical = 4.dp)
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


@Preview(widthDp = 380, heightDp = 1000)
@Composable
private fun StatisticsScreenPreview() {
    DartAppTheme() {
        val viewModel = StatisticsViewModel(NavigationManager(), FakeLegDatabaseDao(fillWithTestData = true))
        StatisticsScreen(viewModel)
    }
}