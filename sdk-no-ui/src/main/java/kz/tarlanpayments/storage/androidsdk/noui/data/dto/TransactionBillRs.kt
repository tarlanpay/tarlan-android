package kz.tarlanpayments.storage.androidsdk.noui.data.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal data class TransactionBillRs(
    @SerializedName("transaction_id") @Expose val transactionId: Long,
    @SerializedName("order_amount") @Expose val orderAmount: Double,
    @SerializedName("upper_commission_amount") @Expose val upperCommissionAmount: Double,
    @SerializedName("total_amount") @Expose val totalAmount: Double,
    @SerializedName("currency") @Expose val currency: String,
    @SerializedName("project_name") @Expose val projectName: String,
    @SerializedName("logo") @Expose val logo: String,
    @SerializedName("acquirer_name") @Expose val acquirerName: String,
    @SerializedName("payment_organization") @Expose val paymentOrganization: String,
    @SerializedName("date_time") @Expose val dateTime: String,
    @SerializedName("masked_pan") @Expose val maskedPan: String,
    @SerializedName("transaction_status_name") @Expose val transactionStatusName: String,
    @SerializedName("transaction_type") @Expose val transactionType: String,
    @SerializedName("email") @Expose val email: String,
    @SerializedName("phone") @Expose val phone: String,
    @SerializedName("description") @Expose val description: String
)