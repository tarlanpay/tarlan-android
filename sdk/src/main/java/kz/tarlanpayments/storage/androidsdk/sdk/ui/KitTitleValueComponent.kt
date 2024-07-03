package kz.tarlanpayments.storage.androidsdk.sdk.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun KitTitleValueComponent(
    title: String,
    value: String,
    titleColor: Color,
    valueColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = TextStyle(
                    color = titleColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400
                ),
                color = titleColor
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Text(
                text = value,
                style = TextStyle(
                    color = valueColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400
                ),
                color = valueColor
            )
        }
        KitDotDivider(secondaryColor = valueColor)
    }
}