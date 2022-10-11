package com.development_felber.dartapp.ui.shared.extensions

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle

@Composable
fun TextStyle.withDropShadow() : TextStyle = this.copy(
    shadow = Shadow(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
        offset = Offset(x = 2f, y = 4f),
        blurRadius = 2f
    )
)

