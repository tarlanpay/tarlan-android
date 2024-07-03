package kz.tarlanpayments.storage.androidsdk


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface TarlanOutput : Parcelable {
    @Parcelize
    data class Success(val transactionId: Long) : TarlanOutput, Parcelable

    @Parcelize
    data class Failure(val error: String) : TarlanOutput, Parcelable
}
