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
import kz.tarlanpayments.storage.androidsdk.noui.TarlanInstance
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionDescriptionModel
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionStatusModel
import kz.tarlanpayments.storage.androidsdk.sdk.provideCurrentLocale
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitBorderButton
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitCompanyLogo
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitDotDivider
import kz.tarlanpayments.storage.androidsdk.sdk.ui.KitTitleValueComponent
import kz.tarlanpayments.storage.androidsdk.sdk.ui.LanguageDropDown
import kz.tarlanpayments.storage.androidsdk.sdk.ui.Theme.kitColor
import kz.tarlanpayments.storage.androidsdk.sdk.ui.theme.KitTheme
import kz.tarlanpayments.storage.androidsdk.sdk.ui.utils.createPdfFromBitmap
import kz.tarlanpayments.storage.androidsdk.sdk.ui.utils.parseColor
import kz.tarlanpayments.storage.androidsdk.sdk.ui.utils.sharePdf
import kz.tarlanpayments.storage.androidsdk.sdk.utils.Localization
import kz.tarlanpayments.storage.androidsdk.sdk.utils.toFormGradient
import kz.tarlanpayments.storage.androidsdk.sdk.utils.toTextGradient
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

                is StatusState.Loading -> {
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
                                "${currentState.transactionDescriptionModel.transactionId}.pdf"
                            )
                        }
                    }


                    if (!loading) {
                        StatusMainComponent(
                            transactionDescriptionModel = currentState.transactionDescriptionModel,
                            screenshotState = screenshotState,
                            onSaveClick = {
                                loading = true
                                screenshotState.capture()
                                screenshotState.bitmap?.let {
                                    createPdfFromBitmap(
                                        context = context,
                                        bitmap = it,
                                        fileName = "${currentState.transactionDescriptionModel.transactionId}.pdf"
                                    )
                                }
                            },
                            onBackClick = {
                                when (currentState.transactionDescriptionModel.status) {
                                    TarlanTransactionStatusModel.Success -> {
                                        activity?.setResult(Activity.RESULT_OK, Intent().apply {
                                            putExtra(
                                                "transactionId",
                                                arguments?.getLong("transactionId")
                                            )
                                        })
                                        activity?.finish()
                                    }

                                    TarlanTransactionStatusModel.Fail, TarlanTransactionStatusModel.AvailableTypes, TarlanTransactionStatusModel.Error -> {
                                        activity?.setResult(Activity.RESULT_CANCELED)
                                        activity?.finish()
                                    }

                                    TarlanTransactionStatusModel.Refund -> {
                                        if (currentState.transactionDescriptionModel.type == TarlanTransactionDescriptionModel.TransactionType.CardLink) {
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
        transactionDescriptionModel: TarlanTransactionDescriptionModel,
        screenshotState: ScreenshotState,
        onSaveClick: () -> Unit = {},
        onBackClick: () -> Unit = {},
    ) {
        val context = LocalContext.current
        var currentLocale by remember { mutableStateOf(context.provideCurrentLocale()) }
        var redirectTime by remember { mutableIntStateOf(transactionDescriptionModel.timeout ?: 0) }
        var stopRedirect by remember { mutableStateOf(false) }

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
                        transactionDescriptionModel.toIcon().invoke(this)
                        Spacer(modifier = Modifier.size(8.dp))
                        transactionDescriptionModel.toTitle().invoke(this)
                        Spacer(modifier = Modifier.height(8.dp))
                        transactionDescriptionModel.toSubtitle().invoke(this)
                        Spacer(modifier = Modifier.height(8.dp))
                        transactionDescriptionModel.toBody().invoke(this)
                    }
                }

                Column(
                    Modifier.padding(horizontal = 16.dp)
                ) {
                    transactionDescriptionModel.toActions(
                        onBackClick = onBackClick,
                        onSaveClick = onSaveClick,
                    ).invoke(this)
                }

                if (transactionDescriptionModel.hasRedirect && stopRedirect.not()) {
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
                            textAlign = TextAlign.Center
                        )
                    )

                    Box(modifier = Modifier.padding(16.dp)) {
                        KitBorderButton(
                            title = Localization.getString(Localization.KeyStayOnPage, currentLocale),
                            brush = transactionDescriptionModel.toFormGradient(),
                            accentColor = parseColor(color = transactionDescriptionModel.mainFormColor),
                            onClick = { stopRedirect = true }
                        )
                    }
                }

                KitCompanyLogo().invoke(this)
            }

            LanguageDropDown(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(horizontal = 16.dp),
                onLanguageChanged = { currentLocale = it },
                currentLanguage = currentLocale,
                accentColor = parseColor(color = transactionDescriptionModel.mainFormColor)
            )
        }

        LaunchedEffect(this) {
            if (transactionDescriptionModel.hasRedirect && stopRedirect.not()) {
                while (redirectTime > 0) {
                    redirectTime--
                    kotlinx.coroutines.delay(1000)
                }
                onBackClick()
            }
        }
    }

    private fun TarlanTransactionDescriptionModel.toIcon(): @Composable ColumnScope.() -> Unit = {
        when (this@toIcon.status) {
            TarlanTransactionStatusModel.Success -> {
                when (type) {
                    TarlanTransactionDescriptionModel.TransactionType.CardLink -> {
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
                        AsyncImage(
                            modifier = Modifier
                                .size(64.dp)
                                .align(Alignment.CenterHorizontally),
                            model = TarlanInstance.provideImage(this@toIcon.logoFilePath),
                            contentDescription = "",
                            error = painterResource(id = R.drawable.ic_error_placaholder),
                            placeholder = painterResource(id = R.drawable.ic_error_placaholder)
                        )
                    }
                }
            }

            TarlanTransactionStatusModel.Fail -> {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(64.dp),
                    painter = painterResource(id = R.drawable.ic_warrning),
                    contentDescription = "",
                    tint = kitColor.attention
                )
            }

            TarlanTransactionStatusModel.Refund -> {
                when (this@toIcon.type) {
                    TarlanTransactionDescriptionModel.TransactionType.CardLink -> {
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

            TarlanTransactionStatusModel.AvailableTypes -> {
                AsyncImage(
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally),
                    model = TarlanInstance.provideImage(this@toIcon.logoFilePath),
                    contentDescription = "",
                    error = painterResource(id = R.drawable.ic_error_placaholder),
                    placeholder = painterResource(id = R.drawable.ic_error_placaholder)
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

    private fun TarlanTransactionDescriptionModel.toTitle(): @Composable ColumnScope.() -> Unit = {
        val secondaryColor = Color(0xFF555555)
        val currentLocale = LocalContext.current.provideCurrentLocale()
        when (this@toTitle.status) {
            TarlanTransactionStatusModel.Success -> {

                when (this@toTitle.type) {
                    TarlanTransactionDescriptionModel.TransactionType.CardLink ->
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
                            color = secondaryColor,
                            textAlign = TextAlign.Center
                        )

                    else -> Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = this@toTitle.storeName,
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = secondaryColor,
                            fontWeight = FontWeight.W400
                        ),
                        color = secondaryColor,
                        textAlign = TextAlign.Center
                    )
                }
            }

            TarlanTransactionStatusModel.Fail -> {
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

            TarlanTransactionStatusModel.AvailableTypes -> {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = Localization.getString(
                        Localization.KeyNoAvailableTypes,
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

            TarlanTransactionStatusModel.Refund -> {
                when (this@toTitle.type) {
                    TarlanTransactionDescriptionModel.TransactionType.CardLink ->
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
                            color = secondaryColor,
                            textAlign = TextAlign.Center
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
                        color = kitColor.negative,
                        textAlign = TextAlign.Center
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
                    color = kitColor.negative,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    private fun TarlanTransactionDescriptionModel.toSubtitle(): @Composable ColumnScope.() -> Unit =
        {

            val secondaryColor = Color(0xFF555555)
            val currentLocale = LocalContext.current.provideCurrentLocale()
            when (this@toSubtitle.status) {
                TarlanTransactionStatusModel.Success -> {
                    when (this@toSubtitle.type) {
                        TarlanTransactionDescriptionModel.TransactionType.CardLink ->
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
                                color = secondaryColor,
                                textAlign = TextAlign.Center
                            )

                        else -> Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = "${this@toSubtitle.totalAmount}₸",
                            style = TextStyle(
                                brush = this@toSubtitle.toTextGradient(),
                                fontWeight = FontWeight.W700,
                                fontSize = 32.sp,
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                TarlanTransactionStatusModel.Fail -> {
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

                TarlanTransactionStatusModel.Refund -> {
                    when (this@toSubtitle.type) {
                        TarlanTransactionDescriptionModel.TransactionType.CardLink ->
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
                                color = secondaryColor,
                                textAlign = TextAlign.Center
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

                TarlanTransactionStatusModel.AvailableTypes -> Unit

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

    private fun TarlanTransactionDescriptionModel.toBody(): @Composable ColumnScope.() -> Unit = {
        val secondaryColor = Color(0xFF555555)
        val currentLocale = LocalContext.current.provideCurrentLocale()
        when (this@toBody.status) {
            TarlanTransactionStatusModel.Success -> {
                when (this@toBody.type) {
                    TarlanTransactionDescriptionModel.TransactionType.CardLink -> Unit
                    else -> {
                        Spacer(modifier = Modifier.size(32.dp))

                        KitDotDivider(secondaryColor)

                        val items = mutableListOf<Pair<String, String>>()

                        items.apply {
                            add(
                                Localization.getString(
                                    Localization.KeyOrderNumber,
                                    currentLocale
                                ) to this@toBody.transactionId.toString()
                            )
                            add(
                                Localization.getString(
                                    Localization.KeyAmount,
                                    currentLocale
                                ) to "${this@toBody.orderAmount}₸"
                            )

                            if (!this@toBody.projectName.isNullOrEmpty()) {
                                add(
                                    Localization.getString(
                                        Localization.KeyMerchantName,
                                        currentLocale
                                    ) to this@toBody.projectName.toString()
                                )
                            }

                            add(
                                Localization.getString(
                                    Localization.KeyBank,
                                    currentLocale
                                ) to this@toBody.acquirerName.toString()
                            )

                            if (!this@toBody.email.isNullOrEmpty()) {
                                add(
                                    Localization.getString(
                                        Localization.KeyEmail,
                                        currentLocale
                                    ) to this@toBody.email.toString()
                                )
                            }

                            if (!this@toBody.phone.isNullOrEmpty()) {
                                add(
                                    Localization.getString(
                                        Localization.KeyPhone,
                                        currentLocale
                                    ) to this@toBody.phone.toString()
                                )
                            }

                            add(
                                Localization.getString(
                                    Localization.KeyDate,
                                    currentLocale
                                ) to convertIsoToStandard(this@toBody.dateTime ?: "")
                            )

                            add(
                                Localization.getString(
                                    Localization.KeyFee,
                                    currentLocale
                                ) to "${this@toBody.upperCommissionAmount}₸"
                            )

                            add(
                                Localization.getString(
                                    Localization.KeyTransactionType,
                                    currentLocale
                                ) to this@toBody.transactionTypeName.toString()
                            )
                        }
                        items.forEach {
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                KitTitleValueComponent(
                                    title = it.first,
                                    value = it.second,
                                    titleColor = parseColor(color = this@toBody.mainFormColor),
                                    valueColor = secondaryColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.size(16.dp))

                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = this@toBody.paymentOrganization ?: "",
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
                }
            }

            else -> Unit
        }
    }


    private fun TarlanTransactionDescriptionModel.toActions(
        onSaveClick: () -> Unit,
        onBackClick: () -> Unit,
    ): @Composable ColumnScope.() -> Unit = {
        val currentLocale = LocalContext.current.provideCurrentLocale()
        when (this@toActions.status) {
            TarlanTransactionStatusModel.Success -> {
                Spacer(modifier = Modifier.height(8.dp))

                KitBorderButton(
                    title = Localization.getString(Localization.KeySaveBill, currentLocale),
                    brush = this@toActions.toFormGradient(),
                    accentColor = parseColor(color = this@toActions.mainFormColor),
                    onClick = onSaveClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                KitBorderButton(
                    title = Localization.getString(Localization.KeyBack, currentLocale),
                    brush = this@toActions.toFormGradient(),
                    accentColor = parseColor(color = this@toActions.mainFormColor),
                    onClick = onBackClick
                )
            }

            TarlanTransactionStatusModel.Fail -> {
                Spacer(modifier = Modifier.height(8.dp))
                KitBorderButton(
                    title = Localization.getString(Localization.KeyBack, currentLocale),
                    brush = this@toActions.toFormGradient(),
                    accentColor = parseColor(color = this@toActions.mainFormColor),
                    onClick = onBackClick
                )
            }

            TarlanTransactionStatusModel.Refund -> {
                Spacer(modifier = Modifier.height(8.dp))
                KitBorderButton(
                    title = Localization.getString(Localization.KeyBack, currentLocale),
                    brush = this@toActions.toFormGradient(),
                    accentColor = parseColor(color = this@toActions.mainFormColor),
                    onClick = onBackClick
                )
            }

            else -> {
                Spacer(modifier = Modifier.height(8.dp))
                KitBorderButton(
                    title = Localization.getString(Localization.KeyBack, currentLocale),
                    brush = this@toActions.toFormGradient(),
                    accentColor = parseColor(color = this@toActions.mainFormColor),
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