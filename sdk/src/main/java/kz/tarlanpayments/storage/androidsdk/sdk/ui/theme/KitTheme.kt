package kz.tarlanpayments.storage.androidsdk.sdk.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Typography
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun KitTheme(
    isLightMode: Boolean = false,
    shape: RoundedCornerShape = RoundedCornerShape(0.dp),
    content: @Composable () -> Unit,
) {
    val colors = KitColor()
    val typography = KitTypography(colors)

    MaterialTheme(
        colors = colors.mapToMaterialColors(isLight = isLightMode),
        typography = typography.mapToTypography(),
        content = {
            CompositionLocalProvider(
                LocalKitColor provides colors,
                LocalRippleTheme provides KitRippleTheme(colors),
                LocalContentColor provides colors.primary,
            ) {

                Surface(
                    content = content,
                    shape = shape
                )
            }
        }
    )
}

internal val LocalKitColor = staticCompositionLocalOf<KitColor> {
    error("No Color provided")
}

@Composable
private fun KitColor.mapToMaterialColors(isLight: Boolean) = Colors(
    primary = this.accent,
    primaryVariant = this.accent,
    secondary = this.accent,
    secondaryVariant = this.accent,
    background = this.primaryInverted,
    surface = this.primaryInverted,
    error = this.negative,
    onPrimary = this.primary,
    onSecondary = this.secondary,
    onBackground = this.secondary,
    onSurface = this.primary,
    onError = this.primary,
    isLight = isLight
)

@Composable
private fun KitTypography.mapToTypography() = Typography(
    h1 = this.primaryLarge,
    h2 = this.primaryLarge,
    h3 = this.primaryMedium,
    h4 = this.primaryMedium,
    h5 = this.primarySmall,
    h6 = this.primarySmall,
    subtitle1 = this.secondaryLarge,
    subtitle2 = this.secondaryMedium,
    body1 = this.primarySmall,
    body2 = primarySmall,
    button = this.caps,
    caption = this.caps,
    overline = this.tagline
)

private class KitRippleTheme(
    private val backgroundColor: KitColor,
) : RippleTheme {
    @Composable
    override fun defaultColor(): Color = backgroundColor.primary

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(
        draggedAlpha = 0.3f,
        focusedAlpha = 0f,
        hoveredAlpha = 0f,
        pressedAlpha = 0.3f
    )
}