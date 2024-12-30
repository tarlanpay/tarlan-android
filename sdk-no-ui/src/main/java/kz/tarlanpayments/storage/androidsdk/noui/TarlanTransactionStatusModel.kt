package kz.tarlanpayments.storage.androidsdk.noui

enum class TarlanTransactionStatusModel {
    Success, Fail, Refund, Error, New, AvailableTypes, Resume;

    fun isTransactionCompleted(): Boolean {
        return this != New
    }

    fun isNeedToResume(): Boolean {
        return this == Resume
    }
}
