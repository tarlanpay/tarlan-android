package kz.tarlanpayments.storage.androidsdk.noui

sealed interface TarlanTransactionStateModel {

    data class Success(
        val transactionId: Long,
        val transactionHash: String
    ) : TarlanTransactionStateModel

    data class Waiting3DS(
        val termUrl: String,
        val action: String,
        val params: Map<String, String>,
        val transactionId: Long,
        val transactionHash: String
    ) : TarlanTransactionStateModel

    data class FingerPrint(
        val methodData: String,
        val methodUrl: String,
        val transactionId: Long,
        val transactionHash: String
    ) : TarlanTransactionStateModel

    data class Error(
        val transactionId: Long,
        val transactionHash: String,
        val message: Exception
    ) : TarlanTransactionStateModel
}