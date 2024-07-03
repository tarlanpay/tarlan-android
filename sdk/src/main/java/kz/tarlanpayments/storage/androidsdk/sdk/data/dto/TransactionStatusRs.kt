package kz.tarlanpayments.storage.androidsdk.sdk.data.dto

internal data class TransactionStatusRs(
    val transactionColor: TransactionColorRs,
    val transactionPayForm: TransactionInfoPayFormRs,
    val transactionInfoMain: TransactionInfoMainRs,
    val transactionBillRs: TransactionBillRs? = null
)