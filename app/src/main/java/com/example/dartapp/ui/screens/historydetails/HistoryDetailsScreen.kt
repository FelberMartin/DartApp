@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dartapp.ui.screens.historydetails

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.children
import com.example.dartapp.data.persistent.database.Converters
import com.example.dartapp.data.persistent.database.FakeLegDatabaseDao
import com.example.dartapp.data.persistent.database.Leg
import com.example.dartapp.data.persistent.database.TestLegData
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.screens.history.DateAndTimeLabels
import com.example.dartapp.ui.screens.history.WeekDayLabel
import com.example.dartapp.ui.shared.Background
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.shared.RoundedTopAppBar
import com.example.dartapp.ui.theme.DartAppTheme
import com.example.dartapp.util.graphs.filter.GamesLegFilter
import com.example.dartapp.util.graphs.statistics.piechart.ServeDistributionStatistic
import com.example.dartapp.views.chart.LineChart
import com.example.dartapp.views.chart.PieChart
import com.example.dartapp.views.chart.data.DataPoint
import com.example.dartapp.views.chart.legend.Legend
import com.example.dartapp.views.chart.util.ColorManager
import com.example.dartapp.views.chart.util.DataSet

@Composable
fun HistoryDetailsScreenEntry(
    viewModel: HistoryDetailsViewModel
) {
    HistoryDetailsScreen(
        viewModel = viewModel,
        leg = viewModel.leg.observeAsState().value
    )
}

@Composable
fun HistoryDetailsScreen(
    viewModel: HistoryDetailsViewModel,
    leg: Leg?
) {
    Background {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            RoundedTopAppBar(
                title = "History Details",
                navigationViewModel = viewModel
            )

            if (leg == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    item {
                        ServeDistributionCard(leg = leg)
                    }
                    item {
                        NumbersAndDataCard(leg = leg)
                    }
                    item {
                        ServeProgressCard(leg = leg)
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ServeDistributionCard(leg: Leg) {
    DetailElementCard(title = "Serve Distribution") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            val dataSet = ServeDistributionStatistic.buildDataSet(listOf(leg), GamesLegFilter.all)
            val materialThemeBasedColorManager = ColorManager.materialThemeBasedColorManager()
            AndroidView(
                factory = { context ->
                    val chart = PieChart(context).apply {
                        colorManager = materialThemeBasedColorManager
                        data = dataSet
                    }
                    val legend = Legend(context).apply {
                        linkedChart = chart
                    }
                    LinearLayout(context).apply {
                        orientation = LinearLayout.HORIZONTAL
                        addView(chart)
                        addView(legend)
                        gravity = Gravity.CENTER_VERTICAL

                    }
                },
                update = { layout ->
                    val chart = layout.children.first { view -> view is PieChart } as PieChart
                    chart.apply {
                        data = dataSet
                        val params = layoutParams as ViewGroup.MarginLayoutParams
                        params.width = 500
                        params.rightMargin = 30
                        layoutParams = params
                    }
                    val legend = layout.children.first { view -> view is Legend } as Legend
                    legend.apply {
                        val params = layoutParams as ViewGroup.MarginLayoutParams
                        params.width = 200
                        params.topMargin = 10
                        layoutParams = params
                    }
                }
            )
        }
    }
}

@Composable
private fun DetailElementCard(
    title: String,
    content: @Composable () -> Unit
) {
    MyCard() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            content()
        }
    }
}

@Composable
private fun NumbersAndDataCard(leg: Leg) {
    DetailElementCard(title = "Numbers & Data") {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(top = 16.dp, bottom = 12.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val date = Converters.toLocalDateTime(leg.endTime)
                WeekDayLabel(date = date)
                DateAndTimeLabels(date = date)
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val doubleRateString = if (leg.doubleAttempts > 0) {
                    String.format("%.0f%%", 100.0 / leg.doubleAttempts.toDouble())
                } else "-"
                val avg9Darts = Converters.toListOfInts(leg.servesList).subList(0, 3).average()
                val legInfos = listOf(
                    Pair("Darts", leg.dartCount.toString()),
                    Pair("Average", String.format("%.1f", leg.servesAvg)),
                    Pair("Avg (9 Darts)", String.format("%.1f", avg9Darts)),
                    Pair("Double Rate", doubleRateString),
                    Pair("Checkout", Converters.toListOfInts(leg.servesList).last().toString())
                )
                for (legInfo in legInfos) {
                    LegInfoTableRow(legInfo)
                }
            }
        }
    }
}

@Composable
private fun LegInfoTableRow(legInfo: Pair<String, String>) {
    Row(Modifier.width(150.dp)) {
        Text(
            text = legInfo.first,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = legInfo.second,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Right,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ServeProgressCard(leg: Leg) {
    DetailElementCard(title = "Serve Progress") {
        val dataSet: DataSet = DataSet(Converters.toListOfInts(leg.servesList).mapIndexed { index, serve ->
            DataPoint(index + 1, serve)
        })
        dataSet.dataPointXType = DataSet.Type.NUMBER
        val materialThemeBasedColorManager = ColorManager.materialThemeBasedColorManager()

        AndroidView(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 6.dp)
                .height(340.dp),
            factory = { context ->
                LineChart(context).apply {
                    yStartAtZero = true
                    verticalAutoPadding = false
                    topAutoPadding = true
                    showXAxisMarkers = false
                    showVerticalGrid = false
                    data = dataSet
                    colorManager = materialThemeBasedColorManager
                }
            },
            update = { chart -> chart.data = dataSet }
        )
    }
}


@Preview
@Composable
fun PreviewHistoryDetailsScreen() {
    DartAppTheme() {
        val leg = TestLegData.createExampleLegs().first()
        HistoryDetailsScreen(
            viewModel = HistoryDetailsViewModel(NavigationManager(), FakeLegDatabaseDao(fillWithTestData = true)),
            leg = leg
        )
    }
}