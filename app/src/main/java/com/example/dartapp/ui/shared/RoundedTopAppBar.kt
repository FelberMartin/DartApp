@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dartapp.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
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
        navigationIcon = {
            IconButton(
                onClick = onBackClicked ?: {},
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .padding(10.dp)
                        .height(24.dp)

                )
            }
         },
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
) = RoundedTopAppBar(
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