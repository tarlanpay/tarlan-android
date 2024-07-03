package kz.tarlanpayments.storage.androidsdk.sdk.data.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal data class TransactionRs(
    @SerializedName("transaction_id") @Expose val transactionId: Long,
    @SerializedName("transaction_status_code") @Expose val transactionStatusCode: String,
    @SerializedName("three_ds") @Expose val threeDs: ThreeDs?,
    @SerializedName("acquirer_code") @Expose val acquirerCode: String,
    @SerializedName("fingerprint") @Expose val fingerprint: Fingerprint?,
) {
    data class ThreeDs(
        @SerializedName("is_3ds") @Expose val is3ds: Boolean,
        @SerializedName("params") @Expose val params: HashMap<String, String>,
        @SerializedName("action") @Expose val action: String,
        @SerializedName("termUrl") @Expose val termUrl: String,
    )

    data class Fingerprint(
        @SerializedName("method_url") @Expose val methodUrl: String,
        @SerializedName("method_data") @Expose val methodData: String,
    )

    companion object {
        const val ThreeDsWaiting = "threeds_waiting"
        const val Success = "success"
        const val Fingerprint = "fingerprint"
    }
}