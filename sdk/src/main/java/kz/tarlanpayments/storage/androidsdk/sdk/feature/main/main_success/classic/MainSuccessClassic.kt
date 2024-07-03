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
import androidx.fragment.app.FragmentManager
import coil.compose.AsyncImage
import kz.tarlanpayments.storage.androidsdk.R
import kz.tarlanpayments.storage.androidsdk.sdk.DepsHolder
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionColorRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionInfoMainRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionInfoPayFormRs
import kz.tarlanpayments.storage.androidsdk.sdk.feature.Localization
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.MainSuccessController
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.ValidationErrorType
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.emailValidationErrorText
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.phoneValidationErrorText
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.toFormGradient
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.toInputGradient
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.toTextGradient
import kz.tarlanpayments.storage.androidsdk.sdk.provideCurrentLocale
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitBorderButton
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitCompanyLogo
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitGradientButton
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitSegmentControl
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitSegmentItem
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitTextField
import kz.tarlanpayments.storage.androidsdk.sdk.ui.LanguageDropDown
import kz.tarlanpayments.storage.androidsdk.sdk.ui.utils.parseColor

@Composable
internal fun MainSuccessClassic(
    fragmentManager: FragmentManager,
    focusRequester: Map<String, FocusRequester>,
    transactionId: Long,
    transactionInfoMainRs: TransactionInfoMainRs,
    transactionInfoPayFormRs: TransactionInfoPayFormRs,
    transactionColorRs: TransactionColorRs,
    mainSuccessController: MainSuccessController,
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

    isSavedCardChanged: (String, String) -> Unit,

    isSavedCardNumber: TextFieldValue,
    onSaveCardChanged: (Boolean) -> Unit,
    isSaveCard: Boolean,

    isSavedCardUse: Boolean,
    isEnabled: Boolean,
    isProgress: Boolean,
    isGooglePayButtonClickEnabled: Boolean,

    onGooglePayTab: (Boolean) -> Unit,

    onGooglePayClick: () -> Unit,
    onPayClick: () -> Unit,

    onCancelClick: () -> Unit,
    onCardRemoved: (String) -> Unit,
) {
    val context = LocalContext.current
    var currentLanguage by remember { mutableStateOf(context.provideCurrentLocale()) }
    var currentPageIndex by remember { mutableIntStateOf(0) }
    val savedCards by remember { mutableStateOf(transactionInfoMainRs.cards) }

    val secondaryColor = Color(0xFFB0B0B0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        MainSuccessClassicHeader(
            transactionId = transactionId,
            transactionInfoMainRs = transactionInfoMainRs,
            transactionInfoPayFormRs = transactionInfoPayFormRs,
            transactionColorRs = transactionColorRs,
            secondaryColor = secondaryColor,
            currentLanguage = currentLanguage,
            onLanguageChanged = { currentLanguage = it }
        )

        Spacer(modifier = Modifier.height(20.dp))


        if (mainSuccessController.isGooglePlayVisible) {
            KitSegmentControl(
                selectedBrush = transactionColorRs.toFormGradient(),
                accentColor = parseColor(color = transactionColorRs.mainFormColor),
                unSelectedTint = secondaryColor,
                selectedBackground = Color.White,
                unSelectedBackground = Color(0xFFECECEC),
                currentPageIndex = currentPageIndex,
                onPageChanged = {
                    currentPageIndex = it
                    onGooglePayTab(currentPageIndex == 1)
                },
                selectedColor = parseColor(color = transactionColorRs.mainFormColor),
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
                transactionColorRs = transactionColorRs,
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
                isSavedCardChanged = isSavedCardChanged,
                isSavedCardNumber = isSavedCardNumber,
                onCardRemoved = onCardRemoved
            ).invoke(this)
        } else {
            MainSuccessClassicGooglePay(
                onGooglePayClick = onGooglePayClick,
                isGooglePayClickEnabled = isGooglePayButtonClickEnabled,
                brush = transactionColorRs.toFormGradient()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if ((mainSuccessController.isShowPhone || mainSuccessController.isShowEmail) && currentPageIndex == 0) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(transactionColorRs.toFormGradient(), RoundedCornerShape(10.dp))
                    .padding(4.dp)
            ) {

                val phoneRequiredHint = if (mainSuccessController.isPhoneRequired) "* " else ""

                if (mainSuccessController.isShowPhone)
                    KitTextField(
                        textFieldModifier = Modifier.focusRequester(focusRequester["phone"]!!),
                        value = phoneNumberTextFieldValue,
                        textColor = parseColor(color = transactionColorRs.mainTextInputColor),
                        labelText = "${phoneRequiredHint}${
                            Localization.getString(
                                Localization.KeyPhone,
                                locale = currentLanguage
                            )
                        }",
                        backgroundBrush = transactionColorRs.toInputGradient(),
                        onValueChange = onPhoneNumberChanged,
                        labelColor = parseColor(color = transactionColorRs.inputLabelColor),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        showError = phoneError != ValidationErrorType.Valid,
                        errorText = phoneError.phoneValidationErrorText(currentLanguage)
                    )

                val emailRequiredHint = if (mainSuccessController.isEmailRequired) "* " else ""
                if (mainSuccessController.isShowEmail)
                    KitTextField(
                        textFieldModifier = Modifier.focusRequester(focusRequester["email"]!!),
                        value = emailTextFieldValue,
                        textColor = parseColor(color = transactionColorRs.mainTextInputColor),
                        labelText = "$emailRequiredHint${
                            Localization.getString(
                                Localization.KeyEmail,
                                locale = currentLanguage
                            )
                        }",
                        backgroundBrush = transactionColorRs.toInputGradient(),
                        onValueChange = onEmailChanged,
                        labelColor = parseColor(color = transactionColorRs.inputLabelColor),
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
                title = "${
                    Localization.getString(
                        Localization.KeyPay,
                        locale = currentLanguage
                    )
                } ${transactionInfoMainRs.totalAmount}₸",
                brush = transactionColorRs.toFormGradient(),
                textColor = parseColor(color = transactionColorRs.inputLabelColor),
                accentColor = parseColor(color = transactionColorRs.inputLabelColor),
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
            brush = transactionColorRs.toFormGradient(),
            accentColor = parseColor(color = transactionColorRs.mainFormColor),
            onClick = onCancelClick
        )

        KitCompanyLogo().invoke(this)
    }
}

@Composable
private fun MainSuccessClassicHeader(
    transactionId: Long,
    transactionInfoMainRs: TransactionInfoMainRs,
    transactionInfoPayFormRs: TransactionInfoPayFormRs,
    transactionColorRs: TransactionColorRs,
    secondaryColor: Color,
    currentLanguage: String,
    onLanguageChanged: (String) -> Unit
) {

    Row(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            modifier = Modifier.size(64.dp),
            model = DepsHolder.provideImage(transactionInfoPayFormRs.logoFilePath),
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
                text = transactionInfoPayFormRs.storeName,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = secondaryColor,
                    fontWeight = FontWeight.W400
                )
            )
            Text(
                text = "${transactionInfoMainRs.orderAmount}₸",
                style = TextStyle(
                    brush = transactionColorRs.toTextGradient(),
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
                    } ${transactionInfoMainRs.upperCommissionAmount}KZT",
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
            accentColor = parseColor(color = transactionColorRs.mainFormColor)
        )
    }
}

