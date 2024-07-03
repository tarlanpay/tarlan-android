package kz.tarlanpayments.storage.androidsdk.sdk.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal data class KitTypography(val kitColor: KitColor) {
    val primaryLarge: TextStyle = TextStyle(
        color = kitColor.tertiary,
        fontSize = 18.0.sp,
        lineHeight = 24.0.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.15.sp
    )
    val primaryMedium: TextStyle = TextStyle(
        color = kitColor.primary,
        fontSize = 16.0.sp,
        lineHeight = 24.0.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.15.sp
    )
    val primarySmall: TextStyle = TextStyle(
        color = kitColor.primary,
        fontSize = 14.0.sp,
        lineHeight = 20.0.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.25.sp
    )
    val secondaryLarge: TextStyle = TextStyle(
        color = kitColor.tertiary,
        fontSize = 13.0.sp,
        lineHeight = 20.0.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.25.sp
    )
    val secondaryMedium: TextStyle = TextStyle(
        color = kitColor.primary,
        fontSize = 12.0.sp,
        lineHeight = 16.0.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.40.sp
    )
    val tagline: TextStyle = TextStyle(
        color = kitColor.primary,
        fontSize = 12.0.sp,
        lineHeight = 16.0.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.4.sp
    )
    val caps: TextStyle = TextStyle(
        color = kitColor.primary,
        fontSize = 12.0.sp,
        lineHeight = 16.0.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.25.sp
    )
}