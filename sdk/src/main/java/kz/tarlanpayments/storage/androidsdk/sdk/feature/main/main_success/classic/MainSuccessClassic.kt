package kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.classic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.FragmentManager
import coil.compose.AsyncImage
import kz.tarlanpayments.storage.androidsdk.R
import kz.tarlanpayments.storage.androidsdk.noui.TarlanInstance
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionDescriptionModel
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.FormController
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.visual_transformation.generalMaskPhoneTransformation
import kz.tarlanpayments.storage.androidsdk.sdk.provideCurrentLocale
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitBorderButton
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitCompanyLogo
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitGradientButton
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitSegmentControl
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitSegmentItem
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitTextField
import kz.tarlanpayments.storage.androidsdk.sdk.ui.LanguageDropDown
import kz.tarlanpayments.storage.androidsdk.sdk.ui.utils.parseColor
import kz.tarlanpayments.storage.androidsdk.sdk.utils.Localization
import kz.tarlanpayments.storage.androidsdk.sdk.utils.ValidationErrorType
import kz.tarlanpayments.storage.androidsdk.sdk.utils.emailValidationErrorText
import kz.tarlanpayments.storage.androidsdk.sdk.utils.phoneValidationErrorText
import kz.tarlanpayments.storage.androidsdk.sdk.utils.toFormGradient
import kz.tarlanpayments.storage.androidsdk.sdk.utils.toInputGradient
import kz.tarlanpayments.storage.androidsdk.sdk.utils.toTextGradient

