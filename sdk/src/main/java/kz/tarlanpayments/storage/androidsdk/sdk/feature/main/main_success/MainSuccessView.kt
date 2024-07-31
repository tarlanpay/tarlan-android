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
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionDescriptionModel
import kz.tarlanpayments.storage.androidsdk.noui.ui.Tarlan3DSFragment
import kz.tarlanpayments.storage.androidsdk.noui.ui.Tarlan3DSV2Fragment
import kz.tarlanpayments.storage.androidsdk.sdk.GooglePayFacade
import kz.tarlanpayments.storage.androidsdk.sdk.TarlanActivity
import kz.tarlanpayments.storage.androidsdk.sdk.TarlanScreens
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.classic.MainSuccessClassic
import kz.tarlanpayments.storage.androidsdk.sdk.utils.ValidationErrorType
import kz.tarlanpayments.storage.androidsdk.sdk.utils.ValidationUtils
import org.json.JSONObject

@Composable
internal fun MainSuccessView(
    transactionId: Long,
    transactionHash: String,
    transactionDescription: TarlanTransactionDescriptionModel,
    fragment: Fragment,
    googlePayFacade: GooglePayFacade
) {

    var isGooglePayClickEnabled by remember { mutableStateOf(true) }
    var isPhoneCanUserGooglePay by remember { mutableStateOf(false) }
    var isGooglePayNow by remember { mutableStateOf(false) }
    var savedCards by remember { mutableStateOf(transactionDescription.cards) }


    val viewModel = viewModel<MainSuccessViewModel>(
        factory = MainSuccessViewModel.MainSuccessViewModelFactory(
            transactionDescription = transactionDescription,
            transactionId = transactionId,
            transactionHash = transactionHash
        )
    )
    val controller = FormController(
        transactionDescription = transactionDescription,
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
    val firstCard by remember { mutableStateOf(savedCards.firstOrNull()?.maskedPan) }

    // region text fields value
    var cardNumberTextFieldValue by remember { mutableStateOf(TextFieldValue((if (controller.hasDefaultCard && savedCards.isNotEmpty()) firstCard!! else ""))) }
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

    var isSavedCardClicked by remember { mutableStateOf(controller.hasDefaultCard && savedCards.isNotEmpty()) }
    var savedCardNumber by remember {
        mutableStateOf(TextFieldValue((if (controller.hasDefaultCard && savedCards.isNotEmpty()) firstCard!! else "")))
    }
    var savedCardToken by remember { mutableStateOf((if (controller.hasDefaultCard && savedCards.isNotEmpty()) savedCards.first().cardToken else "")) }

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
                    fragment.setFragmentResultListener(Tarlan3DSFragment.TARLAN_3DS_REQUEST_KEY) { _, bundle ->
                        TarlanActivity.router.newRootScreen(
                            TarlanScreens.Status(
                                it.transactionId,
                                it.transactionHash
                            )
                        )
                    }
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
                    fragment.setFragmentResultListener(Tarlan3DSV2Fragment.TARLAN_3DS_REQUEST_KEY) { _, bundle ->
                        TarlanActivity.router.newRootScreen(
                            TarlanScreens.Status(
                                it.transactionId,
                                it.transactionHash
                            )
                        )
                    }
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

    fun isSavedCardSelected(): Boolean {
        return isSavedCardClicked && savedCardToken.isNotEmpty()
    }


    fun canSendFromSelectedCard(): Boolean {
        return isSavedCardSelected() && ValidationUtils.isAdditionalFieldsValid(
            formController = controller,
            email = emailTextFieldValue.text,
            phone = phoneTextFieldValue.text
        )
    }

    fun canSendFromNewCard(): Boolean {
        return ValidationUtils.isCardFieldsValid(
            formController = controller,
            cardNumber = cardNumberTextFieldValue.text,
            month = monthNumberTextFieldValue.text,
            year = yearNumberTextFieldValue.text,
            cvv = cvvTextFieldValue.text,
            cardHolder = cardHolderTextFieldValue.text
        ) && ValidationUtils.isAdditionalFieldsValid(
            formController = controller,
            email = emailTextFieldValue.text,
            phone = phoneTextFieldValue.text
        )
    }

    fun canSend(): Boolean {
        return isGooglePayNow || canSendFromNewCard() || canSendFromSelectedCard()
    }

    fun validateFields() {
        if (controller.isPanVisible && !isSavedCardSelected()) {
            cardNumberError = ValidationUtils.validateCardNumber(cardNumberTextFieldValue.text)
        }
        if (controller.isCvvVisible && !isSavedCardSelected()) {
            cvvNumberError = ValidationUtils.validateCvv(cvvTextFieldValue.text)
        }
        if (controller.isExpDate && !isSavedCardSelected()) {
            cardExpError = ValidationUtils.validateExpDate(
                monthNumberTextFieldValue.text,
                yearNumberTextFieldValue.text
            )
        }
        if (controller.isEmailRequired || (emailTextFieldValue.text.isNotEmpty() && controller.isShowEmail)) {
            emailError = ValidationUtils.validateEmail(emailTextFieldValue.text)
        }
        if (controller.isPhoneRequired || (phoneTextFieldValue.text.isNotEmpty() && controller.isShowPhone)) {
            phoneError = ValidationUtils.validatePhone(phoneTextFieldValue.text)
        }
        if (controller.isCardHolderVisible && !isSavedCardSelected()) {
            cardHolderError = ValidationUtils.validateCardHolder(cardHolderTextFieldValue.text)
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
        transactionDescription: TarlanTransactionDescriptionModel,
        googlePayFacade: GooglePayFacade,
    ) {
        isGooglePayClickEnabled = false
        val price = transactionDescription.totalAmount
        val paymentDataRequestJson = googlePayFacade.getPaymentDataRequest(price) ?: return
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        val task = googlePayFacade.paymentsClient!!.loadPaymentData(request)
        task.addOnCompleteListener { completedTask ->
            if (completedTask.isSuccessful) {
                completedTask.result?.let(::handlePaymentSuccess)
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
                    phone = "+7${phoneTextFieldValue.text}",
                    cardHolder = cardHolderTextFieldValue.text,
                    saveCard = isSaveCard
                )
            )
        } else {
            viewModel.setAction(
                MainSuccessAction.FromSavedCard(
                    encryptedId = savedCardToken,
                    email = emailTextFieldValue.text,
                    phone = "+7${phoneTextFieldValue.text}"
                )
            )
        }
    }

    MainSuccessClassic(
        fragmentManager = fragment.childFragmentManager,
        focusRequester = focusRequesters,
        transactionId = transactionId,
        transactionDescription = transactionDescription,
        mainSuccessController = controller,
        cardNumberTextFieldValue = cardNumberTextFieldValue,
        onCardNumberChanged = {
            val filter = it.text.filter { it.isDigit() }

            if (filter.length < 17) {
                cardNumberTextFieldValue = it.copy(text = filter)
            }


            if (cardNumberTextFieldValue.text.length == 16) {
                if (controller.isExpDate) {
                    focusRequesters["monthNumber"]?.requestFocus()
                } else if (controller.isCardHolderVisible) {
                    focusRequesters["cardHolder"]?.requestFocus()
                } else if (controller.isShowEmail) {
                    focusRequesters["email"]?.requestFocus()
                } else if (controller.isShowPhone) {
                    focusRequesters["phone"]?.requestFocus()
                } else {
                    focusManager.clearFocus()
                }
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
                if (controller.isExpDate) {
                    focusRequesters["yearNumber"]?.requestFocus()
                }
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
                if (controller.isCvvVisible) {
                    focusRequesters["cvvNumber"]?.requestFocus()
                }
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
        onSavedCardChanged = { token, cardNumber ->
            isSavedCardClicked = true
            savedCardToken = token
            savedCardNumber = TextFieldValue(cardNumber.filter { it.isDigit() || it == 'X' }
                .replace('X', '*'))
            cardNumberError = ValidationErrorType.Valid
        },
        onNewCardClicked = {
            isSavedCardClicked = false
            savedCardToken = ""
            savedCardNumber = TextFieldValue("")
            cardNumberError = ValidationErrorType.Valid
            cardNumberTextFieldValue = TextFieldValue("")
        },
        isSavedCardNumber = savedCardNumber,
        onSaveCardChanged = { isSaveCard = it },
        isSaveCard = isSaveCard,
        isSavedCardUse = isSavedCardClicked,
        isEnabled = true,
        isProgress = state is MainSuccessState.Loading,
        onGooglePayClick = {
            requestGooglePay(transactionDescription, googlePayFacade)
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
        savedCards = savedCards,
        onCardRemoved = { cardToken ->

            savedCards = savedCards.toMutableList().apply {
                this.removeIf { it.cardToken == cardToken }
            }

            if (cardToken == savedCardToken) {
                isSavedCardClicked = false
                savedCardToken = ""
                savedCardNumber = TextFieldValue("")
                cardNumberTextFieldValue = TextFieldValue("")
            }

            cardNumberError = ValidationErrorType.Valid

            viewModel.setAction(
                MainSuccessAction.DeleteCard(
                    projectId = transactionDescription.projectID,
                    cardId = cardToken,
                )
            )
        }
    )

    LaunchedEffect(Unit) {
        this.launch {
            isPhoneCanUserGooglePay =
                googlePayFacade.possiblyShowGooglePayButton(fragment.requireActivity())
        }
    }
}

fun jsonToMap(jsonObject: JSONObject): Map<String, Any> {
    val map = mutableMapOf<String, Any>()

    val keys = jsonObject.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        when (val value = jsonObject.get(key)) {
            is JSONObject -> map[key] = jsonToMap(value)
            else -> map[key] = value
        }
    }

    return map
}