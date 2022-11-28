@file:OptIn(ExperimentalMaterial3Api::class)

package com.development_felber.dartapp.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.ui.navigation.NavigationCommand
import com.development_felber.dartapp.ui.theme.DartAppTheme

@Composable
fun BackTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onBackClicked: (() -> Unit)? = null,
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(
                onClick = onBackClicked ?: {},
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.height(24.dp)
                )
            }
         },

        scrollBehavior = scrollBehavior,
    )
}


@Preview(showBackground = true, widthDp = 340, heightDp = 200)
@Composable
fun PreviewRoundedTopAppBar() {
    DartAppTheme() {
        Background() {
            Column() {
                BackTopAppBar(title = "Title")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 340, heightDp = 200)
@Composable
fun PreviewRoundedTopAppBarDark() {
    DartAppTheme(useDarkTheme = true) {
        Background() {
            Column() {
                BackTopAppBar(title = "Title")
            }
        }
    }
}