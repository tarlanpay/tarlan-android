package kz.tarlanpayments.storage.androidsdk.sdk.feature.status

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.smarttoolfactory.screenshot.ImageResult
import com.smarttoolfactory.screenshot.ScreenshotBox
import com.smarttoolfactory.screenshot.ScreenshotState
import com.smarttoolfactory.screenshot.rememberScreenshotState
import kz.tarlanpayments.storage.androidsdk.R
import kz.tarlanpayments.storage.androidsdk.sdk.feature.Localization
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.toFormGradient
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.toTextGradient
import kz.tarlanpayments.storage.androidsdk.sdk.provideCurrentLocale
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitBorderButton
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitCompanyLogo
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitDotDivider
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitTitleValueComponent
import kz.tarlanpayments.storage.androidsdk.sdk.ui.LanguageDropDown
import kz.tarlanpayments.storage.androidsdk.sdk.ui.Theme.kitColor
import kz.tarlanpayments.storage.androidsdk.sdk.ui.theme.KitTheme
import kz.tarlanpayments.storage.androidsdk.sdk.DepsHolder
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionInfoMainRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionStatusRs
import kz.tarlanpayments.storage.androidsdk.sdk.ui.utils.createPdfFromBitmap
import kz.tarlanpayments.storage.androidsdk.sdk.ui.utils.parseColor
import kz.tarlanpayments.storage.androidsdk.sdk.ui.utils.sharePdf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


internal class StatusFragment : Fragment() {

