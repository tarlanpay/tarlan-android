package kz.tarlanpayments.storage.androidsdk.sdk.utils

enum class ValidationErrorType {
    Empty, Valid, Invalid
}

fun ValidationErrorType.cardValidationErrorText(currentLocale: String): String {
    return when (this) {
        ValidationErrorType.Invalid -> {
            Localization.getString(
                Localization.KeyCardNumberInvalidError,
                locale = currentLocale
            )
        }
        ValidationErrorType.Empty -> {
            Localization.getString(
                Localization.KeyCardNumberEmptyError,
                locale = currentLocale
            )
        }
        ValidationErrorType.Valid -> {
            return ""
        }
    }
}

fun ValidationErrorType.monthValidationErrorText(currentLocale: String): String {
    return when (this) {
        ValidationErrorType.Invalid -> {
            Localization.getString(
                Localization.KeyCardExpriredDateInvalidError,
                locale = currentLocale
            )
        }
        ValidationErrorType.Empty -> {
            Localization.getString(
                Localization.KeyCardExpriredEmptyError,
                locale = currentLocale
            )
        }
        ValidationErrorType.Valid -> {
            return ""
        }
    }
}

fun ValidationErrorType.cvvValidationErrorText(currentLocale: String): String {
    return when (this) {
        ValidationErrorType.Invalid -> {
            Localization.getString(
                Localization.KeyCvvInvalidError,
                locale = currentLocale
            )
        }
        ValidationErrorType.Empty -> {
            Localization.getString(
                Localization.KeyCvvEmptyError,
                locale = currentLocale
            )
        }
        ValidationErrorType.Valid -> {
            return ""
        }
    }
}

fun ValidationErrorType.cardHolderValidationErrorText(currentLocale: String): String {
    return when (this) {
        ValidationErrorType.Invalid -> {
            Localization.getString(
                Localization.KeyCardHolderInvalidError,
                locale = currentLocale
            )
        }
        ValidationErrorType.Empty -> {
            Localization.getString(
                Localization.KeyCardHolderEmptyError,
                locale = currentLocale
            )
        }
        ValidationErrorType.Valid -> {
            return ""
        }
    }
}

fun ValidationErrorType.phoneValidationErrorText(currentLocale: String): String {
    return when (this) {
        ValidationErrorType.Invalid -> {
            Localization.getString(
                Localization.KeyPhoneInvalidError,
                locale = currentLocale
            )
        }
        ValidationErrorType.Empty -> {
            Localization.getString(
                Localization.KeyPhoneEmptyError,
                locale = currentLocale
            )
        }
        ValidationErrorType.Valid -> {
            return ""
        }
    }
}

fun ValidationErrorType.emailValidationErrorText(currentLocale: String): String {
    return when (this) {
        ValidationErrorType.Invalid -> {
            Localization.getString(
                Localization.KeyEmailInvalidError,
                locale = currentLocale
            )
        }
        ValidationErrorType.Empty -> {
            Localization.getString(
                Localization.KeyEmailEmptyError,
                locale = currentLocale
            )
        }
        ValidationErrorType.Valid -> {
            return ""
        }
    }
}