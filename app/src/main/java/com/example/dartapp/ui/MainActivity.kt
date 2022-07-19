@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dartapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dartapp.R
import com.example.dartapp.ui.shared.Background
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.shared.extensions.withDropShadow
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
    Background {
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


}

@Composable
private fun SettingsRow() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = "Settings",
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun StatisticsCard() {
    MyCard {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .padding(vertical = 12.dp),
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

            OutlinedButton(
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    imageVector = Icons.Filled.StackedLineChart,
                    contentDescription = null
                )
                Text(
                    text = "more Stats",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AppIconAndName() {
    Row(
        modifier = Modifier.height(62.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ElevatedCard(elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)) {
            Image(
                painter = painterResource(id = R.drawable.dartappicon),
                contentDescription = "App Icon",
                contentScale = ContentScale.FillHeight
            )
        }

        Text(
            text = "Dart Stats",
            style = MaterialTheme.typography.titleLarge.withDropShadow(),
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun PlayButtonAndModeSelection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 36.dp)
    ) {
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(44.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 5.dp)
        ) {
            Text(
                text = "Play",
                style = MaterialTheme.typography.titleLarge
            )
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        ModeSelection()
    }
}

@Composable
private fun ModeSelection() {
    val modes = listOf("501", "Free Play")
    var selectedMode by rememberSaveable { mutableStateOf("501") }
    var expanded by remember { mutableStateOf(false) }

    var textfieldSize by remember { mutableStateOf(Size.Zero)}

    val icon = if (expanded)
        Icons.Filled.ArrowDropUp //it requires androidx.compose.material:material-icons-extended
    else
        Icons.Filled.ArrowDropDown

    Box() {
        Row(
            modifier = Modifier.clickable { expanded = !expanded }
        ) {
            Text(
                text = selectedMode,
                color = MaterialTheme.colorScheme.primary
            )

            Icon(
                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            modes.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(
                            text = mode,
                            color = if (mode == selectedMode) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    },
                    onClick = { selectedMode = mode }
                )
            }
        }
    }

}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun DefaultPreview() {
    DartAppTheme {
        HomeScreen()
    }
}