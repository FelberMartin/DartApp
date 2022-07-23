package com.example.dartapp.ui.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dartapp.ui.navigation.command.NavigationCommand
import com.example.dartapp.ui.theme.DartAppTheme

@Composable
fun RoundedTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onBackClicked: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon = { Icon(
            imageVector = Icons.Default.ArrowBackIos,
            contentDescription = "Back",
            modifier = Modifier
                .padding(20.dp)
                .height(16.dp)
                .clickable(onClick = onBackClicked ?: {})
        ) },
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp), ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun RoundedTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigationViewModel: NavigationViewModel
) = com.example.dartapp.ui.shared.RoundedTopAppBar(
    title = title,
    scrollBehavior = scrollBehavior,
    onBackClicked = { navigationViewModel.navigate(NavigationCommand.NAVIGATE_UP) }
)

@Preview(showBackground = true, widthDp = 340, heightDp = 200)
@Composable
fun PreviewRoundedTopAppBar() {
    DartAppTheme() {
        Background() {
            Column() {
                RoundedTopAppBar(title = "Title")
            }
        }
    }
}