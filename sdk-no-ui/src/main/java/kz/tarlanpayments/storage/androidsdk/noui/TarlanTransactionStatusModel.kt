package kz.tarlanpayments.storage.androidsdk.noui

enum class TarlanTransactionStatusModel {
    Success, Fail, Refund, Error, New;

    fun isTransactionCompleted(): Boolean {
        return this != New
    }
}
