package kz.tarlanpayments.storage.androidsdk.sdk.utils

import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.FormController
import java.util.Calendar

internal object ValidationUtils {

    fun isAdditionalFieldsValid(
        formController: FormController,
        email: String,
        phone: String,
    ): Boolean {
        if (formController.isEmailRequired) {
            if (validateEmail(email) != ValidationErrorType.Valid) return false
        }
        if (formController.isShowEmail && email.isNotEmpty()) {
            if (validateEmail(email) != ValidationErrorType.Valid) return false
        }

        if (formController.isPhoneRequired) {
            if (validatePhone(phone) != ValidationErrorType.Valid) return false
        }

        if (formController.isShowPhone && phone.isNotEmpty()) {
            if (validatePhone(phone) != ValidationErrorType.Valid) return false
        }

        return true
    }

    fun isCardFieldsValid(
        formController: FormController,
        cardNumber: String,
        month: String,
        year: String,
        cvv: String,
        cardHolder: String
    ): Boolean {
        return when {
            validateCardNumber(cardNumber) != ValidationErrorType.Valid -> false
            formController.isPanVisible && validateCardNumber(cardNumber) != ValidationErrorType.Valid -> false
            formController.isExpDate && validateExpDate(
                month,
                year
            ) != ValidationErrorType.Valid -> false

            formController.isCvvVisible && validateCvv(cvv) != ValidationErrorType.Valid -> false
            formController.isCardHolderVisible && validateCardHolder(cardHolder) != ValidationErrorType.Valid -> false
            else -> true
        }
    }

    fun validateCardNumber(cardNumber: String): ValidationErrorType {
        return when {
            cardNumber.isEmpty() -> ValidationErrorType.Empty
            cardNumber.length != 16 -> ValidationErrorType.Invalid
            !isValidCardNumber(cardNumber) -> ValidationErrorType.Invalid
            else -> ValidationErrorType.Valid
        }
    }

    fun validateExpDate(month: String, year: String): ValidationErrorType {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR).toString().substring(2)
        val currentMonth = calendar.get(Calendar.MONTH) + 1

        return when {
            month.isEmpty() -> ValidationErrorType.Empty
            month.toInt() > 12 -> ValidationErrorType.Invalid
            year.isEmpty() -> ValidationErrorType.Empty
            year.length != 2 -> ValidationErrorType.Invalid
            year.toInt() < currentYear.toInt() -> ValidationErrorType.Invalid
            year.toInt() >= currentYear.toInt() + 10 -> ValidationErrorType.Invalid
            year.toInt() == currentYear.toInt() && month.toInt() < currentMonth -> ValidationErrorType.Invalid
            else -> ValidationErrorType.Valid
        }
    }

    fun validateCvv(cvv: String): ValidationErrorType {
        return when {
            cvv.isEmpty() -> ValidationErrorType.Empty
            cvv.length != 3 -> ValidationErrorType.Invalid
            cvv.any { !it.isDigit() } -> ValidationErrorType.Invalid
            else -> ValidationErrorType.Valid
        }
    }

    fun validateEmail(email: String): ValidationErrorType {
        return when {
            email.isEmpty() -> ValidationErrorType.Empty
            !isValidEmail(email) -> ValidationErrorType.Invalid
            else -> ValidationErrorType.Valid
        }
    }

    fun validatePhone(phone: String): ValidationErrorType {
        return when {
            phone.isEmpty() -> ValidationErrorType.Empty
            phone.length != 10 -> ValidationErrorType.Invalid
            !isValidPhone(phone) -> ValidationErrorType.Invalid
            else -> ValidationErrorType.Valid
        }
    }

    fun validateCardHolder(cardHolder: String): ValidationErrorType {
        return when {
            cardHolder.isEmpty() -> ValidationErrorType.Empty
            cardHolder.length < 2 -> ValidationErrorType.Invalid
            cardHolder.length > 26 -> ValidationErrorType.Invalid
            !isStringContainOnlyLatinLetter(cardHolder) -> ValidationErrorType.Invalid
            else -> ValidationErrorType.Valid
        }
    }


    private fun isStringContainOnlyLatinLetter(str: String): Boolean {
        return str.all { it in 'A'..'Z' || it in 'a'..'z' || it == ' ' }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPhone(phone: String): Boolean {
        return phone.length == 10 && phone.all { it in '0'..'9' }
    }

    private fun isValidCardNumber(cardNumber: String): Boolean {
        var sum = 0
        var alternate = false
        for (i in cardNumber.length - 1 downTo 0) {
            var n = cardNumber[i].digitToInt() // Преобразование символа в число
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n = (n % 10) + 1
                }
            }
            sum += n
            alternate = !alternate
        }
        return sum % 10 == 0
    }
}