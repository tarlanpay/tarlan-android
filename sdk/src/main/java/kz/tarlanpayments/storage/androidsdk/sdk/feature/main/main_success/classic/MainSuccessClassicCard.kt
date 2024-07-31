package kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.classic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentManager
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionDescriptionModel
import kz.tarlanpayments.storage.androidsdk.sdk.feature.CardPickerBottomSheet
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.FormController
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.visual_transformation.CardNumberVisualTransformation
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitTextField
import kz.tarlanpayments.storage.androidsdk.sdk.ui.utils.parseColor
import kz.tarlanpayments.storage.androidsdk.sdk.utils.Localization
import kz.tarlanpayments.storage.androidsdk.sdk.utils.ValidationErrorType
import kz.tarlanpayments.storage.androidsdk.sdk.utils.cardHolderValidationErrorText
import kz.tarlanpayments.storage.androidsdk.sdk.utils.cardValidationErrorText
import kz.tarlanpayments.storage.androidsdk.sdk.utils.cvvValidationErrorText
import kz.tarlanpayments.storage.androidsdk.sdk.utils.monthValidationErrorText
import kz.tarlanpayments.storage.androidsdk.sdk.utils.toFormGradient
import kz.tarlanpayments.storage.androidsdk.sdk.utils.toInputGradient

