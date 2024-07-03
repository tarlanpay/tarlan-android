package kz.tarlanpayments.storage.androidsdk.sdk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun KitGradientButton(
    title: String,
    brush: Brush,
    textColor: Color,
    accentColor: Color,
    isEnabled: Boolean,
    isProgress: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .backgroundIsEnabled(
                brush = brush,
                shape = RoundedCornerShape(6.dp),
                isEnabled = isEnabled
            )
            .clickableIfEnabled(
                isEnabled = isEnabled,
                accentColor = accentColor,
                onClick = onClick,
                isProgress = isProgress
            )
    ) {
        if (!isProgress) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = title.uppercase(),
                style = TextStyle(
                    color = isEnabled.toColor(color = textColor),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp)
                    .padding(4.dp),
                color = textColor,
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
private fun Boolean.toColor(color: Color): Color {
    return if (this) {
        color
    } else {
        Color(0xFF838383)
    }
}

@Composable
private fun Modifier.clickableIfEnabled(
    isProgress: Boolean,
    isEnabled: Boolean,
    accentColor: Color,
    onClick: () -> Unit
): Modifier = if (isEnabled && !isProgress) {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = true, color = accentColor),
        onClick = onClick
    )
} else {
    this
}

@Composable
private fun Modifier.backgroundIsEnabled(
    isEnabled: Boolean,
    brush: Brush,
    shape: RoundedCornerShape
): Modifier = if (isEnabled) {
    background(brush = brush, shape = shape)
} else {
    background(color = Color(0xFFD9D9D9), shape = shape)
}