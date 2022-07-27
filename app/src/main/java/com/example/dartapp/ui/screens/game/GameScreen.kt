package com.example.dartapp.ui.screens.game

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dartapp.ui.navigation.NavigationManager
import com.example.dartapp.ui.shared.Background
import com.example.dartapp.ui.shared.MyCard
import com.example.dartapp.ui.theme.DartAppTheme
import com.example.dartapp.ui.values.Padding


@Composable
fun GameScreen(
    viewModel: GameViewModel
) {
    Background {
        Column(Modifier.fillMaxSize()) {
            TopRow(onCloseClicked = viewModel::closeClicked)
            Column(
                modifier = Modifier
                    .padding(Padding.MediumPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                PlayerStats(viewModel)
                Spacer(Modifier.height(20.dp))
                BottomElements(viewModel)
            }
        }
    }
}

@Composable
private fun TopRow(
    onCloseClicked: () -> Unit
) {
    Row(Modifier.fillMaxWidth()) {
        Spacer(Modifier.weight(1f))

        IconButton(onClick = onCloseClicked) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close"
            )
        }
    }
}

@Composable
private fun PlayerStats(
    viewModel: GameViewModel
) {
    MyCard(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "501",
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PlayerGameStatistic(
                    name = "Darts: ",
                    valueString = "12"
                )

                PlayerGameStatistic(
                    name = "Last: ",
                    valueString = "80"
                )

                PlayerGameStatistic(
                    name = "Ø ",
                    valueString = "56.6"
                )
            }
        }
    }
}

@Composable
private fun PlayerGameStatistic(
    name: String,
    valueString: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Text(
            text = valueString,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun BottomElements(
    viewModel: GameViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth().height(450.dp)
    ) {
        CheckoutInfo()
        NumPadInfoAndActionsRow()
        PerServeNumPad(
            onDigitClicked = {},
            onClearClicked = { /*TODO*/ },
            onEnterClicked = {}
        )
    }
}

@Composable
private fun CheckoutInfo() {
    MyCard() {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "Checkout: T19 D20",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun NumPadInfoAndActionsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.onBackground, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Undo,
                contentDescription = "Undo"
            )
        }

        Text(
            text = "69",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.secondary
        )

        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.onBackground, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.AppRegistration,
                contentDescription = "Change NumPad Layout"
            )
        }
    }
}



@Preview(widthDp = 380, heightDp = 780)
@Composable
private fun GameScreenPreview() {
    DartAppTheme() {
        val viewModel = GameViewModel(NavigationManager())
        GameScreen(viewModel)
    }
}