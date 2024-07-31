package kz.tarlanpayments.storage.androidsdk.sdk

import android.app.Activity
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import kotlinx.coroutines.tasks.await
import kz.tarlanpayments.storage.androidsdk.BuildConfig
import kz.tarlanpayments.storage.androidsdk.noui.TarlanInstance
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

internal class GooglePayFacade {

    var paymentsClient: PaymentsClient? = null

    suspend fun possiblyShowGooglePayButton(activity: FragmentActivity): Boolean {
        val isReadyToPayJson = isReadyToPayRequest() ?: return false
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
        return try {
            paymentsClient = createPaymentsClient(activity)
            paymentsClient?.isReadyToPay(request)?.await() ?: false
        } catch (exception: ApiException) {
            Log.e(exception.message,  exception.stackTraceToString())
            false
        }
    }

    fun getPaymentDataRequest(price: Double): JSONObject? {
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod()))
                put("transactionInfo", getTransactionInfo(price.toString()))
                put("merchantInfo", getMerchantInfo(BuildConfig.MERCHANT_GATEWAY))
            }
        } catch (e: JSONException) {
            Log.e(e.message, e.stackTraceToString())
            null
        }
    }

    private fun getTransactionInfo(price: String): JSONObject {
        return JSONObject().apply {
            put("totalPrice", price)
            put("totalPriceStatus", "FINAL")
            put("countryCode", "KZ")
            put("currencyCode", "KZT")
        }
    }

    private fun getMerchantInfo(merchantName: String): JSONObject {
        return JSONObject().apply {
            put("merchantName", merchantName)
        }
    }

    private val baseRequest = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
    }

    private fun gatewayTokenizationSpecification(): JSONObject {
        return JSONObject().apply {
            put("type", "PAYMENT_GATEWAY")
            put(
                "parameters", JSONObject(
                    mapOf(
                        "gateway" to BuildConfig.MERCHANT_GATEWAY,
                        "gatewayMerchantId" to BuildConfig.MERCHANT_GATEWAY_ID
                    )
                )
            )
        }
    }

    private val allowedCardNetworks = JSONArray(listOf("MASTERCARD", "VISA"))

    private val allowedCardAuthMethods = JSONArray(listOf("PAN_ONLY", "CRYPTOGRAM_3DS"))

    private fun baseCardPaymentMethod(): JSONObject {
        return JSONObject().apply {

            val parameters = JSONObject().apply {
                put("allowedAuthMethods", allowedCardAuthMethods)
                put("allowedCardNetworks", allowedCardNetworks)
            }

            put("type", "CARD")
            put("parameters", parameters)
            put("tokenizationSpecification", gatewayTokenizationSpecification())
        }
    }

    private fun cardPaymentMethod(): JSONObject {
        val cardPaymentMethod = baseCardPaymentMethod()
        cardPaymentMethod.put("tokenizationSpecification", gatewayTokenizationSpecification())

        return cardPaymentMethod
    }

    private fun createPaymentsClient(activity: Activity): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(if (TarlanInstance.isDebug) WalletConstants.ENVIRONMENT_TEST else WalletConstants.ENVIRONMENT_PRODUCTION)
            .build()

        return Wallet.getPaymentsClient(activity, walletOptions)
    }

    private fun isReadyToPayRequest(): JSONObject? {
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
            }

        } catch (e: JSONException) {
            null
        }
    }
}