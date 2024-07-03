package kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.classic

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kz.tarlanpayments.storage.androidsdk.R

@Composable
internal fun MainSuccessClassicGooglePay(
    onGooglePayClick: () -> Unit,
    isGooglePayClickEnabled: Boolean,
    brush: Brush
) {

    if (isGooglePayClickEnabled)
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
                .background(Color.Black, RoundedCornerShape(6.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = true,
                        color = Color.Black.copy(alpha = 0.6f),
                        radius = 6.dp
                    ),
                    onClick = onGooglePayClick
                )
        ) {
            Spacer(modifier = Modifier.fillMaxWidth().weight(1f))
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_google_pay),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier.height(24.dp),
                painter = painterResource(id = R.drawable.ic_google_pay_title),
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.fillMaxWidth().weight(1f))
        }
    else {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
                .background(Color.Transparent)
                .border(
                    1.dp,
                    brush,
                    shape = RoundedCornerShape(6.dp)
                )
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_google_pay),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier.height(24.dp),
                painter = painterResource(id = R.drawable.ic_google_pay_title),
                contentDescription = null,
            )
        }
    }
}