    companion object {
        fun newInstance(transactionId: Long, hash: String): StatusFragment {
            return StatusFragment().apply {
                this.arguments = Bundle().apply {
                    this.putLong("transactionId", transactionId)
                    this.putString("hash", hash)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent { KitTheme { MainContent() } }
    }


    @Composable
    private fun MainContent() {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val viewModel = viewModel<StatusViewModel>(
                factory = FailViewModelFactory(
                    transactionId = arguments?.getLong("transactionId") ?: 0L,
                    hash = arguments?.getString("hash") ?: ""
                )
            )
            val screenshotState = rememberScreenshotState()
            val state by viewModel.viewState.collectAsState()
            var loading by remember { mutableStateOf(false) }

            val context = LocalContext.current
            val imageResult: ImageResult = screenshotState.imageState.value

            when (state) {
                is StatusState.Error -> {
                    this@StatusFragment.activity?.setResult(Activity.RESULT_CANCELED)
                    this@StatusFragment.activity?.finish()
                }

                StatusState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(48.dp)
                        )
                    }
                }

                is StatusState.Success -> {
                    val currentState = state as StatusState.Success
                    LaunchedEffect(key1 = imageResult) {
                        if (imageResult is ImageResult.Success || imageResult is ImageResult.Error) {
                            loading = false
                            sharePdf(
                                context,
                                "${currentState.transactionStatusRs.transactionInfoMain.transactionId}.pdf"
                            )
                        }
                    }


                    if (!loading) {
                        StatusMainComponent(
                            transactionStatusRs = currentState.transactionStatusRs,
                            screenshotState = screenshotState,
                            onSaveClick = {
                                loading = true
                                screenshotState.capture()
                                screenshotState.bitmap?.let {
                                    createPdfFromBitmap(
                                        context = context,
                                        bitmap = it,
                                        fileName = "${currentState.transactionStatusRs.transactionInfoMain.transactionId}.pdf"
                                    )
                                }
                            },
                            onBackClick = {
                                when (currentState.transactionStatusRs.transactionInfoMain.transactionStatus.code) {
                                    TransactionInfoMainRs.TransactionStatusDto.SUCCESS -> {
                                        activity?.setResult(Activity.RESULT_OK, Intent().apply {
                                            putExtra(
                                                "transactionId",
                                                arguments?.getLong("transactionId")
                                            )
                                        })
                                        activity?.finish()
                                    }

                                    TransactionInfoMainRs.TransactionStatusDto.FAIL -> {
                                        activity?.setResult(Activity.RESULT_CANCELED)
                                        activity?.finish()
                                    }

                                    TransactionInfoMainRs.TransactionStatusDto.REFUND -> {
                                        if (currentState.transactionStatusRs.transactionInfoMain.transactionType.code == TransactionInfoMainRs.TransactionTypeDto.CARD_LINK) {
                                            activity?.setResult(Activity.RESULT_OK, Intent().apply {
                                                putExtra(
                                                    "transactionId",
                                                    arguments?.getLong("transactionId")
                                                )
                                            })
                                            activity?.finish()
                                        } else {
                                            activity?.setResult(Activity.RESULT_CANCELED)
                                            activity?.finish()
                                        }
                                    }

                                    else -> {
                                        activity?.setResult(Activity.RESULT_CANCELED)
                                        activity?.finish()
                                    }
                                }
                                activity?.setResult(Activity.RESULT_CANCELED)
                                activity?.finish()
                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(48.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun StatusMainComponent(
        transactionStatusRs: TransactionStatusRs,
        screenshotState: ScreenshotState,
        onSaveClick: () -> Unit = {},
        onBackClick: () -> Unit = {},
    ) {
        val context = LocalContext.current
        var currentLocale by remember { mutableStateOf(context.provideCurrentLocale()) }
        var redirectTime by
        remember { mutableIntStateOf(transactionStatusRs.transactionPayForm.timeout ?: 0) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                ScreenshotBox(screenshotState = screenshotState) {
                    Column {
                        Spacer(modifier = Modifier.size(32.dp))
                        transactionStatusRs.toIcon().invoke(this)
                        Spacer(modifier = Modifier.size(8.dp))
                        transactionStatusRs.toTitle().invoke(this)
                        Spacer(modifier = Modifier.height(8.dp))
                        transactionStatusRs.toSubtitle().invoke(this)
                        Spacer(modifier = Modifier.height(8.dp))
                        transactionStatusRs.toBody().invoke(this)
                    }
                }

                Column(
                    Modifier.padding(horizontal = 16.dp)
                ) {
                    transactionStatusRs.toActions(
                        onBackClick = onBackClick,
                        onSaveClick = onSaveClick,
                    ).invoke(this)
                }

                if (transactionStatusRs.transactionPayForm.hasRedirect) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 10.dp, horizontal = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        text = String.format(
                            Localization.getString(
                                Localization.KeyRedirect,
                                currentLocale
                            ), redirectTime
                        ),
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W400,
                        )
                    )
                }

                KitCompanyLogo().invoke(this)
            }

            LanguageDropDown(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(horizontal = 16.dp),
                onLanguageChanged = { currentLocale = it },
                currentLanguage = currentLocale,
                accentColor = parseColor(color = transactionStatusRs.transactionColor.mainFormColor)
            )
        }

        LaunchedEffect(this) {
            if (transactionStatusRs.transactionPayForm.hasRedirect) {
                while (redirectTime > 0) {
                    redirectTime--
                    kotlinx.coroutines.delay(1000)
                }
                onBackClick()
            }
        }
    }

    private fun TransactionStatusRs.toIcon(): @Composable ColumnScope.() -> Unit = {
        when (this@toIcon.transactionInfoMain.transactionStatus.code) {
            TransactionInfoMainRs.TransactionStatusDto.SUCCESS -> {
                AsyncImage(
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally),
                    model = DepsHolder.provideImage(transactionPayForm.logoFilePath),
                    contentDescription = "",
                    error = painterResource(id = R.drawable.ic_error_placaholder),
                    placeholder = painterResource(id = R.drawable.ic_error_placaholder)
                )
            }

            TransactionInfoMainRs.TransactionStatusDto.FAIL -> {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(64.dp),
                    painter = painterResource(id = R.drawable.ic_warrning),
                    contentDescription = "",
                    tint = kitColor.attention
                )
            }

            TransactionInfoMainRs.TransactionStatusDto.REFUND -> {
                when (transactionInfoMain.transactionType.code) {
                    TransactionInfoMainRs.TransactionTypeDto.CARD_LINK -> {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .size(64.dp),
                            painter = painterResource(id = R.drawable.ic_success),
                            contentDescription = "",
                            tint = kitColor.positive
                        )
                    }

                    else -> {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .size(64.dp),
                            painter = painterResource(id = R.drawable.ic_error),
                            contentDescription = "",
                            tint = kitColor.negative
                        )
                    }
                }
            }

            else -> {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(64.dp),
                    painter = painterResource(id = R.drawable.ic_error),
                    contentDescription = "",
                    tint = kitColor.negative
                )
            }
        }
    }

    private fun TransactionStatusRs.toTitle(): @Composable ColumnScope.() -> Unit = {
        val secondaryColor = Color(0xFF555555)
        val currentLocale = LocalContext.current.provideCurrentLocale()
        when (this@toTitle.transactionInfoMain.transactionStatus.code) {
            TransactionInfoMainRs.TransactionStatusDto.SUCCESS -> {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = transactionPayForm.storeName,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = secondaryColor,
                        fontWeight = FontWeight.W400
                    ),
                    color = secondaryColor
                )
            }

            TransactionInfoMainRs.TransactionStatusDto.FAIL -> {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = Localization.getString(
                        Localization.KeyBillFail,
                        currentLocale
                    ),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = kitColor.attention,
                        fontWeight = FontWeight.W700
                    ),
                    textAlign = TextAlign.Center
                )
            }

            TransactionInfoMainRs.TransactionStatusDto.REFUND -> {
                when (transactionInfoMain.transactionType.code) {
                    TransactionInfoMainRs.TransactionTypeDto.CARD_LINK ->
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = Localization.getString(
                                Localization.KeyBillCardLinkSuccess,
                                currentLocale
                            ),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = kitColor.positive,
                                fontWeight = FontWeight.W700
                            ),
                            color = secondaryColor
                        )

                    else -> Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = Localization.getString(
                            Localization.KeyBillFail,
                            currentLocale
                        ),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = kitColor.negative,
                            fontWeight = FontWeight.W700
                        ),
                        color = kitColor.negative
                    )
                }
            }

            else -> {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = Localization.getString(
                        Localization.KeyBillFail,
                        currentLocale
                    ),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = kitColor.negative,
                        fontWeight = FontWeight.W700
                    ),
                    color = kitColor.negative
                )
            }
        }
    }

    private fun TransactionStatusRs.toSubtitle(): @Composable ColumnScope.() -> Unit = {

        val secondaryColor = Color(0xFF555555)
        val currentLocale = LocalContext.current.provideCurrentLocale()
        when (this@toSubtitle.transactionInfoMain.transactionStatus.code) {
            TransactionInfoMainRs.TransactionStatusDto.SUCCESS -> {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "${transactionBillRs!!.orderAmount}₸",
                    style = TextStyle(
                        brush = transactionColor.toTextGradient(),
                        fontWeight = FontWeight.W700,
                        fontSize = 32.sp,
                    ),
                )
            }

            TransactionInfoMainRs.TransactionStatusDto.FAIL -> {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = Localization.getString(
                        Localization.KeyBillFailSubtitle,
                        currentLocale
                    ),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = kitColor.secondary,
                        fontWeight = FontWeight.W400
                    ),
                    textAlign = TextAlign.Center
                )
            }

            TransactionInfoMainRs.TransactionStatusDto.REFUND -> {
                when (transactionInfoMain.transactionType.code) {
                    TransactionInfoMainRs.TransactionTypeDto.CARD_LINK ->
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = Localization.getString(
                                Localization.KeyBillCardLinkSuccessSubtitle,
                                currentLocale
                            ),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = kitColor.positive,
                                fontWeight = FontWeight.W400
                            ),
                            color = secondaryColor
                        )

                    else -> Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = Localization.getString(
                            Localization.KeyBillFailSubtitle,
                            currentLocale
                        ),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = kitColor.secondary,
                            fontWeight = FontWeight.W400
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = Localization.getString(
                        Localization.KeyBillFailSubtitle,
                        currentLocale
                    ),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = kitColor.secondary,
                        fontWeight = FontWeight.W400
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    private fun TransactionStatusRs.toBody(): @Composable ColumnScope.() -> Unit = {
        val secondaryColor = Color(0xFF555555)
        val currentLocale = LocalContext.current.provideCurrentLocale()
        when (this@toBody.transactionInfoMain.transactionStatus.code) {
            TransactionInfoMainRs.TransactionStatusDto.SUCCESS -> {
                Spacer(modifier = Modifier.size(32.dp))

                KitDotDivider(secondaryColor)

                listOf(
                    Localization.getString(
                        Localization.KeyOrderNumber,
                        currentLocale
                    ) to transactionBillRs!!.transactionId.toString(),
                    Localization.getString(
                        Localization.KeyAmount,
                        currentLocale
                    ) to "${transactionBillRs.orderAmount}₸",
                    Localization.getString(
                        Localization.KeyFee,
                        currentLocale
                    ) to "${transactionBillRs.upperCommissionAmount}₸",
                    Localization.getString(
                        Localization.KeyDate,
                        currentLocale
                    ) to convertIsoToStandard(transactionBillRs.dateTime),
                    Localization.getString(
                        Localization.KeyBank,
                        currentLocale
                    ) to transactionBillRs.acquirerName,
                ).forEach {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        KitTitleValueComponent(
                            title = it.first,
                            value = it.second,
                            titleColor = parseColor(color = transactionColor.mainFormColor),
                            valueColor = secondaryColor
                        )
                    }
                }

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = transactionBillRs.paymentOrganization,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = secondaryColor,
                        fontWeight = FontWeight.W500
                    ),
                    color = secondaryColor
                )

                Spacer(modifier = Modifier.size(16.dp))

                KitDotDivider(secondaryColor = secondaryColor)
            }

            else -> Unit
        }
    }


    private fun TransactionStatusRs.toActions(
        onSaveClick: () -> Unit,
        onBackClick: () -> Unit,
    ): @Composable ColumnScope.() -> Unit = {
        val currentLocale = LocalContext.current.provideCurrentLocale()
        when (this@toActions.transactionInfoMain.transactionStatus.code) {
            TransactionInfoMainRs.TransactionStatusDto.SUCCESS -> {
                Spacer(modifier = Modifier.height(8.dp))

                KitBorderButton(
                    title = Localization.getString(Localization.KeySaveBill, currentLocale),
                    brush = transactionColor.toFormGradient(),
                    accentColor = parseColor(color = transactionColor.mainFormColor),
                    onClick = onSaveClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                KitBorderButton(
                    title = Localization.getString(Localization.KeyBack, currentLocale),
                    brush = transactionColor.toFormGradient(),
                    accentColor = parseColor(color = transactionColor.mainFormColor),
                    onClick = onBackClick
                )
            }

            TransactionInfoMainRs.TransactionStatusDto.FAIL -> {
                Spacer(modifier = Modifier.height(8.dp))
                KitBorderButton(
                    title = Localization.getString(Localization.KeyBack, currentLocale),
                    brush = transactionColor.toFormGradient(),
                    accentColor = parseColor(color = transactionColor.mainFormColor),
                    onClick = onBackClick
                )
            }

            TransactionInfoMainRs.TransactionStatusDto.REFUND -> {
                Spacer(modifier = Modifier.height(8.dp))
                KitBorderButton(
                    title = Localization.getString(Localization.KeyBack, currentLocale),
                    brush = transactionColor.toFormGradient(),
                    accentColor = parseColor(color = transactionColor.mainFormColor),
                    onClick = onBackClick
                )
            }

            else -> {
                Spacer(modifier = Modifier.height(8.dp))
                KitBorderButton(
                    title = Localization.getString(Localization.KeyBack, currentLocale),
                    brush = transactionColor.toFormGradient(),
                    accentColor = parseColor(color = transactionColor.mainFormColor),
                    onClick = onBackClick
                )
            }
        }
    }
}

fun convertIsoToStandard(isoDateString: String): String {
    val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
    val date: Date = isoFormatter.parse(isoDateString)
    val standardFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    standardFormatter.timeZone = TimeZone.getDefault()
    return standardFormatter.format(date)
}