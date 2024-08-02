package kz.tarlanpayments.storage.androidsdk.noui.data

import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionDescriptionModel
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionStateModel
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionStatusModel
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.BaseResponse
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.TransactionBillRs
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.TransactionColorRs
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.TransactionInfoMainRs
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.TransactionInfoPayFormRs
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.TransactionRs

internal object TarlanMapper {


    fun mapToDescriptionModel(
        transactionInfo: TransactionInfoMainRs,
        transactionStatus: TarlanTransactionStatusModel,
        transactionInfoPayForm: TransactionInfoPayFormRs,
        transactionColor: TransactionColorRs,
        transactionBill: TransactionBillRs?,
    ): TarlanTransactionDescriptionModel {


        return TarlanTransactionDescriptionModel(
            cards = transactionInfo.cards.map { card ->
                TarlanTransactionDescriptionModel.SavedCard(
                    cardToken = card.cardToken,
                    maskedPan = card.maskedPan
                )
            },
            totalAmount = transactionInfo.totalAmount,
            projectID = transactionInfo.projectId,
            type = transactionInfo.availableTypes.toTransactionType(),
            status = transactionStatus,
            hasGooglePay = transactionInfo.availableTypes.any { it.code == TransactionInfoMainRs.TransactionAvailableTypesDto.GooglePay },
            requiredEmail = transactionInfoPayForm.requiredEmail,
            hasEmail = transactionInfoPayForm.hasEmail,
            requiredPhone = transactionInfoPayForm.requiredPhone,
            hasPhone = transactionInfoPayForm.hasPhone,
            hasDefaultCard = transactionInfoPayForm.hasDefaultCard,
            logoFilePath = transactionInfo.logo,
            upperCommissionAmount = transactionInfo.upperCommissionAmount,
            transactionId = transactionInfo.transactionId,
            hasRedirect = transactionInfoPayForm.hasRedirect,
            storeName = transactionInfoPayForm.storeName,
            orderAmount = transactionInfo.orderAmount,
            dateTime = transactionBill?.dateTime,
            acquirerName = transactionBill?.acquirerName,
            paymentOrganization = transactionBill?.paymentOrganization,
            mainFormColor = transactionColor.mainFormColor,
            secondaryFormColor = transactionColor.secondaryFormColor,
            mainInputColor = transactionColor.mainInputColor,
            secondaryInputColor = transactionColor.secondaryInputColor,
            mainTextColor = transactionColor.mainTextColor,
            secondaryTextColor = transactionColor.secondaryTextColor,
            mainTextInputColor = transactionColor.mainTextInputColor,
            inputLabelColor = transactionColor.inputLabelColor,
            timeout = transactionInfoPayForm.timeout
        )
    }

    fun BaseResponse<TransactionRs>.toTransactionState(
        transactionHash: String,
        transactionId: Long
    ): TarlanTransactionStateModel {
        return when (this.result.transactionStatusCode) {
            TransactionRs.Success -> TarlanTransactionStateModel.Success(
                transactionId = transactionId,
                transactionHash = transactionHash
            )

            TransactionRs.ThreeDsWaiting -> TarlanTransactionStateModel.Waiting3DS(
                termUrl = this.result.threeDs?.termUrl ?: "",
                action = this.result.threeDs?.action ?: "",
                params = this.result.threeDs?.params ?: emptyMap(),
                transactionId = transactionId,
                transactionHash = transactionHash
            )

            TransactionRs.Fingerprint -> TarlanTransactionStateModel.FingerPrint(
                methodData = this.result.fingerprint?.methodData ?: "",
                methodUrl = this.result.fingerprint?.methodUrl ?: "",
                transactionId = transactionId,
                transactionHash = transactionHash
            )

            else -> TarlanTransactionStateModel.Error(
                transactionId = transactionId,
                transactionHash = transactionHash,
                message = Exception(this.message)
            )
        }
    }

    fun BaseResponse<TransactionInfoMainRs>.toStatus(): TarlanTransactionStatusModel {
        when (this.result.transactionStatus.code) {
            TransactionInfoMainRs.TransactionStatusDto.SUCCESS -> return TarlanTransactionStatusModel.Success
            TransactionInfoMainRs.TransactionStatusDto.FAIL -> return TarlanTransactionStatusModel.Fail
            TransactionInfoMainRs.TransactionStatusDto.REFUND -> return TarlanTransactionStatusModel.Refund
            TransactionInfoMainRs.TransactionStatusDto.NEW -> return TarlanTransactionStatusModel.New
            else -> return TarlanTransactionStatusModel.Error
        }
    }

    private fun List<TransactionInfoMainRs.TransactionAvailableTypesDto>.toTransactionType(): TarlanTransactionDescriptionModel.TransactionType {
        if (this.any { it.code == TransactionInfoMainRs.TransactionAvailableTypesDto.IN })
            return TarlanTransactionDescriptionModel.TransactionType.In

        if (this.any { it.code == TransactionInfoMainRs.TransactionAvailableTypesDto.OUT })
            return TarlanTransactionDescriptionModel.TransactionType.Out

        if (this.any { it.code == TransactionInfoMainRs.TransactionAvailableTypesDto.CARD_LINK })
            return TarlanTransactionDescriptionModel.TransactionType.CardLink

        throw Exception()
    }
}