package kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kz.tarlanpayments.storage.androidsdk.sdk.GooglePayFacade
import kz.tarlanpayments.storage.androidsdk.sdk.TarlanActivity
import kz.tarlanpayments.storage.androidsdk.sdk.TarlanScreens
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionColorRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionInfoMainRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionInfoPayFormRs
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.classic.MainSuccessClassic
import org.json.JSONObject

@Composable
internal fun MainSuccessView(
    transactionId: Long,
    transactionHash: String,
    transactionInfoMainRs: TransactionInfoMainRs,
    transactionInfoPayFormRs: TransactionInfoPayFormRs,
    transactionColorRs: TransactionColorRs,
    fragment: Fragment,
    googlePayFacade: GooglePayFacade
) {

    var isGooglePayClickEnabled by remember { mutableStateOf(true) }
    var isPhoneCanUserGooglePay by remember { mutableStateOf(false) }
    var isGooglePayNow by remember { mutableStateOf(false) }

    val viewModel = viewModel<MainSuccessViewModel>(
        factory = MainSuccessViewModel.MainSuccessViewModelFactory(
            transactionInfoMainRs = transactionInfoMainRs,
            transactionId = transactionId,
            transactionHash = transactionHash
        )
    )
    val controller = MainSuccessController(
        transactionInfoMainRs = transactionInfoMainRs,
        transactionInfoPayFormRs = transactionInfoPayFormRs,
        isPhoneCanUseGooglePay = isPhoneCanUserGooglePay,
    )

    val focusManager = LocalFocusManager.current
    val focusRequesters = remember {
        mapOf(
            "cardNumber" to FocusRequester(),
            "monthNumber" to FocusRequester(),
            "yearNumber" to FocusRequester(),
            "cvvNumber" to FocusRequester(),
            "cardHolder" to FocusRequester(),
            "email" to FocusRequester(),
            "phone" to FocusRequester()
        )
    }
    val firstCard = transactionInfoMainRs.cards.firstOrNull()?.maskedPan

    // region text fields value
    var cardNumberTextFieldValue by remember { mutableStateOf(TextFieldValue((if (controller.hasDefaultCard) firstCard!! else ""))) }
    var monthNumberTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var yearNumberTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var cvvTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var emailTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var phoneTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var cardHolderTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    // endregion

    // region errors
    var cardNumberError by remember { mutableStateOf(ValidationErrorType.Valid) }
    var cardExpError by remember { mutableStateOf(ValidationErrorType.Valid) }
    var cvvNumberError by remember { mutableStateOf(ValidationErrorType.Valid) }
    var emailError by remember { mutableStateOf(ValidationErrorType.Valid) }
    var phoneError by remember { mutableStateOf(ValidationErrorType.Valid) }
    var cardHolderError by remember { mutableStateOf(ValidationErrorType.Valid) }
    // endregion

    var isSavedCardClicked by remember { mutableStateOf(controller.hasDefaultCard) }
    var savedCardNumber by remember {
        mutableStateOf(TextFieldValue((if (controller.hasDefaultCard) firstCard!! else "")))
    }
    var isSavedCardToken by remember { mutableStateOf((if (controller.hasDefaultCard) transactionInfoMainRs.cards.first().cardToken else "")) }

    var isSaveCard by remember { mutableStateOf(false) }

    val state by viewModel.viewState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                is MainSuccessEffect.ShowError -> {
                    TarlanActivity.router.newRootScreen(
                        TarlanScreens.Status(
                            transactionId = it.transactionId,
                            hash = it.transactionHash
                        )
                    )
                }

                is MainSuccessEffect.ShowSuccess -> {
                    TarlanActivity.router.newRootScreen(
                        TarlanScreens.Status(
                            it.transactionId,
                            it.transactionHash
                        )
                    )
                }

                is MainSuccessEffect.Show3ds -> {
                    TarlanActivity.router.newRootScreen(
                        TarlanScreens.ThreeDs(
                            termUrl = it.termUrl,
                            action = it.action,
                            transactionId = it.transactionId,
                            transactionHash = it.transactionHash,
                            params = it.params,
                        )
                    )
                }

                is MainSuccessEffect.ShowFingerprint -> {
                    TarlanActivity.router.newRootScreen(
                        TarlanScreens.Fingerprint(
                            methodData = it.methodData,
                            action = it.action,
                            transactionId = it.transactionId,
                            transactionHash = it.transactionHash
                        )
                    )
                }
            }
        }
    }


    fun isAdditionalFieldsFilled(): Boolean {
        if (isGooglePayNow) {
            return true
        }
        if (controller.isEmailRequired) {
            if (!(emailTextFieldValue.text.isNotEmpty() && emailTextFieldValue.text.validateEmail()))
                return false
        }

        if (controller.isShowEmail && emailTextFieldValue.text.isNotEmpty()) {
            if (!emailTextFieldValue.text.validateEmail())
                return false
        }

        if (controller.isPhoneRequired) {
            if (!(phoneTextFieldValue.text.isNotEmpty() && phoneTextFieldValue.text.validatePhone()))
                return false
        }

        if (controller.isShowPhone && phoneTextFieldValue.text.isNotEmpty()) {
            if (!(phoneTextFieldValue.text.validatePhone())) {
                return false
            }
        }

        return true
    }

    fun isCardRelatedFieldsFilled(): Boolean {

        if (isGooglePayNow)
            return true

        if (controller.isCardHolderVisible) {
            if (cardHolderTextFieldValue.text.isEmpty()) {
                return false
            }
        }

        if (controller.isPanVisible) {
            if (!(cardNumberTextFieldValue.text.length == 16 && cardNumberTextFieldValue.text.validateLunaCardNumber())) {
                return false
            }
        }

        if (controller.isCvvVisible) {
            if (cvvTextFieldValue.text.length != 3) {
                return false
            }
        }

        if (controller.isExpDate) {
            if (!(monthNumberTextFieldValue.text.isNotEmpty() && monthNumberTextFieldValue.text.toInt() <= 12 && yearNumberTextFieldValue.text.isNotEmpty() &&
                        yearNumberTextFieldValue.text.toInt() >= 24 && yearNumberTextFieldValue.text.toInt() <= 99)
            ) {
                return false
            }
        }

        return true
    }

    fun isSavedCardSelected(): Boolean {
        if (isGooglePayNow) {
            return true
        }
        return isSavedCardClicked && isSavedCardToken.isNotEmpty()
    }

    fun canSend(): Boolean {
        return isAdditionalFieldsFilled() && (isCardRelatedFieldsFilled() || isSavedCardSelected())
    }

    fun isGooglePayButtonEnabled(): Boolean {
        return isAdditionalFieldsFilled() && isGooglePayClickEnabled
    }

    fun validateCardNumber() {
        cardNumberError = when {
            cardNumberTextFieldValue.text.isEmpty() -> ValidationErrorType.Empty
            cardNumberTextFieldValue.text.length != 16 -> ValidationErrorType.Invalid
            !cardNumberTextFieldValue.text.validateLunaCardNumber() -> ValidationErrorType.Invalid
            else -> ValidationErrorType.Valid
        }
    }

    fun validateExpDate() {
        cardExpError = when {
            monthNumberTextFieldValue.text.isEmpty() -> ValidationErrorType.Empty
            monthNumberTextFieldValue.text.toInt() > 12 -> ValidationErrorType.Invalid
            yearNumberTextFieldValue.text.isEmpty() -> ValidationErrorType.Empty
            yearNumberTextFieldValue.text.length != 2 -> ValidationErrorType.Invalid
            yearNumberTextFieldValue.text.toInt() < 23 -> ValidationErrorType.Invalid
            yearNumberTextFieldValue.text.toInt() >= 99 -> ValidationErrorType.Invalid
            else -> ValidationErrorType.Valid
        }
    }

    fun validateCvv() {
        cvvNumberError = when {
            cvvTextFieldValue.text.isEmpty() -> ValidationErrorType.Empty
            cvvTextFieldValue.text.length != 3 -> ValidationErrorType.Invalid
            else -> ValidationErrorType.Valid
        }
    }

    fun validateEmail() {
        emailError = when {
            emailTextFieldValue.text.isEmpty() -> ValidationErrorType.Empty
            emailTextFieldValue.text.isNotEmpty() && !emailTextFieldValue.text.validateEmail() -> ValidationErrorType.Invalid
            else -> ValidationErrorType.Valid
        }
    }

    fun validatePhone() {
        phoneError = when {
            phoneTextFieldValue.text.isEmpty() -> ValidationErrorType.Empty
            phoneTextFieldValue.text.isNotEmpty() && !phoneTextFieldValue.text.validatePhone() -> ValidationErrorType.Invalid
            else -> ValidationErrorType.Valid
        }
    }

    fun validateCardHolder() {
        cardHolderError = when {
            cardHolderTextFieldValue.text.isEmpty() -> ValidationErrorType.Empty
            cardHolderTextFieldValue.text.length < 2 -> ValidationErrorType.Invalid
            cardHolderTextFieldValue.text.length > 26 -> ValidationErrorType.Invalid
            !cardHolderTextFieldValue.text.isLatinLetter() -> ValidationErrorType.Invalid
            else -> ValidationErrorType.Valid
        }
    }


    fun validateFields() {
        if (controller.isPanVisible) {
            validateCardNumber()
        }
        if (controller.isCvvVisible) {
            validateCvv()
        }
        if (controller.isExpDate) {
            validateExpDate()
        }
        if (controller.isEmailRequired || (emailTextFieldValue.text.isNotEmpty() && controller.isShowEmail)) {
            validateEmail()
        }
        if (controller.isPhoneRequired || (phoneTextFieldValue.text.isNotEmpty() && controller.isShowPhone)) {
            validatePhone()
        }
        if (controller.isCardHolderVisible) {
            validateCardHolder()
        }
    }

    fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson()
        val paymentMethodData =
            JSONObject(paymentInformation).getJSONObject("paymentMethodData")
        viewModel.setAction(
            MainSuccessAction.FromGooglePay(
                paymentMethodData = jsonToMap(paymentMethodData)
            )
        )
    }

    val resolvePaymentForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result: ActivityResult ->
        when (result.resultCode) {
            Activity.RESULT_OK ->
                result.data?.let { intent ->
                    PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                }

            Activity.RESULT_CANCELED -> {
                // Пользователь отменил попытку платежа
            }
        }
    }

    fun requestGooglePay(
        transactionInfoMainRs: TransactionInfoMainRs,
        googlePayFacade: GooglePayFacade,
    ) {
        isGooglePayClickEnabled = false
        val price = transactionInfoMainRs.totalAmount
        val paymentDataRequestJson = googlePayFacade.getPaymentDataRequest(price) ?: return
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        val task = googlePayFacade.paymentsClient!!.loadPaymentData(request)
        task.addOnCompleteListener { completedTask ->
            if (completedTask.isSuccessful) {
                completedTask.result.let(::handlePaymentSuccess)
            } else {
                when (val exception = completedTask.exception) {
                    is ResolvableApiException -> {
                        resolvePaymentForResult.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    }

                    is ApiException -> {
//                        handleError(exception.statusCode, exception.message)
                    }

                    else -> {
                    }
                }
            }

            isGooglePayClickEnabled = true
        }
        task.addOnCanceledListener {
            isGooglePayClickEnabled = true
        }
    }

    fun onPaySendAction() {
        if (!isSavedCardClicked) {
            viewModel.setAction(
                MainSuccessAction.FromInterface(
                    cardNumber = cardNumberTextFieldValue.text,
                    cvv = cvvTextFieldValue.text,
                    month = monthNumberTextFieldValue.text,
                    year = yearNumberTextFieldValue.text,
                    email = emailTextFieldValue.text,
                    phone = phoneTextFieldValue.text,
                    cardHolder = cardHolderTextFieldValue.text,
                    saveCard = isSaveCard
                )
            )
        } else {
            viewModel.setAction(
                MainSuccessAction.FromSavedCard(
                    encryptedId = isSavedCardToken,
                    email = emailTextFieldValue.text,
                    phone = phoneTextFieldValue.text
                )
            )
        }
    }

    MainSuccessClassic(
        fragmentManager = fragment.childFragmentManager,
        focusRequester = focusRequesters,
        transactionId = transactionId,
        transactionInfoMainRs = transactionInfoMainRs,
        transactionInfoPayFormRs = transactionInfoPayFormRs,
        transactionColorRs = transactionColorRs,
        mainSuccessController = controller,
        cardNumberTextFieldValue = cardNumberTextFieldValue,
        onCardNumberChanged = {
            val filter = it.text.filter { it.isDigit() }

            if (filter.length < 17) {
                cardNumberTextFieldValue = it.copy(text = filter)
            }

            if (cardNumberTextFieldValue.text.length == 16) {
                focusRequesters["monthNumber"]?.requestFocus()
            }

            cardNumberError = ValidationErrorType.Valid
        },
        monthNumberTextFieldValue = monthNumberTextFieldValue,
        onMonthNumberChanged = {
            val filter = it.text.filter { it.isDigit() }

            if (filter.length < 3) {
                monthNumberTextFieldValue = it.copy(text = filter)
            }

            if (monthNumberTextFieldValue.text.length == 2) {
                focusRequesters["yearNumber"]?.requestFocus()
            }

            cardExpError = ValidationErrorType.Valid
        },
        yearNumberTextFieldValue = yearNumberTextFieldValue,
        onYearNumberChanged = {
            val filter = it.text.filter { it.isDigit() }

            if (filter.length < 3) {
                yearNumberTextFieldValue = it.copy(text = filter)
            }

            if (yearNumberTextFieldValue.text.length == 2) {
                focusRequesters["cvvNumber"]?.requestFocus()
            }

            cardExpError = ValidationErrorType.Valid
        },
        cvvNumberTextFieldValue = cvvTextFieldValue,
        onCvvNumberChanged = {
            val filtered = it.text.filter { it.isDigit() }

            if (filtered.length < 4) {
                cvvTextFieldValue = it.copy(text = filtered)
            }

            if (cvvTextFieldValue.text.length == 3) {
                if (controller.isCardHolderVisible) {
                    focusRequesters["cardHolder"]?.requestFocus()
                } else if (controller.isShowEmail) {
                    focusRequesters["email"]?.requestFocus()
                } else if (controller.isShowPhone) {
                    focusRequesters["phone"]?.requestFocus()
                } else {
                    focusManager.clearFocus()
                }
            }

            cvvNumberError = ValidationErrorType.Valid
        },
        cardHolderTextFieldValue = cardHolderTextFieldValue,
        onCardHolderChanged = {
            val filtered = it.text
                .uppercase()
                .filter { it in 'A'..'Z' || it == ' ' }

            cardHolderTextFieldValue = it.copy(text = filtered)
            cardHolderError = ValidationErrorType.Valid
        },
        phoneNumberTextFieldValue = phoneTextFieldValue,
        onPhoneNumberChanged = {
            phoneTextFieldValue = it
            phoneError = ValidationErrorType.Valid
        },
        emailTextFieldValue = emailTextFieldValue,
        onEmailChanged = {
            emailTextFieldValue = it
            emailError = ValidationErrorType.Valid
        },
        cardNumberError = cardNumberError,
        cardExpError = cardExpError,
        cvvNumberError = cvvNumberError,
        emailError = emailError,
        cardHolderError = cardHolderError,
        phoneError = phoneError,
        isSavedCardChanged = { token, cardNumber ->
            if (token.isNotEmpty() && cardNumber.isNotEmpty()) {
                isSavedCardClicked = true
                isSavedCardToken = token
                savedCardNumber = TextFieldValue(cardNumber.filter { it.isDigit() || it == 'X' }
                    .replace('X', '*'))
            } else {
                isSavedCardClicked = false
                isSavedCardToken = ""
                savedCardNumber = TextFieldValue("")
            }

            cardNumberError = ValidationErrorType.Valid
        },
        isSavedCardNumber = savedCardNumber,
        onSaveCardChanged = { isSaveCard = it },
        isSaveCard = isSaveCard,
        isSavedCardUse = isSavedCardClicked,
        isEnabled = true,
        isProgress = state is MainSuccessState.Loading,
        isGooglePayButtonClickEnabled = isGooglePayButtonEnabled(),
        onGooglePayClick = {
            requestGooglePay(transactionInfoMainRs, googlePayFacade)
        },
        onPayClick = {
            if (canSend())
                onPaySendAction()
            else validateFields()
        },
        onCancelClick = {
            fragment.activity?.setResult(Activity.RESULT_CANCELED)
            fragment.activity?.finish()
        },
        onGooglePayTab = {
            isGooglePayNow = it
        },
    ) {
        viewModel.setAction(
            MainSuccessAction.DeleteCard(
                projectId = transactionInfoMainRs.projectId,
                cardId = it,
            )
        )
    }

    LaunchedEffect(Unit) {
        this.launch {
            isPhoneCanUserGooglePay =
                googlePayFacade.possiblyShowGooglePayButton(fragment.requireActivity())
        }
    }
}

private fun String.isLatinLetter(): Boolean {
    return this.all { it in 'A'..'Z' || it in 'a'..'z' || it == ' ' }
}

private fun String.validateEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

private fun String.validatePhone(): Boolean {
    return this.length == 12
}

private fun String.validateLunaCardNumber(): Boolean {
    var sum = 0
    var alternate = false
    for (i in this.length - 1 downTo 0) {
        var n = this[i].digitToInt() // Преобразование символа в число
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

fun jsonToMap(jsonObject: JSONObject): Map<String, Any> {
    val map = mutableMapOf<String, Any>()

    val keys = jsonObject.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        val value = jsonObject.get(key)
        when (value) {
            is JSONObject -> map[key] = jsonToMap(value)
            else -> map[key] = value
        }
    }

    return map
}