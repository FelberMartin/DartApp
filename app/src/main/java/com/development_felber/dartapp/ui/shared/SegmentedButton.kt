package com.development_felber.dartapp.ui.shared

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.development_felber.dartapp.ui.theme.DartAppTheme

@Composable
fun TowSegmentSingleSelectButton(
    text1: String,
    text2: String,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit
) {
    val borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .height(40.dp)
            .clip(CircleShape)
            .border(border = borderStroke, shape = CircleShape),
    ) {
        SegmentSelectButton(text = text1, isSelected = selectedIndex == 0) {
            onSelectedIndexChange(0)
        }

        Spacer(modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .background(color = MaterialTheme.colorScheme.outline)
        )

        SegmentSelectButton(text = text2, isSelected = selectedIndex == 1) {
            onSelectedIndexChange(1)
        }
    }
}

@Composable
private fun SegmentSelectButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animationSpec = keyframes<Dp> { durationMillis = 200 }

    val maxIconSize = 18.dp
    val iconSize by animateDpAsState(
        targetValue = if (isSelected) maxIconSize else 0.dp,
        animationSpec = animationSpec
    )
    val maxSpacerWidth = 8.dp
    val spacerWidth by animateDpAsState(
        targetValue = if (isSelected) maxSpacerWidth else 0.dp,
        animationSpec = animationSpec
    )
    val noSelectionSpacerWidth = maxIconSize - iconSize + maxSpacerWidth - spacerWidth

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .defaultMinSize(minWidth = 48.dp)
            .fillMaxHeight()
            .clickable { onClick() }
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else
                    MaterialTheme.colorScheme.surface
            )
    ) {
        Spacer(Modifier.width(12.dp))

        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )

        Spacer(Modifier.width(noSelectionSpacerWidth / 2))

        Spacer(Modifier.width(spacerWidth))

        Text(
            text = text,
        )

        Spacer(Modifier.width(noSelectionSpacerWidth / 2))

        Spacer(Modifier.width(12.dp))

    }
}


@Preview(showBackground = true)
@Composable
private fun TowSegmentSingleSelectButtonPreview() {
    DartAppTheme() {
        var selectedIndex by remember { mutableStateOf(0) }
        TowSegmentSingleSelectButton(
            text1 = "Text 1",
            text2 = "Text 2",
            selectedIndex = selectedIndex,
            onSelectedIndexChange = { selectedIndex = it }
        )
    }
}
