package kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success

import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionDescriptionModel

internal class FormController(
    transactionDescription: TarlanTransactionDescriptionModel,
    isPhoneCanUseGooglePay: Boolean,
) {

    val isCardHolderVisible =
        transactionDescription.type != TarlanTransactionDescriptionModel.TransactionType.Out
    val isPanVisible = true
    val isCvvVisible =
        transactionDescription.type != TarlanTransactionDescriptionModel.TransactionType.Out
    val isExpDate =
        transactionDescription.type != TarlanTransactionDescriptionModel.TransactionType.Out

    val isGooglePlayVisible = transactionDescription.hasGooglePay && isPhoneCanUseGooglePay

    val isSaveCardVisible =
        transactionDescription.type == TarlanTransactionDescriptionModel.TransactionType.In
    val isSavedCardVisible =
        transactionDescription.type != TarlanTransactionDescriptionModel.TransactionType.CardLink

    val isShowEmail = transactionDescription.requiredEmail || transactionDescription.hasEmail
    val isEmailRequired = transactionDescription.requiredEmail
    val isShowPhone = transactionDescription.requiredPhone || transactionDescription.hasPhone
    val isPhoneRequired = transactionDescription.requiredPhone
    val hasDefaultCard = transactionDescription.hasDefaultCard
}