@Composable
internal fun MainSuccessClassic(
    fragmentManager: FragmentManager,
    focusRequester: Map<String, FocusRequester>,
    transactionId: Long,
    transactionDescription: TarlanTransactionDescriptionModel,
    mainSuccessController: FormController,
    cardNumberTextFieldValue: TextFieldValue,
    onCardNumberChanged: (TextFieldValue) -> Unit,
    monthNumberTextFieldValue: TextFieldValue,
    onMonthNumberChanged: (TextFieldValue) -> Unit,
    yearNumberTextFieldValue: TextFieldValue,
    onYearNumberChanged: (TextFieldValue) -> Unit,
    cvvNumberTextFieldValue: TextFieldValue,
    onCvvNumberChanged: (TextFieldValue) -> Unit,
    cardHolderTextFieldValue: TextFieldValue,
    onCardHolderChanged: (TextFieldValue) -> Unit,
    phoneNumberTextFieldValue: TextFieldValue,
    onPhoneNumberChanged: (TextFieldValue) -> Unit,
    emailTextFieldValue: TextFieldValue,
    onEmailChanged: (TextFieldValue) -> Unit,

    cardNumberError: ValidationErrorType,
    cardExpError: ValidationErrorType,
    cvvNumberError: ValidationErrorType,
    emailError: ValidationErrorType,
    cardHolderError: ValidationErrorType,
    phoneError: ValidationErrorType,

    onSavedCardChanged: (String, String) -> Unit,
    onNewCardClicked: () -> Unit,

    isSavedCardNumber: TextFieldValue,
    onSaveCardChanged: (Boolean) -> Unit,
    isSaveCard: Boolean,
    savedCards: List<TarlanTransactionDescriptionModel.SavedCard>,

    isSavedCardUse: Boolean,
    isEnabled: Boolean,
    isProgress: Boolean,

    onGooglePayTab: (Boolean) -> Unit,

    onGooglePayClick: () -> Unit,
    onPayClick: () -> Unit,

    onCancelClick: () -> Unit,
    onCardRemoved: (String) -> Unit,

    ) {
    val context = LocalContext.current
    var currentLanguage by remember { mutableStateOf(context.provideCurrentLocale()) }
    var currentPageIndex by remember { mutableIntStateOf(0) }

    val secondaryColor = Color(0xFFB0B0B0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        MainSuccessClassicHeader(
            transactionId = transactionId,
            transactionDescription = transactionDescription,
            secondaryColor = secondaryColor,
            currentLanguage = currentLanguage,
            onLanguageChanged = { currentLanguage = it }
        )

        Spacer(modifier = Modifier.height(20.dp))


        if (mainSuccessController.isGooglePlayVisible) {
            KitSegmentControl(
                selectedBrush = transactionDescription.toFormGradient(),
                accentColor = parseColor(color = transactionDescription.mainFormColor),
                unSelectedTint = secondaryColor,
                selectedBackground = Color.White,
                unSelectedBackground = Color(0xFFECECEC),
                currentPageIndex = currentPageIndex,
                onPageChanged = {
                    currentPageIndex = it
                    onGooglePayTab(currentPageIndex == 1)
                },
                selectedColor = parseColor(color = transactionDescription.mainFormColor),
                itemsTitle = listOf(
                    KitSegmentItem(
                        title = Localization.getString(
                            Localization.KeyCard,
                            locale = currentLanguage
                        ),
                        icon = painterResource(id = R.drawable.ic_card)
                    ),
                    KitSegmentItem(
                        title = Localization.getString(
                            Localization.KeyGoogle,
                            locale = currentLanguage
                        ),
                        icon = painterResource(id = R.drawable.ic_google)
                    )
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (currentPageIndex == 0) {
            MainSuccessClassicCard(
                controller = mainSuccessController,
                focusRequester = focusRequester,
                fragmentManager = fragmentManager,
                transactionDescription = transactionDescription,
                savedCard = savedCards,
                cardNumberError = cardNumberError,
                cardExpireDateError = cardExpError,
                cvvNumberError = cvvNumberError,
                cardHolderError = cardHolderError,
                cardNumberTextFieldValue = cardNumberTextFieldValue,
                onCardNumberChanged = onCardNumberChanged,
                monthNumberTextFieldValue = monthNumberTextFieldValue,
                onMonthNumberChanged = onMonthNumberChanged,
                yearNumberTextFieldValue = yearNumberTextFieldValue,
                onYearNumberChanged = onYearNumberChanged,
                cvvNumberTextFieldValue = cvvNumberTextFieldValue,
                onCvvNumberChanged = onCvvNumberChanged,
                cardHolderTextFieldValue = cardHolderTextFieldValue,
                onCardHolderChanged = onCardHolderChanged,
                isSaveCard = isSaveCard,
                onSaveCardChanged = onSaveCardChanged,
                secondaryColor = secondaryColor,
                currentLocale = currentLanguage,
                isSavedCardUse = isSavedCardUse,
                onSavedCardChanged = onSavedCardChanged,
                onNewCardClicked = onNewCardClicked,
                isSavedCardNumber = isSavedCardNumber,
                onCardRemoved = onCardRemoved
            ).invoke(this)
        } else {
            MainSuccessClassicGooglePay(
                onGooglePayClick = onGooglePayClick
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if ((mainSuccessController.isShowPhone || mainSuccessController.isShowEmail) && currentPageIndex == 0) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(transactionDescription.toFormGradient(), RoundedCornerShape(10.dp))
                    .padding(4.dp)
            ) {

                val phoneRequiredHint = if (mainSuccessController.isPhoneRequired) "* " else ""

                if (mainSuccessController.isShowPhone)
                    KitTextField(
                        textFieldModifier = Modifier.focusRequester(focusRequester["phone"]!!),
                        value = phoneNumberTextFieldValue,
                        textColor = parseColor(color = transactionDescription.mainTextInputColor),
                        labelText = "${phoneRequiredHint}${
                            Localization.getString(
                                Localization.KeyPhone,
                                locale = currentLanguage
                            )
                        }",
                        backgroundBrush = transactionDescription.toInputGradient(),
                        onValueChange = {
                            if (it.text.length <= 10 && it.text.isDigitsOnly()) {
                                onPhoneNumberChanged(it)
                            }
                        },
                        labelColor = parseColor(color = transactionDescription.inputLabelColor),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        showError = phoneError != ValidationErrorType.Valid,
                        errorText = phoneError.phoneValidationErrorText(currentLanguage),
                        visualTransformation = { generalMaskPhoneTransformation(text = it) }
                    )

                val emailRequiredHint = if (mainSuccessController.isEmailRequired) "* " else ""
                if (mainSuccessController.isShowEmail)
                    KitTextField(
                        textFieldModifier = Modifier.focusRequester(focusRequester["email"]!!),
                        value = emailTextFieldValue,
                        textColor = parseColor(color = transactionDescription.mainTextInputColor),
                        labelText = "$emailRequiredHint${
                            Localization.getString(
                                Localization.KeyEmail,
                                locale = currentLanguage
                            )
                        }",
                        backgroundBrush = transactionDescription.toInputGradient(),
                        onValueChange = onEmailChanged,
                        labelColor = parseColor(color = transactionDescription.inputLabelColor),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        showError = emailError != ValidationErrorType.Valid,
                        errorText = emailError.emailValidationErrorText(currentLanguage)
                    )

            }
        }

        if (currentPageIndex == 0) {
            Spacer(modifier = Modifier.height(16.dp))

            KitGradientButton(
                title = transactionDescription.getButtonTitle(currentLanguage),
                brush = transactionDescription.toFormGradient(),
                textColor = parseColor(color = transactionDescription.inputLabelColor),
                accentColor = parseColor(color = transactionDescription.inputLabelColor),
                isEnabled = isEnabled,
                onClick = onPayClick,
                isProgress = isProgress
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        KitBorderButton(
            title = Localization.getString(
                Localization.KeyCancel,
                locale = currentLanguage
            ),
            brush = transactionDescription.toFormGradient(),
            accentColor = parseColor(color = transactionDescription.mainFormColor),
            onClick = onCancelClick
        )

        KitCompanyLogo().invoke(this)
    }
}

private fun TarlanTransactionDescriptionModel.getButtonTitle(currentLanguage: String): String {
    when (this.type) {
        TarlanTransactionDescriptionModel.TransactionType.In -> {
            return "${
                Localization.getString(
                    Localization.KeyPay,
                    currentLanguage
                )
            } ${this.totalAmount}₸"
        }

        TarlanTransactionDescriptionModel.TransactionType.Out -> {
            return "${
                Localization.getString(
                    Localization.KeyRefund,
                    currentLanguage
                )
            } ${this.totalAmount}₸"
        }

        TarlanTransactionDescriptionModel.TransactionType.CardLink -> {
            return Localization.getString(
                Localization.KeyCardLink,
                currentLanguage
            )
        }

        else -> return ""
    }
}

@Composable
private fun MainSuccessClassicHeader(
    transactionId: Long,
    transactionDescription: TarlanTransactionDescriptionModel,
    secondaryColor: Color,
    currentLanguage: String,
    onLanguageChanged: (String) -> Unit
) {

    Row(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            modifier = Modifier.size(64.dp),
            model = TarlanInstance.provideImage(transactionDescription.logoFilePath),
            contentDescription = "",
            placeholder = painterResource(id = R.drawable.ic_error_placaholder),
            error = painterResource(id = R.drawable.ic_error_placaholder)
        )
        Spacer(
            modifier = Modifier.width(16.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = transactionDescription.storeName,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = secondaryColor,
                    fontWeight = FontWeight.W400
                )
            )
            Text(
                text = "${transactionDescription.orderAmount}₸",
                style = TextStyle(
                    brush = transactionDescription.toTextGradient(),
                    fontWeight = FontWeight.W700,
                    fontSize = 32.sp,
                )
            )
            Row {
                Text(
                    text = "№$transactionId",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = secondaryColor,
                        fontWeight = FontWeight.W400
                    )
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "${
                        Localization.getString(
                            Localization.KeyCommssion,
                            currentLanguage
                        )
                    } ${transactionDescription.upperCommissionAmount}KZT",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = secondaryColor,
                        fontWeight = FontWeight.W400
                    )
                )
            }
        }

        LanguageDropDown(
            onLanguageChanged = { onLanguageChanged(it) },
            currentLanguage = currentLanguage,
            accentColor = parseColor(color = transactionDescription.mainFormColor)
        )
    }
}

