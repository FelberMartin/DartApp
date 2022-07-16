@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dartapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dartapp.R
import com.example.dartapp.ui.theme.DartAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DartAppTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        SettingsRow()
        StatisticsCard()
        AppIconAndName()
        PlayButtonAndModeSelection()
    }

}

@Composable
private fun SettingsRow() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Settings"
        )
    }
}

@Composable
private fun StatisticsCard() {
    Card {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Average"
            )

            Image(
                painter = painterResource(id = R.drawable.graph_placeholder),
                contentDescription = "Preview of statistics"
            )

            OutlinedButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.AutoGraph,
                    contentDescription = null
                )
                Text(text = "more Stats")
            }
        }
    }
}

@Composable
private fun AppIconAndName() {
    Row(
        modifier = Modifier.height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.dartappicon),
            contentDescription = "App Icon",
            contentScale = ContentScale.FillHeight
        )
        Text(
            text = "Dart Stats",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraLight
            ),
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun PlayButtonAndModeSelection() {
    val modes = listOf("501", "Free Play")
    var selectedText =""

    Column() {
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Play")
        }
        DropdownMenu(expanded = false, onDismissRequest = { /*TODO*/ }) {
//            modes.forEach { label ->
//                DropdownMenuItem(onClick = {
//                    selectedText = label
//                }) {
//                    Text(text = label)
//                }
//            }
        }
    }

}

@Preview(showBackground = true, widthDp = 340, heightDp = 800)
@Composable
fun DefaultPreview() {
    DartAppTheme {
        HomeScreen()
    }
}