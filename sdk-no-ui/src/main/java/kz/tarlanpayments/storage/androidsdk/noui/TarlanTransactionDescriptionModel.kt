package kz.tarlanpayments.storage.androidsdk.noui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TarlanTransactionDescriptionModel(
    val cards: List<SavedCard>,
    val totalAmount: Double,
    val projectID: Long,
    val type: TransactionType,
    val status: TarlanTransactionStatusModel,
    val hasGooglePay: Boolean,
    val requiredEmail: Boolean,
    val hasEmail: Boolean,
    val requiredPhone: Boolean,
    val hasPhone: Boolean,
    val hasDefaultCard: Boolean,
    val logoFilePath: String,
    val upperCommissionAmount: Double,
    val transactionId: Long,
    val hasRedirect: Boolean,

    val storeName: String,
    val orderAmount: Double,

    val dateTime: String?,
    val acquirerName: String?,
    val paymentOrganization: String?,

    val mainFormColor: String,
    val secondaryFormColor: String,
    val mainInputColor: String,
    val secondaryInputColor: String,
    val mainTextColor: String,
    val secondaryTextColor: String,

    val mainTextInputColor: String,
    val inputLabelColor: String,

    val timeout: Int?,
    val transactionTypeName: String?,
    val transactionCurrency: String?,
    val phone: String?,
    val email: String?,
    val description: String?,
    val projectName: String?

) : Parcelable {

    @Parcelize
    data class SavedCard(
        val cardToken: String,
        val maskedPan: String
    ) : Parcelable

    @Parcelize
    enum class TransactionType : Parcelable {
        In,
        Out,
        CardLink
    }
}