package kz.tarlanpayments.storage.androidsdk.sdk.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import kz.tarlanpayments.storage.androidsdk.sdk.setLocale

@Composable
internal fun LanguageDropDown(
    modifier: Modifier = Modifier,
    onLanguageChanged: (String) -> Unit,
    currentLanguage: String,
    accentColor: Color,
) {
    val isLanguageDropDownExpanded = remember { mutableStateOf(false) }
    val secondaryColor = Color(0xFFB0B0B0)
    val context = LocalContext.current

    Column(modifier = modifier) {
        KitLanguageButton(
            modifier = Modifier,
            title = currentLanguage,
            textColor = secondaryColor,
            accentColor = accentColor,
            onClick = {
                isLanguageDropDownExpanded.value = true
            }
        )

        fun setLocale(locale: String) {
            isLanguageDropDownExpanded.value = false
            context.setLocale(locale)
            onLanguageChanged(locale)
        }
        DropdownMenu(
            expanded = isLanguageDropDownExpanded.value,
            onDismissRequest = { isLanguageDropDownExpanded.value = false },
            properties = PopupProperties(focusable = true),
            offset = DpOffset(0.dp, 0.dp)
        ) {
            Box(modifier = Modifier
                .height(32.dp)
                .clickable {
                    setLocale("RU")
                }) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 12.dp),
                    text = "RU",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = secondaryColor,
                        fontWeight = FontWeight.W400
                    )
                )
            }
            Box(modifier = Modifier
                .height(32.dp)
                .clickable {
                    setLocale("KZ")
                }) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 12.dp),
                    text = "KZ",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = secondaryColor,
                        fontWeight = FontWeight.W400
                    )
                )
            }
            Box(modifier = Modifier
                .height(32.dp)
                .clickable { setLocale("EN") }) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 12.dp),
                    text = "EN",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = secondaryColor,
                        fontWeight = FontWeight.W400
                    )
                )
            }
        }
    }
}