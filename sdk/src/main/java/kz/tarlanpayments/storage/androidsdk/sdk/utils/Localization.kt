package kz.tarlanpayments.storage.androidsdk.sdk.utils

internal object Localization {

    val KeyCard = "card"
    val KeyGoogle = "google_pay"
    val KeyCardNumber = "card_number"
    val KeyCardExpiryDate = "card_expiry"
    val KeyMonth = "month"
    val KeyYear = "year"
    val KeyCvv = "cvv"
    val KeyCardHolder = "card_holder"
    val KeySaveCard = "save_card"
    val KeyPay = "pay"
    val KeyCancel = "cancel"
    val KeyEmail = "email"
    val KeyPhone = "phone"
    val KeyAddNewCard = "add_new_card"
    val KeyRefund = "refund"
    val KeyCardLink = "card_link"

    val KeyOrderNumber = "order_number"
    val KeyAmount = "amount"
    val KeyFee = "fee"
    val KeyBank = "bank"
    val KeyDate = "date"
    val KeySaveBill = "save_bill"
    val KeySendBillToEmail = "send_bill_to_email"

    val KeyBillCardLinkSuccess = "bill_card_link_success"
    val KeyBillCardLinkSuccessSubtitle = "bill_card_link_success_subtitle"
    val KeyBillFail = "bill_fail"
    val KeyBillFailSubtitle = "bill_fail_subtitle"

    val KeyBack = "back"
    val KeyAddCardHint = "add_card_hint"
    val KeyRemovingCard = "removing_card"
    val KeyCommssion = "commission"
    val KeyCardNumberInvalidError = "card_number_invalid_error"
    val KeyCardNumberEmptyError = "card_number_empty_error"
    val KeyCardExpriredDateInvalidError = "card_expiry_date_invalid_error"
    val KeyCardExpriredEmptyError = "card_expiry_date_empty_error"
    val KeyRedirect = "redirect"
    val KeyCvvInvalidError = "cvv_invalid_error"
    val KeyCvvEmptyError = "cvv_empty_error"
    val KeyCardHolderInvalidError = "card_holder_invalid_error"
    val KeyCardHolderEmptyError = "card_holder_empty_error"
    val KeyPhoneInvalidError = "phone_invalid_error"
    val KeyPhoneEmptyError = "phone_empty_error"
    val KeyEmailInvalidError = "email_invalid_error"
    val KeyEmailEmptyError = "email_empty_error"

