package kz.tarlanpayments.storage.androidsdk.sdk.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun KitLanguageButton(
    modifier: Modifier,
    title: String,
    textColor: Color,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = accentColor),
                onClick = onClick
            )
            .then(modifier)
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = title.uppercase(),
            style = TextStyle(
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.W400
            ),
            color = textColor
        )
        Icon(
            modifier = Modifier.align(Alignment.CenterVertically).size(24.dp),
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "",
            tint = textColor,
        )
    }
}