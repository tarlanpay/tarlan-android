package kz.tarlanpayments.storage.androidsdk.sdk.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kz.tarlanpayments.storage.androidsdk.R

@Composable
internal fun KitCompanyLogo(): @Composable ColumnScope.() -> Unit = {
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.align(Alignment.CenterHorizontally)
    ) {
        Image(
            modifier = Modifier.size(48.dp),
            painter = painterResource(id = R.drawable.ic_master_card),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            modifier = Modifier.size(48.dp),
            painter = painterResource(id = R.drawable.ic_tarlan),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            modifier = Modifier.size(48.dp),
            painter = painterResource(id = R.drawable.ic_visa),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            modifier = Modifier.size(48.dp),
            painter = painterResource(id = R.drawable.ic_pci_dss),
            contentDescription = ""
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

    //        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .aspectRatio(2f / 1f)
//                .background(Color.Gray, RoundedCornerShape(10.dp))
//        ) {
//
//        }
}