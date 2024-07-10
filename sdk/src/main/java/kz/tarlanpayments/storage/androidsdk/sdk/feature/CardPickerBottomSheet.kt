package kz.tarlanpayments.storage.androidsdk.sdk.feature

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kz.tarlanpayments.storage.androidsdk.R
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionColorRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionInfoMainRs
import kz.tarlanpayments.storage.androidsdk.sdk.utils.toFormGradient
import kz.tarlanpayments.storage.androidsdk.sdk.provideCurrentLocale
import kz.tarlanpayments.storage.androidsdk.sdk.ui.theme.KitTheme
import kz.tarlanpayments.storage.androidsdk.sdk.ui.utils.parseColor
import kz.tarlanpayments.storage.androidsdk.sdk.utils.Localization

internal class CardPickerBottomSheet : BottomSheetDialogFragment() {

    @Parcelize
    data class Launcher(
        val transactionColorRs: TransactionColorRs,
        val savedCard: List<TransactionInfoMainRs.CardDto>,
    ) : Parcelable

    companion object {
        fun newInstance(launcher: Launcher): CardPickerBottomSheet {
            return CardPickerBottomSheet().apply {
                arguments = bundleOf("launcher" to launcher)
            }
        }
    }


    var onNewCardClicked: () -> Unit = {}
    var onCardRemoved: (String, String) -> Unit = { _, _ -> }
    var onSelectCard: (String, String) -> Unit = { _, _ -> }

    private val launcher by lazy { requireArguments().getParcelable<Launcher>("launcher")!! }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setContent {
            KitTheme(
                shape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
                content = { ViewContent() })
        }
    }

    @Composable
    private fun ViewContent() {
        var localCards by remember { mutableStateOf(launcher.savedCard) }
        val currentLocale = LocalContext.current.provideCurrentLocale()
        LazyColumn(
            modifier = Modifier.background(
                launcher.transactionColorRs.toFormGradient(),
                shape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp)
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {

            item {
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .height(4.dp)
                        .width(32.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(size = 3.dp),
                        )
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                        .clickable { this@CardPickerBottomSheet.dismiss() }
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = Localization.getString(
                            Localization.KeyAddCardHint,
                            locale = currentLocale
                        ),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = parseColor(color = launcher.transactionColorRs.inputLabelColor),
                            fontWeight = FontWeight.W400
                        ),
                        color = parseColor(color = launcher.transactionColorRs.inputLabelColor),
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                        .clickable {
                            onNewCardClicked()
                            this@CardPickerBottomSheet.dismiss()
                        }
                        .background(color = Color(0x00000000).copy(alpha = 0.2f))
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = Localization.getString(
                            Localization.KeyAddNewCard,
                            locale = currentLocale
                        ),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = parseColor(color = launcher.transactionColorRs.inputLabelColor),
                            fontWeight = FontWeight.W400
                        ),
                        color = parseColor(color = launcher.transactionColorRs.inputLabelColor),
                    )
                }
            }

            items(
                count = localCards.size,
                itemContent = {
                    ShowCard(
                        onSelectCard = onSelectCard,
                        onCardRemoved = { cardToken, cardMask ->
                            onCardRemoved(cardToken, cardMask)
                            localCards = localCards.filter { item -> item.cardToken != cardToken }
                        },
                        card = localCards[it],
                        launcher = launcher,
                        onDismiss = {
                            this@CardPickerBottomSheet.dismiss()
                        }
                    )
                },
                key = { card -> localCards[card].cardToken + kotlin.random.Random.nextInt() }
            )
        }
    }
}

@Composable
private fun ShowCard(
    onSelectCard: (String, String) -> Unit,
    onCardRemoved: (String, String) -> Unit,
    card: TransactionInfoMainRs.CardDto,
    launcher: CardPickerBottomSheet.Launcher,
    onDismiss: () -> Unit,
) {

    val currentLocale = LocalContext.current.provideCurrentLocale()
    var isExpanded by remember { mutableStateOf(false) }
    var isRemoving by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .clickable {
                onSelectCard(card.cardToken, card.maskedPan)
                onDismiss()
            }
            .background(color = Color(0x00000000).copy(alpha = if (!isExpanded) 0.2f else 0.4f))
    ) {

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = isRemoving,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = Localization.getString(
                    Localization.KeyRemovingCard,
                    locale = currentLocale
                ),
                style = TextStyle(
                    fontSize = 14.sp,
                    color = parseColor(color = launcher.transactionColorRs.inputLabelColor),
                    fontWeight = FontWeight.W400
                ),
                color = parseColor(color = launcher.transactionColorRs.inputLabelColor),
            )

        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = !isRemoving,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = card.maskedPan.replace("X", "*").replace("-", " "),
                style = TextStyle(
                    fontSize = 14.sp,
                    color = parseColor(color = launcher.transactionColorRs.inputLabelColor),
                    fontWeight = FontWeight.W400
                ),
                color = parseColor(color = launcher.transactionColorRs.inputLabelColor),
            )
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.CenterStart),
            visible = !isExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { isExpanded = true },
                imageVector = Icons.Outlined.Delete,
                contentDescription = ""
            )
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.CenterStart),
            visible = isExpanded && !isRemoving,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row {
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            isExpanded = false
                        },
                    imageVector = Icons.Outlined.Close,
                    contentDescription = ""
                )
                Icon(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            isRemoving = true
                            coroutineScope.launch {
                                delay(1000)
                                isExpanded = false
                                onCardRemoved(card.cardToken, card.maskedPan)
                            }
                        },
                    imageVector = Icons.Outlined.Done,
                    contentDescription = ""
                )
            }
        }
    }
}
