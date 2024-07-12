package kz.tarlanpayments.storage.androidsdk.sdk.data

import com.google.gson.Gson
import kz.tarlanpayments.storage.androidsdk.sdk.DepsHolder
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.BaseResponse
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.CardLinkRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.DeleteCardRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.GooglePayRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.InFromSavedRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.InRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.OutFromSavedRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.PayoutRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.ResumeTransactionRq
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionBillRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionColorRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionInfoMainRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionInfoPayFormRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionStatusRs

internal class TarlanRepository {

    private val apiService: TarlanApi = DepsHolder.retrofit
    private val mrapiService: TarlanApi = DepsHolder.mrapi
    private val gson: Gson = Gson()

    suspend fun inRq(
        transactionId: Long,
        hash: String,
        cardNumber: String,
        cvv: String,
        month: String,
        year: String,
        cardHolder: String,
        email: String? = null,
        phone: String? = null,
        savaCard: Boolean = false
    ): BaseResponse<TransactionRs> {
        val publicKey = apiService.getPublicKey().result
        val valueToEncrypt = gson.toJson(
            InRq.ValueToEncrypt(
                pan = cardNumber,
                cvc = cvv,
                month = month,
                year = year,
                fullName = cardHolder
            )
        )
        val encryptedCard = RSAEncryption.loadPublicKeyAndEncryptData(
            data = valueToEncrypt,
            pemPublicKey = publicKey
        )

        return apiService.payIn(
            InRq(
                transactionId = transactionId,
                userPhone = phone,
                userEmail = email,
                encryptedCard = encryptedCard,
                transactionHash = hash,
                save = savaCard
            )
        )
    }

    suspend fun cardLink(
        transactionId: Long,
        hash: String,
        cardNumber: String,
        cvv: String,
        month: String,
        year: String,
        cardHolder: String,
        email: String? = null,
        phone: String? = null,
    ): BaseResponse<TransactionRs> {
        val publicKey = apiService.getPublicKey().result
        val valueToEncrypt = gson.toJson(
            InRq.ValueToEncrypt(
                pan = cardNumber,
                cvc = cvv,
                month = month,
                year = year,
                fullName = cardHolder
            )
        )
        val encryptedCard = RSAEncryption.loadPublicKeyAndEncryptData(
            data = valueToEncrypt,
            pemPublicKey = publicKey
        )

        return apiService.cardLink(
            CardLinkRq(
                transactionId = transactionId,
                fullName = cardHolder,
                userPhone = phone,
                userEmail = email,
                encryptedCard = encryptedCard,
                transactionHash = hash,
            )
        )
    }

    suspend fun outRq(
        transactionId: Long,
        hash: String,
        cardNumber: String,
        email: String? = null,
        phone: String? = null,
    ): BaseResponse<TransactionRs> {
        val publicKey = apiService.getPublicKey().result
        val valueToEncrypt = gson.toJson(PayoutRq.ValueToEncrypt(pan = cardNumber))
        val encryptedCard = RSAEncryption.loadPublicKeyAndEncryptData(
            data = valueToEncrypt,
            pemPublicKey = publicKey
        )

        return apiService.payOut(
            PayoutRq(
                transactionId = transactionId,
                userPhone = phone,
                userEmail = email,
                encryptedPan = encryptedCard,
                transactionHash = hash,
            )
        )
    }

    suspend fun inFromSavedRq(
        transactionId: Long,
        hash: String,
        encryptedId: String,
        email: String? = null,
        phone: String? = null,
    ): BaseResponse<TransactionRs> {
        return apiService.payInFromSaved(
            InFromSavedRq(
                transactionId = transactionId,
                userPhone = phone,
                userEmail = email,
                encryptedId = encryptedId,
                transactionHash = hash,
            )
        )
    }

    suspend fun outFromSaved(
        transactionId: Long,
        hash: String,
        encryptedId: String,
        email: String? = null,
        phone: String? = null,
    ): BaseResponse<TransactionRs> {
        return apiService.outFromSaved(
            OutFromSavedRq(
                transactionId = transactionId,
                userPhone = phone,
                userEmail = email,
                encryptedId = encryptedId,
                transactionHash = hash,
            )
        )
    }

    suspend fun googlePay(
        transactionId: Long,
        hash: String,
        paymentMethodData: Map<String, Any>
    ): BaseResponse<TransactionRs> {
        return apiService.googlePay(
            GooglePayRq(
                transactionId = transactionId,
                transactionHash = hash,
                paymentMethodData = paymentMethodData,
            )
        )
    }

    suspend fun getTransactionInfo(
        transactionId: Long,
        hash: String,
    ): BaseResponse<TransactionInfoMainRs> {
        return apiService.getTransaction(
            transactionId = transactionId,
            hash = hash
        )
    }

    suspend fun getTransactionStatus(
        transactionId: Long,
        hash: String
    ): TransactionStatusRs {
        val transactionInfoDto = getTransactionInfo(transactionId, hash)
        var transactionBillRs: TransactionBillRs? = null

        if (!transactionInfoDto.status) {
            throw Exception(transactionInfoDto.message)
        }

        if (transactionInfoDto.result.transactionStatus.code == TransactionInfoMainRs.TransactionStatusDto.REFUND ||
            transactionInfoDto.result.transactionStatus.code == TransactionInfoMainRs.TransactionStatusDto.SUCCESS
        ) {
            transactionBillRs = apiService.getBill(
                transactionId = transactionId,
                hash = hash
            ).result
        }

        val transactionInfo = transactionInfoDto.result

        val colorsDto = getColors(
            projectId = transactionInfoDto.result.projectId,
            merchantId = transactionInfoDto.result.merchantId,
        ).result

        val payFormDto = getPayForm(
            projectId = transactionInfoDto.result.projectId,
            merchantId = transactionInfoDto.result.merchantId,
        ).result

        return TransactionStatusRs(
            transactionColor = colorsDto,
            transactionPayForm = payFormDto,
            transactionInfoMain = transactionInfo,
            transactionBillRs = transactionBillRs
        )
    }

    suspend fun resumeTransaction(
        transactionId: Long,
        hash: String,
    ): BaseResponse<TransactionRs> {
        return apiService.resumeTransaction(
            body = ResumeTransactionRq(
                transactionId = transactionId,
                transactionHash = hash
            )
        )
    }

    suspend fun deleteCard(
        transactionId: Long,
        transactionHash: String,
        projectId: Long,
        encryptedCardId: String,
    ): BaseResponse<Unit> {
        return apiService
            .deleteCard(
                body = DeleteCardRq(
                    transactionId = transactionId,
                    transactionHash = transactionHash,
                    encryptedCardId = encryptedCardId,
                    projectId = projectId
                )
            )
    }

    private suspend fun getColors(
        projectId: Long,
        merchantId: Long,
    ): BaseResponse<TransactionColorRs> {
        return mrapiService.getColors(
            projectId = projectId,
            merchantId = merchantId,
        )
    }

    private suspend fun getPayForm(
        projectId: Long,
        merchantId: Long,
    ): BaseResponse<TransactionInfoPayFormRs> {
        return mrapiService.getPayForm(
            projectId = projectId,
            merchantId = merchantId,
        )
    }
}