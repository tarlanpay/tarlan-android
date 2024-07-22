package kz.tarlanpayments.storage.androidsdk.sdk.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils

@Composable
internal inline fun <T> rememberLambdaT(crossinline lambda: @DisallowComposableCalls (T) -> Unit): (T) -> Unit =
    remember { { lambda(it) } }

@Composable
internal fun parseColor(color: String): Color {
    if (color.length == 9) {
        val alpha: Int = color.substring(7, 9).toInt(16)
        val baseColorString = color.substring(0, 7)
        val baseColor: Int = android.graphics.Color.parseColor(baseColorString)

        return Color(ColorUtils.setAlphaComponent(baseColor, alpha))
    }
    if (color.length == 7) {
        return Color(android.graphics.Color.parseColor(color.substring(0, 7).uppercase()))
    }
    return Color(android.graphics.Color.parseColor(color.uppercase()))
}


@Composable
inline fun rememberLambda(crossinline lambda: @DisallowComposableCalls () -> Unit): () -> Unit =
    remember { { lambda() } }