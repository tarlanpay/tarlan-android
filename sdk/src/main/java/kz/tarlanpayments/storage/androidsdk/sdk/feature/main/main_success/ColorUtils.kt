package kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionColorRs
import kz.tarlanpayments.storage.androidsdk.sdk.ui.utils.parseColor

@Composable
internal fun TransactionColorRs.toFormGradient() = Brush.linearGradient(
    colors = listOf(
        parseColor(color = this.mainFormColor),
        parseColor(color = this.secondaryFormColor)
    )
)

@Composable
internal fun TransactionColorRs.toTextGradient() = Brush.linearGradient(
    colors = listOf(
        parseColor(color = this.mainTextColor),
        parseColor(color = this.secondaryTextColor)
    )
)

@Composable
internal fun TransactionColorRs.toInputGradient() = Brush.linearGradient(
    colors = listOf(
        parseColor(color = this.mainInputColor),
        parseColor(color = this.secondaryInputColor)
    )
)