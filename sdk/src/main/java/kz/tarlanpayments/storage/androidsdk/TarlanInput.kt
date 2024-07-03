package kz.tarlanpayments.storage.androidsdk

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TarlanInput(
    val hash: String,
    val transactionId: Long,
    val localeCode: String? = null,
    val isDebug: Boolean = false
) : Parcelable