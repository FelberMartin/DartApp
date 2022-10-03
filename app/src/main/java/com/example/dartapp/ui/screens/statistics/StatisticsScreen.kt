@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.dartapp.ui.screens.statistics

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.children
import com.example.dartapp.data.persistent.database.FakeLegDatabaseDao
import com.example.dartapp.ui.navigation.NavigationDirections
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.Background
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.shared.RoundedTopAppBar
import com.example.dartapp.ui.theme.DartAppTheme
import com.example.dartapp.util.extensions.observeAsStateNonOptional
import com.example.dartapp.util.graphs.filter.LegFilterBase
import com.example.dartapp.util.graphs.statistics.StatisticTypeBase
import com.example.dartapp.views.chart.*
import com.example.dartapp.views.chart.legend.Legend
import com.example.dartapp.views.chart.util.ColorManager
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
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
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
                StatisticsChart(viewModel)
            }

            Spacer(Modifier.height(6.dp))

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
fun StatisticsChart(
    viewModel: StatisticsViewModel,
    interactionEnabled: Boolean = true
) {
    val statisticType by viewModel.statisticType.observeAsStateNonOptional()
    val selectedFilterCategory by viewModel.selectedFilterCategory.observeAsStateNonOptional()
    val filterByTime = selectedFilterCategory == LegFilterBase.Category.ByTime

    val dataSet by viewModel.dataSet.observeAsStateNonOptional()
    val noData by viewModel.noLegDataAvailable.observeAsStateNonOptional()

    if (noData) {
        NoDataWarning("You first have to train to explore your statistics.")
    } else {
        when (statisticType.chartType) {
            EChartType.LINE_CHART -> LineGraph(dataSet, statisticType::modifyChart, filterByTime, interactionEnabled)
            EChartType.BAR_CHART -> BarGraph(dataSet, statisticType::modifyChart, interactionEnabled)
            EChartType.PIE_CHART -> PieGraph(dataSet, statisticType::modifyChart, interactionEnabled)
        }
    }
}

@Composable
fun NoDataWarning(detailedText: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            val color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            Icon(
                imageVector = Icons.Rounded.WarningAmber,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = color
            )
            Text(
                text = "No data",
                color = color,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = detailedText,
                textAlign = TextAlign.Center,
                color = color
            )
        }
    }
}

@Composable
fun LineGraph(
    dataSet: DataSet,
    modifyChart: (Chart) -> Unit,
    xAxisIsTime: Boolean,
    interactionEnabled: Boolean
) {
    val materialThemeBasedColorManager = ColorManager.materialThemeBasedColorManager()
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            LineChart(context).apply {
                colorManager = materialThemeBasedColorManager
                modifyChart(this)
                data = dataSet
            }
        },
        update = { chart ->
            modifyChart(chart)
            chart.interactionEnabled = interactionEnabled
            chart.showXAxisMarkers = xAxisIsTime
            chart.showVerticalGrid = xAxisIsTime
            chart.data = dataSet
        }
    )
}

@Composable
fun BarGraph(
    dataSet: DataSet,
    modifyChart: (Chart) -> Unit,
    interactionEnabled: Boolean
) {
    val materialThemeBasedColorManager = ColorManager.materialThemeBasedColorManager()
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            BarChart(context).apply {
                colorManager = materialThemeBasedColorManager
                data = dataSet
            }
        },
        update = { chart ->
            chart.data = dataSet
            chart.interactionEnabled = interactionEnabled
            modifyChart(chart)
        }
    )
}

@Composable
fun PieGraph(
    dataSet: DataSet,
    modifyChart: (Chart) -> Unit,
    interactionEnabled: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val materialThemeBasedColorManager = ColorManager.materialThemeBasedColorManager()
        AndroidView(
            modifier = Modifier.fillMaxSize(0.95f),
            factory = { context ->
                val chart = PieChart(context).apply {
                    colorManager = materialThemeBasedColorManager
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
                chart.interactionEnabled = interactionEnabled
                modifyChart(chart)

                val legend = layout.children.first { view -> view is Legend } as Legend
                legend.apply {
                    val params = layoutParams as ViewGroup.MarginLayoutParams
                    params.width = 575
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        SectionTitle(
            icon = Icons.Default.BarChart,
            title = "Statistic"
        )

        val selectedStatistic by viewModel.statisticType.observeAsStateNonOptional()
        var expanded by remember { mutableStateOf(false) }

        Spacer(modifier = Modifier.width(24.dp))

        Box() {
            FilterChip(
                selected = true,
                onClick = { expanded = !expanded },
                label = {
                    Text(
                        text = selectedStatistic.name,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(0.8f),
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                    )
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                StatisticTypeBase.all.forEach { statistic ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = statistic.name,
                                color = if (statistic == selectedStatistic) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        },
                        onClick = {
                            viewModel.setStatisticType(statistic)
                            expanded = false
                        }
                    )
                }
            }
        }
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
        modifier = Modifier.animateContentSize()
    ) {
        itemLabels.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            item {
                FilterChip(
                    selected = selected,
                    onClick = { if (!selected) onSelectionIndexChanged(index) },
                    leadingIcon = {
                        Box(
                            Modifier.animateContentSize(keyframes { durationMillis = 200 })
                        ) {
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        }
                    },
                    label = {
                        Text(text = label)
                    },
                )
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
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
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