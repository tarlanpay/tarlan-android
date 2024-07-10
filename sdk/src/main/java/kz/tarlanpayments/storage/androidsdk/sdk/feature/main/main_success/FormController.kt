package kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success

import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionInfoMainRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionInfoPayFormRs


internal class FormController(
    transactionInfoMainRs: TransactionInfoMainRs,
    transactionInfoPayFormRs: TransactionInfoPayFormRs,
    isPhoneCanUseGooglePay: Boolean,
) {

    val isCardHolderVisible =
        transactionInfoMainRs.transactionType.code != TransactionInfoMainRs.TransactionTypeDto.OUT

    val isPanVisible = true
    val isCvvVisible =
        transactionInfoMainRs.transactionType.code != TransactionInfoMainRs.TransactionTypeDto.OUT

    val isExpDate =
        transactionInfoMainRs.transactionType.code != TransactionInfoMainRs.TransactionTypeDto.OUT

    val isGooglePlayVisible = isPhoneCanUseGooglePay &&
            transactionInfoMainRs.transactionType.code != TransactionInfoMainRs.TransactionTypeDto.CARD_LINK
            &&
            transactionInfoMainRs.availableTypes.find { it.code == TransactionInfoMainRs.TransactionAvailableTypesDto.GooglePay } != null

    val isSaveCardVisible =
        transactionInfoMainRs.transactionType.code == TransactionInfoMainRs.TransactionTypeDto.IN
    val isSavedCardVisible =
        transactionInfoMainRs.cards.isNotEmpty() && transactionInfoMainRs.transactionType.code != TransactionInfoMainRs.TransactionTypeDto.CARD_LINK

    val isShowEmail = transactionInfoPayFormRs.hasEmail
    val isEmailRequired = transactionInfoPayFormRs.requiredEmail
    val isShowPhone = transactionInfoPayFormRs.hasPhone
    val isPhoneRequired = transactionInfoPayFormRs.requiredPhone
    val hasDefaultCard =
        transactionInfoPayFormRs.hasDefaultCard && transactionInfoMainRs.cards.isNotEmpty()
}