    val englishStrings = mapOf(
        KeyCard to "Card",
        KeyGoogle to "Pay",
        KeyCardNumber to "Card number:",
        KeyMonth to "Month",
        KeyYear to "Year",
        KeyCvv to "CVV:",
        KeyCardExpiryDate to "Expiration date:",
        KeyCardHolder to "Card holder:",
        KeySaveCard to "Save card details for future payments",
        KeyPay to "Pay",
        KeyCancel to "Cancel",
        KeyEmail to "E-mail:",
        KeyPhone to "Phone:",
        KeyAddNewCard to "+ Add new card",
        KeyOrderNumber to "Order number",
        KeyAmount to "Amount",
        KeyFee to "Fee",
        KeyBank to "Bank",
        KeyDate to "Date",
        KeySaveBill to "Save bill",
        KeySendBillToEmail to "Send bill to email",
        KeyBillCardLinkSuccess to "Card successfully linked.",
        KeyBillFail to "Something went wrong!",
        KeyBillFailSubtitle to "For detailed information, contact technical support:",
        KeyBack to "Back",
        KeyAddCardHint to "Saved cards",
        KeyRemovingCard to "Removing card",
        KeyBillCardLinkSuccessSubtitle to "Your card is saved and will be available for reuse when paying.",
        KeyCommssion to "Commission",
        KeyCardNumberInvalidError to "Card number is not exist",
        KeyCardExpriredDateInvalidError to "Not valid card expiry date",
        KeyRedirect to "Redirecting to merchant page in: %s sec",
        KeyCvvInvalidError to "Invalid CVV",
        KeyCvvEmptyError to "Enter CVV",
        KeyCardHolderInvalidError to "Invalid card holder",
        KeyCardHolderEmptyError to "Enter card holder",
        KeyPhoneInvalidError to "Invalid phone",
        KeyPhoneEmptyError to "Enter phone",
        KeyEmailInvalidError to "Invalid email",
        KeyCardNumberEmptyError to "Enter card number",
        KeyCardExpriredEmptyError to "Enter card expiry date",
        KeyEmailEmptyError to "Enter email",
        KeyRefund to "Refund",
        KeyCardLink to "Привязать карту"
    )
    val russianStrings = mapOf(
        KeyCard to "Card",
        KeyGoogle to "Pay",
        KeyCardNumber to "Номер карты:",
        KeyMonth to "Месяц:",
        KeyYear to "Год:",
        KeyCvv to "CVV:",
        KeyCardExpiryDate to "Срок действия карты:",
        KeyCardHolder to "Фамилия и имя на карте:",
        KeySaveCard to "Запомнить карту",
        KeyPay to "Оплатить",
        KeyCancel to "Отменить",
        KeyEmail to "E-mail:",
        KeyPhone to "Телефон:",
        KeyAddNewCard to "+ Добавить новую карту",
        KeyOrderNumber to "Номер заказа",
        KeyAmount to "Сумма оплаты",
        KeyFee to "Комиссия",
        KeyBank to "Название банка-эквайера",
        KeyDate to "Дата транзакции",
        KeySaveBill to "Сохранить квитанцию",
        KeySendBillToEmail to "Отправить квитанцию на почту",
        KeyBillCardLinkSuccess to "Карта успешно привязана.",
        KeyBillFail to "Что-то пошло не так!",
        KeyBillFailSubtitle to "Для подробной информации обратитесь в техническую поддержку: support@tarlanpayments.kz",
        KeyBack to "Назад",
        KeyAddCardHint to "Сохраненные карты",
        KeyRemovingCard to "Карта удалена",
        KeyBillCardLinkSuccessSubtitle to "Ваша карта сохранена и будет доступна для повторного использование при оплате.",
        KeyCommssion to "Комиссия",
        KeyCardNumberInvalidError to "Такого номера карты не существует",
        KeyCardExpriredDateInvalidError to "Неверный срок действия карты",
        KeyRedirect to "Возврат на страницу магазина через: %s сек",
        KeyCvvInvalidError to "Неверный CVV",
        KeyCvvEmptyError to "Введите CVV",
        KeyCardHolderInvalidError to "Неверный держатель карты",
        KeyCardHolderEmptyError to "Введите держателя карты",
        KeyPhoneInvalidError to "Неверный телефон",
        KeyPhoneEmptyError to "Введите телефон",
        KeyEmailInvalidError to "Неверный формат электронного адреса",
        KeyCardNumberEmptyError to "Введите номер карты",
        KeyCardExpriredEmptyError to "Введите срок действия карты",
        KeyEmailEmptyError to "Введите email",
        KeyRefund to "Вывести",
        KeyCardLink to "Привязать карту"
    )
    val kazakhStrings = mapOf(
        KeyCard to "Карта",
        KeyGoogle to "Pay",
        KeyCardNumber to "Карта нөмірі:",
        KeyMonth to "Ай",
        KeyYear to "Жыл",
        KeyCvv to "CVV",
        KeyCardExpiryDate to "Картаның жарамдылық мерзімі:",
        KeyCardHolder to "Картадағы тегі мен аты:",
        KeySaveCard to "Картаны сақтау",
        KeyPay to "Төлеу",
        KeyCancel to "Болдырмау",
        KeyEmail to "E-mail:",
        KeyPhone to "Телефон:",
        KeyAddNewCard to "+ Жаңа картаны қосу",
        KeyOrderNumber to "Тапсырыс нөмірі",
        KeyAmount to "Төлем сомасы",
        KeyFee to "Комиссия",
        KeyBank to "Эквайринг банкінің атауы",
        KeyDate to "Транзакция күні",
        KeySaveBill to "Квитанцияны сақтау",
        KeySendBillToEmail to "Квитанцияны электрондық поштамен жіберу",
        KeyBillCardLinkSuccess to "Карта сәтті түрде байланылды.",
        KeyBillFail to "Бірде-бір қате пайда болды!",
        KeyBillFailSubtitle to "Толығырақ ақпарат алу үшін техникалық қолдауға хабарласыңыз:",
        KeyBack to "Артқа",
        KeyAddCardHint to "Сақталған карталар",
        KeyRemovingCard to "Картаны жою",
        KeyBillCardLinkSuccessSubtitle to "Сіздің картаныз сақталды және қайталып төлемді аяқтау кезінде қолдануға қолжетімді болады.",
        KeyCommssion to "Комиссия",
        KeyCardNumberInvalidError to "Карта нөмірі дұрыс емес",
        KeyCardExpriredDateInvalidError to "Картаның мерзімі дұрыс емес",
        KeyRedirect to "Дүкен бетіне қайтару: %s секундтан кейін",
        KeyCvvInvalidError to "Дұрыс CVV емес",
        KeyCvvEmptyError to "CVV енгізіңіз",
        KeyCardHolderInvalidError to "Дұрыс картаның иесі",
        KeyCardHolderEmptyError to "Картаның иесін енгізіңіз",
        KeyPhoneInvalidError to "Дұрыс телефон емес",
        KeyPhoneEmptyError to "Телефон нөмірін енгізіңіз",
        KeyEmailInvalidError to "Дұрыс email емес",
        KeyCardNumberEmptyError to "Карта нөмірін енгізіңіз",
        KeyCardExpriredEmptyError to "Картаның мерзімін енгізіңіз",
        KeyEmailEmptyError to "Email енгізіңіз",
        KeyRefund to "Шығару",
        KeyCardLink to "Картаны байланыстыру"
    )


    fun getString(key: String, locale: String): String {
        return when (locale) {
            "EN" -> englishStrings[key] ?: ""
            "RU" -> russianStrings[key] ?: ""
            "KK" -> kazakhStrings[key] ?: ""
            "KZ" -> kazakhStrings[key] ?: ""
            else -> russianStrings[key] ?: ""
        }
    }
}