@Composable
internal fun MainSuccessClassicCard(
    controller: FormController,
    focusRequester: Map<String, FocusRequester>,
    fragmentManager: FragmentManager,

    transactionDescription: TarlanTransactionDescriptionModel,
    savedCard: List<TarlanTransactionDescriptionModel.SavedCard>,

    cardNumberError: ValidationErrorType,
    cardExpireDateError: ValidationErrorType,
    cvvNumberError: ValidationErrorType,
    cardHolderError: ValidationErrorType,

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
    isSaveCard: Boolean,

    onSaveCardChanged: (Boolean) -> Unit,
    onNewCardClicked: () -> Unit,
    secondaryColor: Color,

    currentLocale: String,
    isSavedCardUse: Boolean,
    onSavedCardChanged: (String, String) -> Unit,
    isSavedCardNumber: TextFieldValue,
    onCardRemoved: (String) -> Unit,
): @Composable ColumnScope.() -> Unit = {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(transactionDescription.toFormGradient(), RoundedCornerShape(10.dp))
            .padding(4.dp)
    ) {
        if (controller.isPanVisible) {
            Column {
                KitTextField(
                    textFieldModifier = Modifier.focusRequester(focusRequester["cardNumber"]!!),
                    errorText = cardNumberError.cardValidationErrorText(currentLocale),
                    showError = cardNumberError != ValidationErrorType.Valid,
                    value = if (isSavedCardUse) isSavedCardNumber else cardNumberTextFieldValue,
                    textColor = parseColor(color = transactionDescription.mainTextInputColor),
                    labelText = Localization.getString(
                        Localization.KeyCardNumber,
                        locale = currentLocale
                    ),
                    readOnly = isSavedCardUse,
                    backgroundBrush = transactionDescription.toInputGradient(),
                    onValueChange = onCardNumberChanged,
                    labelColor = parseColor(color = transactionDescription.inputLabelColor),
                    trailingIcon = {
                        if (controller.isSavedCardVisible && savedCard.isNotEmpty())
                            Icon(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        val fragment = CardPickerBottomSheet.newInstance(
                                            launcher = CardPickerBottomSheet.Launcher(
                                                transactionColorRs = transactionDescription,
                                                savedCard = savedCard
                                            )
                                        )

                                        fragment.onCardRemoved = { cardToken, _ ->
                                            onCardRemoved(cardToken)
                                        }

                                        fragment.onSelectCard = { cardToken, cardNumber ->
                                            onSavedCardChanged(cardToken, cardNumber)
                                        }

                                        fragment.onNewCardClicked = {
                                            onNewCardClicked()
                                        }

                                        fragment.show(fragmentManager, "CardPickerBottomSheet")
                                    },
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "",
                                tint = parseColor(color = transactionDescription.mainTextInputColor)
                            )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = CardNumberVisualTransformation()
                )
            }
        }

        if (controller.isExpDate && !isSavedCardUse) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = Localization.getString(
                            Localization.KeyCardExpiryDate,
                            locale = currentLocale
                        ),
                        style = TextStyle(
                            color = parseColor(color = transactionDescription.inputLabelColor),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.W400
                        ),
                        color = parseColor(color = transactionDescription.inputLabelColor)
                    )
                    Column {
                        Row {
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(modifier = Modifier.width(70.dp)) {
                                KitTextField(
                                    modifier = Modifier
                                        .width(70.dp),
                                    textFieldModifier = Modifier
                                        .focusRequester(focusRequester["monthNumber"]!!),
                                    horizontalPadding = 0.dp,
                                    verticalPadding = 0.dp,
                                    value = monthNumberTextFieldValue,
                                    textColor = parseColor(color = transactionDescription.mainTextInputColor),
                                    backgroundBrush = transactionDescription.toInputGradient(),
                                    onValueChange = onMonthNumberChanged,
                                    labelColor = parseColor(color = transactionDescription.inputLabelColor),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    showError = cardExpireDateError != ValidationErrorType.Valid
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.width(70.dp)) {
                                KitTextField(
                                    verticalPadding = 0.dp,
                                    horizontalPadding = 0.dp,
                                    modifier = Modifier
                                        .width(70.dp),
                                    textFieldModifier = Modifier
                                        .focusRequester(focusRequester["yearNumber"]!!),
                                    value = yearNumberTextFieldValue,
                                    textColor = parseColor(color = transactionDescription.mainTextInputColor),
                                    backgroundBrush = transactionDescription.toInputGradient(),
                                    onValueChange = onYearNumberChanged,
                                    labelColor = parseColor(color = transactionDescription.inputLabelColor),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    showError = cardExpireDateError != ValidationErrorType.Valid
                                )
                            }
                        }
                        if (cardExpireDateError != ValidationErrorType.Valid)
                            Row {
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                    text = cardExpireDateError.monthValidationErrorText(
                                        currentLocale
                                    ),
                                    style = TextStyle(
                                        color = Color.Red,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.W400
                                    ),
                                    color = Color.Red
                                )
                            }
                    }
                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Box(modifier = Modifier.width(96.dp)) {
                    KitTextField(
                        modifier = Modifier
                            .width(96.dp),
                        textFieldModifier = Modifier
                            .focusRequester(focusRequester["cvvNumber"]!!),
                        value = cvvNumberTextFieldValue,
                        textColor = parseColor(color = transactionDescription.mainTextInputColor),
                        labelText = Localization.getString(
                            Localization.KeyCvv,
                            locale = currentLocale
                        ),
                        backgroundBrush = transactionDescription.toInputGradient(),
                        onValueChange = onCvvNumberChanged,
                        labelColor = parseColor(color = transactionDescription.inputLabelColor),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        showError = cvvNumberError != ValidationErrorType.Valid,
                        errorText = cvvNumberError.cvvValidationErrorText(currentLocale)
                    )
                }
            }
        }

        if (controller.isCardHolderVisible && !isSavedCardUse) {
            KitTextField(
                textFieldModifier = Modifier.focusRequester(focusRequester["cardHolder"]!!),
                value = cardHolderTextFieldValue,
                textColor = parseColor(color = transactionDescription.mainTextInputColor),
                labelText = Localization.getString(
                    Localization.KeyCardHolder,
                    locale = currentLocale
                ),
                backgroundBrush = transactionDescription.toInputGradient(),
                onValueChange = onCardHolderChanged,
                labelColor = parseColor(color = transactionDescription.inputLabelColor),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                textAlign = TextAlign.Center,
                showError = cardHolderError != ValidationErrorType.Valid,
                errorText = cardNumberError.cardHolderValidationErrorText(currentLocale),
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (controller.isSaveCardVisible && !isSavedCardUse) {
        Row(
            Modifier
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                text = Localization.getString(
                    Localization.KeySaveCard,
                    locale = currentLocale
                ),
                style = TextStyle(
                    fontSize = 14.sp,
                    color = secondaryColor,
                    fontWeight = FontWeight.W400
                ),
                color = secondaryColor
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .background(Color.Red)
                    .weight(1f)
            )
            Checkbox(
                checked = isSaveCard,
                onCheckedChange = { onSaveCardChanged(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = parseColor(color = transactionDescription.mainFormColor),
                    uncheckedColor = secondaryColor,
                    checkmarkColor = Color.White
                ),
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

