package kz.tarlanpayments.storage.androidsdk.sdk.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Composable
internal inline fun <T> rememberLambdaT(crossinline lambda: @DisallowComposableCalls (T) -> Unit): (T) -> Unit =
    remember { { lambda(it) } }

@Composable
internal fun parseColor(color: String): Color {
    if (color.length == 9) {
        Color(
            android.graphics.Color.parseColor(
                color.take(1) + color.takeLast(2) + color.take(7).takeLast(6)
            )
        )
    }
    if (color.length == 7) {
        return Color(android.graphics.Color.parseColor(color.substring(0, 7).uppercase()))
    }
    return Color(android.graphics.Color.parseColor(color.uppercase()))
}

@Composable
inline fun rememberLambda(crossinline lambda: @DisallowComposableCalls () -> Unit): () -> Unit =
    remember { { lambda() } }