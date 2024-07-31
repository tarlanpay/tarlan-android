package kz.tarlanpayments.storage.androidsdk.noui

interface TarlanRepository {

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
    ): TarlanTransactionStateModel

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
    ): TarlanTransactionStateModel

    suspend fun outRq(
        transactionId: Long,
        hash: String,
        cardNumber: String,
        email: String? = null,
        phone: String? = null,
    ): TarlanTransactionStateModel

    suspend fun inFromSavedRq(
        transactionId: Long,
        hash: String,
        encryptedId: String,
        email: String? = null,
        phone: String? = null,
    ): TarlanTransactionStateModel

    suspend fun outFromSaved(
        transactionId: Long,
        hash: String,
        encryptedId: String,
        email: String? = null,
        phone: String? = null,
    ): TarlanTransactionStateModel

    suspend fun googlePay(
        transactionId: Long,
        hash: String,
        paymentMethodData: Map<String, Any>
    ): TarlanTransactionStateModel

    suspend fun getTransactionStatus(
        transactionId: Long,
        hash: String
    ): TarlanTransactionStatusModel

    suspend fun resumeTransaction(
        transactionId: Long,
        hash: String,
    ): TarlanTransactionStateModel

    suspend fun deleteCard(
        transactionId: Long,
        transactionHash: String,
        projectId: Long,
        encryptedCardId: String,
    )

    suspend fun getTransactionDescription(
        transactionId: Long,
        hash: String
    ): TarlanTransactionDescriptionModel
}