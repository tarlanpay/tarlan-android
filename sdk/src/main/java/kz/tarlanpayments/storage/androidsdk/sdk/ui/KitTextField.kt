package kz.tarlanpayments.storage.androidsdk.sdk.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kz.tarlanpayments.storage.androidsdk.sdk.ui.Theme.kitColor
import kz.tarlanpayments.storage.androidsdk.sdk.ui.utils.rememberLambdaT

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun KitTextField(
    modifier: Modifier = Modifier,
    labelTextModifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
    trailingIconPadding: PaddingValues = PaddingValues(horizontal = 8.dp),
    labelText: String? = null,
    labelColor: Color = kitColor.secondary,
    singleLine: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    maxLength: Int = Int.MAX_VALUE,
    textColor: Color,
    backgroundBrush: Brush,
    onClick: () -> Unit = {},
    readOnly: Boolean = false,
    verticalPadding: Dp = 8.dp,
    horizontalPadding: Dp = 16.dp,
    textAlign: TextAlign = TextAlign.Start,
    errorText: String = "",
    showError: Boolean = false
) {

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding
            )
            .bringIntoViewRequester(bringIntoViewRequester)
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(verticalPadding / 2)
    ) {
        if (labelText?.isNotBlank() == true)
            Text(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .then(labelTextModifier),
                text = labelText,
                style = TextStyle(
                    color = labelColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.W400
                ),
                color = labelColor
            )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(color = kitColor.tertiaryInverted),
                    onClick = onClick
                )
                .onFocusEvent { focusState ->
                    if (focusState.isFocused) {
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                }
                .border(
                    width = if (showError) 1.dp else 0.dp,
                    color = if (showError) Color.Red else Color.Transparent,
                    shape = RoundedCornerShape(6.dp)
                )
                .background(
                    brush = backgroundBrush,
                    shape = RoundedCornerShape(6.dp),
                ),
        ) {
            BasicTextField(
                modifier = Modifier
                    .then(textFieldModifier)
                    .background(Color.Transparent),
                readOnly = readOnly,
                value = value,
                onValueChange = rememberLambdaT {
                    if (it.text.length <= maxLength) {
                        onValueChange(it)
                    }
                },
                textStyle = TextStyle(
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    textAlign = textAlign
                ),
                keyboardActions = keyboardActions,
                keyboardOptions = keyboardOptions,
                singleLine = singleLine,
                maxLines = maxLines,
                visualTransformation = visualTransformation,
                cursorBrush = SolidColor(kitColor.accent),
                decorationBox = @Composable {
                    Box(
                        modifier = Modifier.background(Color.Transparent),
                    ) {

                        Box(
                            modifier = Modifier
                                .align(if (textAlign == TextAlign.Start) Alignment.CenterStart else Alignment.Center)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            it()
                        }
                        Box(
                            modifier = Modifier
                                .padding(trailingIconPadding)
                                .align(Alignment.CenterEnd)
                        ) {
                            trailingIcon()
                        }
                    }
                },
                interactionSource = interactionSource,
            )
        }

        if (errorText.isNotBlank() && showError) {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = errorText,
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