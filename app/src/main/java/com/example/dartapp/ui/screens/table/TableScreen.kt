package com.example.dartapp.ui.screens.table

import android.graphics.fonts.FontStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.Background
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.shared.NavigationViewModel
import com.example.dartapp.ui.shared.RoundedTopAppBar
import com.example.dartapp.ui.theme.DartAppTheme
import java.time.format.TextStyle

@Composable
fun TableScreen(
    viewModel: NavigationViewModel
) {
    Background {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            RoundedTopAppBar(
                title = "Table",
                navigationViewModel = viewModel
            )

            Spacer(Modifier.height(16.dp))

            Table(
                header = "Totals",
                items = listOf(Pair("#Games", "80"), Pair("#Darts", "123"), Pair("Time spent", "11h 13m"))
            )
        }
    }
}

@Composable
private fun Table(
    header: String,
    items: List<Pair<String, String>>
) {
    MyCard(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
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
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

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
            .padding(horizontal = 16.dp, vertical = 6.dp)
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
fun previewTableScreen() {
    DartAppTheme() {
        TableScreen(viewModel = TableViewModel(NavigationManager()))
    }
}

