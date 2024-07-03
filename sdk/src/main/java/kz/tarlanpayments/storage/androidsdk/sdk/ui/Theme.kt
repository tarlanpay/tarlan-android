package kz.tarlanpayments.storage.androidsdk.sdk.ui

import androidx.compose.runtime.Composable
import kz.tarlanpayments.storage.androidsdk.sdk.ui.theme.KitColor
import kz.tarlanpayments.storage.androidsdk.sdk.ui.theme.LocalKitColor

internal object Theme {
    val kitColor: KitColor
        @Composable
        get() = LocalKitColor.current
}