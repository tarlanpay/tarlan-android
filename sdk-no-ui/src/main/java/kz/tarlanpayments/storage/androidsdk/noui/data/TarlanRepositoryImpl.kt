package kz.tarlanpayments.storage.androidsdk.noui.data

import com.google.gson.Gson
import kz.tarlanpayments.storage.androidsdk.noui.TarlanInstance
import kz.tarlanpayments.storage.androidsdk.noui.TarlanRepository
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionDescriptionModel
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionStateModel
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionStatusModel
import kz.tarlanpayments.storage.androidsdk.noui.data.TarlanMapper.toStatus
import kz.tarlanpayments.storage.androidsdk.noui.data.TarlanMapper.toTransactionState
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.BaseResponse
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.CardLinkRq
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.DeleteCardRq
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.GooglePayRq
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.InFromSavedRq
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.InRq
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.OutFromSavedRq
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.PayoutRq
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.ResumeTransactionRq
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.TransactionColorRs
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.TransactionInfoMainRs
import kz.tarlanpayments.storage.androidsdk.noui.data.dto.TransactionInfoPayFormRs

internal class TarlanRepositoryImpl : TarlanRepository {

    private val apiService: TarlanApi = TarlanInstance.retrofit
    private val mrapiService: TarlanApi = TarlanInstance.mrapi
    private val gson: Gson = Gson()

    override suspend fun inRq(
        transactionId: Long,
        hash: String,
        cardNumber: String,
        cvv: String,
        month: String,
        year: String,
        cardHolder: String,
        email: String?,
        phone: String?,
        savaCard: Boolean
    ): TarlanTransactionStateModel {
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
        ).toTransactionState(transactionHash = hash, transactionId = transactionId)
    }

    override suspend fun cardLink(
        transactionId: Long,
        hash: String,
        cardNumber: String,
        cvv: String,
        month: String,
        year: String,
        cardHolder: String,
        email: String?,
        phone: String?,
    ): TarlanTransactionStateModel {
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
        ).toTransactionState(transactionHash = hash, transactionId = transactionId)
    }

    override suspend fun outRq(
        transactionId: Long,
        hash: String,
        cardNumber: String,
        email: String?,
        phone: String?,
    ): TarlanTransactionStateModel {
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
        ).toTransactionState(transactionHash = hash, transactionId = transactionId)
    }

    override suspend fun inFromSavedRq(
        transactionId: Long,
        hash: String,
        encryptedId: String,
        email: String?,
        phone: String?,
    ): TarlanTransactionStateModel {
        return apiService.payInFromSaved(
            InFromSavedRq(
                transactionId = transactionId,
                userPhone = phone,
                userEmail = email,
                encryptedId = encryptedId,
                transactionHash = hash,
            )
        ).toTransactionState(transactionHash = hash, transactionId = transactionId)
    }

    override suspend fun outFromSaved(
        transactionId: Long,
        hash: String,
        encryptedId: String,
        email: String?,
        phone: String?,
    ): TarlanTransactionStateModel {
        return apiService.outFromSaved(
            OutFromSavedRq(
                transactionId = transactionId,
                userPhone = phone,
                userEmail = email,
                encryptedId = encryptedId,
                transactionHash = hash,
            )
        ).toTransactionState(transactionHash = hash, transactionId = transactionId)
    }

    override suspend fun googlePay(
        transactionId: Long,
        hash: String,
        paymentMethodData: Map<String, Any>
    ): TarlanTransactionStateModel {
        return apiService.googlePay(
            GooglePayRq(
                transactionId = transactionId,
                transactionHash = hash,
                paymentMethodData = paymentMethodData,
            )
        ).toTransactionState(transactionHash = hash, transactionId = transactionId)
    }

    override suspend fun getTransactionStatus(
        transactionId: Long,
        hash: String
    ): TarlanTransactionStatusModel {
        return apiService.getTransaction(
            transactionId = transactionId,
            hash = hash
        ).toStatus()
    }

    override suspend fun resumeTransaction(
        transactionId: Long,
        hash: String,
    ): TarlanTransactionStateModel {
        return apiService.resumeTransaction(
            body = ResumeTransactionRq(
                transactionId = transactionId,
                transactionHash = hash
            )
        ).toTransactionState(transactionHash = hash, transactionId = transactionId)
    }

    override suspend fun deleteCard(
        transactionId: Long,
        transactionHash: String,
        projectId: Long,
        encryptedCardId: String,
    ) {
        apiService
            .deleteCard(
                body = DeleteCardRq(
                    transactionId = transactionId,
                    transactionHash = transactionHash,
                    encryptedCardId = encryptedCardId,
                    projectId = projectId
                )
            )
    }

    override suspend fun getTransactionDescription(
        transactionId: Long,
        hash: String
    ): TarlanTransactionDescriptionModel {
        val transactionStatus = getTransactionStatus(transactionId, hash)
        val transactionInfo = getTransactionInfo(transactionId, hash).result

        val transactionBill = when (transactionStatus) {
            TarlanTransactionStatusModel.Success, TarlanTransactionStatusModel.Refund ->
                apiService.getBill(transactionId = transactionId, hash = hash).result

            else -> null
        }
        val transactionColor = getColors(
            projectId = transactionInfo.projectId,
            merchantId = transactionInfo.merchantId,
        ).result
        val transactionInfoPayForm = getPayForm(
            projectId = transactionInfo.projectId,
            merchantId = transactionInfo.merchantId
        ).result


        return TarlanMapper.mapToDescriptionModel(
            transactionInfo = transactionInfo,
            transactionStatus = transactionStatus,
            transactionInfoPayForm = transactionInfoPayForm,
            transactionColor = transactionColor,
            transactionBill = transactionBill
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

    private suspend fun getTransactionInfo(
        transactionId: Long,
        hash: String,
    ): BaseResponse<TransactionInfoMainRs> {
        return apiService.getTransaction(
            transactionId = transactionId,
            hash = hash
        )
    }
}