package kz.tarlanpayments.storage.androidsdk.noui.data.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal data class InRq(
    @SerializedName("transaction_id") @Expose val transactionId: Long,
    @SerializedName("user_phone") @Expose val userPhone: String? = null,
    @SerializedName("user_email") @Expose val userEmail: String? = null,
    @SerializedName("transaction_hash") @Expose val transactionHash: String,
    @SerializedName("save") @Expose val save: Boolean = false,
    @SerializedName("encrypted_card") @Expose val encryptedCard: String? = null,
) {
    data class ValueToEncrypt(
        @SerializedName("pan") @Expose val pan: String,
        @SerializedName("cvc") @Expose val cvc: String,
        @SerializedName("exp_month") @Expose val month: String,
        @SerializedName("exp_year") @Expose val year: String,
        @SerializedName("full_name") @Expose val fullName: String? = null,
    )
}

internal data class CardLinkRq(
    @SerializedName("transaction_id") @Expose val transactionId: Long,
    @SerializedName("full_name") @Expose val fullName: String? = null,
    @SerializedName("user_phone") @Expose val userPhone: String? = null,
    @SerializedName("user_email") @Expose val userEmail: String? = null,
    @SerializedName("transaction_hash") @Expose val transactionHash: String,
    @SerializedName("save") @Expose val save: Boolean = true,
    @SerializedName("encrypted_card") @Expose val encryptedCard: String? = null,
)

internal data class PayoutRq(
    @SerializedName("transaction_id") @Expose val transactionId: Long,
    @SerializedName("transaction_hash") @Expose val transactionHash: String,
    @SerializedName("user_email") @Expose val userEmail: String? = null,
    @SerializedName("user_phone") @Expose val userPhone: String? = null,
    @SerializedName("encrypted_pan") @Expose val encryptedPan: String
) {
    data class ValueToEncrypt(
        @SerializedName("pan") @Expose val pan: String
    )
}

internal data class InFromSavedRq(
    @SerializedName("transaction_id") @Expose val transactionId: Long,
    @SerializedName("encrypted_id") @Expose val encryptedId: String,
    @SerializedName("user_email") @Expose val userEmail: String? = null,
    @SerializedName("user_phone") @Expose val userPhone: String? = null,
    @SerializedName("transaction_hash") @Expose val transactionHash: String
)

internal data class OutFromSavedRq(
    @SerializedName("transaction_id") @Expose val transactionId: Long,
    @SerializedName("encrypted_id") @Expose val encryptedId: String,
    @SerializedName("user_email") @Expose val userEmail: String? = null,
    @SerializedName("user_phone") @Expose val userPhone: String? = null,
    @SerializedName("transaction_hash") @Expose val transactionHash: String
)

internal data class ResumeTransactionRq(
    @SerializedName("transaction_id") @Expose val transactionId: Long,
    @SerializedName("transaction_hash") @Expose val transactionHash: String
)

internal data class DeleteCardRq(
    @SerializedName("transaction_id") @Expose val transactionId: Long,
    @SerializedName("transaction_hash") @Expose val transactionHash: String,
    @SerializedName("encrypted_card_id") @Expose val encryptedCardId: String,
    @SerializedName("project_id") @Expose val projectId: Long,
)

internal data class GooglePayRq(
    @SerializedName("paymentMethodData") @Expose val paymentMethodData: Map<String, Any>,
    @SerializedName("transactionId") @Expose val transactionId: Long,
    @SerializedName("transactionHash") @Expose val transactionHash: String,
)