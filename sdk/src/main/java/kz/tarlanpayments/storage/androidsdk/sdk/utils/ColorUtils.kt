package kz.tarlanpayments.storage.androidsdk.sdk.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionDescriptionModel
import kz.tarlanpayments.storage.androidsdk.sdk.ui.utils.parseColor

@Composable
internal fun TarlanTransactionDescriptionModel.toFormGradient() = Brush.linearGradient(
    colors = listOf(
        parseColor(color = this.mainFormColor),
        parseColor(color = this.secondaryFormColor)
    )
)

@Composable
internal fun TarlanTransactionDescriptionModel.toTextGradient() = Brush.linearGradient(
    colors = listOf(
        parseColor(color = this.mainTextColor),
        parseColor(color = this.secondaryTextColor)
    )
)

@Composable
internal fun TarlanTransactionDescriptionModel.toInputGradient() = Brush.linearGradient(
    colors = listOf(
        parseColor(color = this.mainInputColor),
        parseColor(color = this.secondaryInputColor)
    )
)