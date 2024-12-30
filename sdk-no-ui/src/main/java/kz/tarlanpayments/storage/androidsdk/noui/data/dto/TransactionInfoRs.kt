package kz.tarlanpayments.storage.androidsdk.noui.data.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal data class TransactionColorRs(
    @SerializedName("project_id") @Expose val projectId: Int,
    @SerializedName("merchant_id") @Expose val merchantId: Int,
    @SerializedName("view_type") @Expose val viewType: String,
    @SerializedName("main_form_color") @Expose val mainFormColor: String,
    @SerializedName("secondary_form_color") @Expose val secondaryFormColor: String,
    @SerializedName("main_text_color") @Expose val mainTextColor: String,
    @SerializedName("secondary_text_color") @Expose val secondaryTextColor: String,
    @SerializedName("main_input_color") @Expose val mainInputColor: String,
    @SerializedName("secondary_input_color") @Expose val secondaryInputColor: String,
    @SerializedName("main_text_input_color") @Expose val mainTextInputColor: String,
    @SerializedName("input_label_color") @Expose val inputLabelColor: String,
    @SerializedName("main_modern_color") @Expose val mainModernColor: String,
    @SerializedName("secondary_modern_color") @Expose val secondaryModernColor: String,
    @SerializedName("modern_button_color") @Expose val modernButtonColor: String,
    @SerializedName("light_button_color") @Expose val lightButtonColor: String
)

internal data class TransactionInfoMainRs(
    @SerializedName("transaction_id") @Expose val transactionId: Long,
    @SerializedName("project_reference_id") @Expose val projectReferenceId: String,
    @SerializedName("project_client_id") @Expose val projectClientId: String,
    @SerializedName("project_id") @Expose val projectId: Long,
    @SerializedName("merchant_id") @Expose val merchantId: Long,
    @SerializedName("order_amount") @Expose val orderAmount: Double,
    @SerializedName("upper_commission_amount") @Expose val upperCommissionAmount: Double,
    @SerializedName("total_amount") @Expose val totalAmount: Double,
    @SerializedName("logo") @Expose val logo: String?,
    @SerializedName("transaction_info") @Expose val transactionInfo: TransactionInfo,
    @SerializedName("transaction_status") @Expose val transactionStatus: TransactionStatusDto,
    @SerializedName("transaction_type") @Expose val transactionType: TransactionTypeDto,
    @SerializedName("available_types") @Expose val availableTypes: List<TransactionAvailableTypesDto>,
    @SerializedName("cards") @Expose val cards: List<CardDto>,
    @SerializedName("description") @Expose val description: String? = null
) {

    data class CardDto(
        @SerializedName("card_token") @Expose val cardToken: String,
        @SerializedName("masked_pan") @Expose val maskedPan: String,
    )

    data class TransactionInfo(
        @SerializedName("callback_url") @Expose val callbackUrl: String,
        @SerializedName("success_redirect_url") @Expose val successRedirectUrl: String,
        @SerializedName("failure_redirect_url") @Expose val failureRedirectUrl: String,
    )

    data class TransactionStatusDto(
        @SerializedName("code") @Expose val code: String,
        @SerializedName("name") @Expose val name: String,
    ) {
        companion object {
            const val NEW = "new"
            const val SUCCESS = "success"
            const val FAIL = "fail"
            const val REFUND = "refund"
        }
    }

    data class TransactionTypeDto(
        @SerializedName("code") @Expose val code: String,
        @SerializedName("name") @Expose val name: String,
    ) {
    }

    data class TransactionAvailableTypesDto(
        @SerializedName("code") @Expose val code: String,
        @SerializedName("name") @Expose val name: String,
    ) {
        companion object {
            const val GooglePay = "google_pay"
            const val IN = "in"
            const val OUT = "out"
            const val CARD_LINK = "card_link"
        }
    }
}

internal data class TransactionInfoPayFormRs(
    @SerializedName("id") @Expose val id: Int,
    @SerializedName("project_id") @Expose val projectId: Int,
    @SerializedName("merchant_id") @Expose val merchantId: Int,
    @SerializedName("logo_file_path") @Expose val logoFilePath: String,
    @SerializedName("store_name") @Expose val storeName: String,
    @SerializedName("has_email") @Expose val hasEmail: Boolean,
    @SerializedName("required_email") @Expose val requiredEmail: Boolean,
    @SerializedName("has_phone") @Expose val hasPhone: Boolean,
    @SerializedName("required_phone") @Expose val requiredPhone: Boolean,
    @SerializedName("default_language") @Expose val defaultLanguage: String,
    @SerializedName("timeout") @Expose val timeout: Int? = null,
    @SerializedName("has_redirect") @Expose val hasRedirect: Boolean,
    @SerializedName("has_default_card") @Expose val hasDefaultCard: Boolean
)