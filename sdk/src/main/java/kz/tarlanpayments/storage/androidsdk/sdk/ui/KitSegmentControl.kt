package kz.tarlanpayments.storage.androidsdk.sdk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.tarlanpayments.storage.androidsdk.sdk.ui.utils.rememberLambda

internal data class KitSegmentItem(
    val title: String,
    val icon: Painter
)

@Composable
internal fun KitSegmentControl(
    modifier: Modifier = Modifier,
    selectedBrush: Brush,
    selectedColor: Color,
    accentColor: Color,
    unSelectedTint: Color,
    selectedBackground: Color,
    unSelectedBackground: Color,
    currentPageIndex: Int,
    onPageChanged: (Int) -> Unit,
    itemsTitle: List<KitSegmentItem>,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .then(modifier)
    ) {
        KitSegment(
            currentPageIndex = currentPageIndex,
            onPageChanged = onPageChanged,
            itemsTitle = itemsTitle,
            selectedBrush = selectedBrush,
            unSelectedTint = unSelectedTint,
            selectedBackground = selectedBackground,
            unSelectedBackground = unSelectedBackground,
            accentColor = accentColor,
            selectedColor = selectedColor
        )
    }
}

@Composable
private fun KitSegment(
    currentPageIndex: Int,
    onPageChanged: (Int) -> Unit,
    selectedBrush: Brush,
    selectedColor: Color,
    unSelectedTint: Color,
    selectedBackground: Color,
    unSelectedBackground: Color,
    accentColor: Color,
    itemsTitle: List<KitSegmentItem>,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = unSelectedBackground,
                shape = RoundedCornerShape(5.dp)
            )
            .height(36.dp)
            .padding(1.dp)
    ) {
        itemsTitle.forEachIndexed { index, itemTitle ->
            fun isItemSelected(): Boolean {
                return index == currentPageIndex
            }
            CompositionLocalProvider(
                LocalRippleTheme provides KitSegmentButtonRipple(accentColor)
            ) {
                Button(
                    onClick = rememberLambda { onPageChanged(index) },
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    elevation = null,
                    enabled = currentPageIndex != index,
                    shape = RoundedCornerShape(5.dp),
                    border = null,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = isItemSelected().backgroundColor(
                            selectedBackground = selectedBackground,
                            unSelectedBackground = unSelectedBackground
                        ),
                        contentColor = Color.Transparent,
                        disabledBackgroundColor = isItemSelected().backgroundColor(
                            selectedBackground = selectedBackground,
                            unSelectedBackground = unSelectedBackground
                        ),
                        disabledContentColor = Color.Transparent
                    ),
                    content = @Composable {
                        Row {
                            Icon(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .size(24.dp),
                                painter = itemTitle.icon,
                                contentDescription = "",
                                tint = if (isItemSelected()) selectedColor else unSelectedTint
                            )
                            Text(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                text = itemTitle.title,
                                style = isItemSelected().contentTextStyle(
                                    selectedBrush = selectedBrush,
                                    unSelectedTint = unSelectedTint
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    contentPadding = PaddingValues(0.dp)
                )
            }
        }
    }
}

private class KitSegmentButtonRipple(
    val color: Color
) : RippleTheme {
    @Composable
    override fun defaultColor(): Color = color

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(
        draggedAlpha = 0.3f, focusedAlpha = 0f, hoveredAlpha = 0f, pressedAlpha = 0.3f
    )
}

@Composable
private fun Boolean.contentTextStyle(
    selectedBrush: Brush,
    unSelectedTint: Color,
): TextStyle {
    when (this) {
        true -> {
            return TextStyle(brush = selectedBrush, fontWeight = FontWeight.W500, fontSize = 14.sp)
        }

        false -> {
            return TextStyle(color = unSelectedTint, fontSize = 14.sp, fontWeight = FontWeight.W500)
        }
    }
}

@Composable
private fun Boolean.backgroundColor(selectedBackground: Color, unSelectedBackground: Color): Color {
    return if (this) selectedBackground else unSelectedBackground
}

@Composable
private fun Boolean.tint(selectedBrush: Brush): Modifier {
    when (this) {
        true -> {
            return Modifier
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(selectedBrush, blendMode = BlendMode.SrcAtop)
                    }
                }
        }

        false -> {
            return Modifier
        }
    }
}