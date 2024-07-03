package kz.tarlanpayments.storage.androidsdk.sdk.feature.threeds

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import okhttp3.FormBody
import okio.Buffer
import okio.IOException

internal class ThreeDsWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : WebView(context, attrs, defStyleAttr) {

    var authListener: Listener? = null
    lateinit var redirectUrl: String
    lateinit var postbackUrl: String

    init {
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = true
//        settings.setAppCacheEnabled (false)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                authListener?.on3dsPageLoading()
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("3DSWebView", "onPageFinished: $url")
                if (url?.lowercase()?.contains(redirectUrl.toLowerCase()) == true) {
                    authListener?.onSuccess()
                } else {
                    authListener?.on3dsPageLoaded()
                }

                super.onPageFinished(view, url)
            }
        }

        webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                return super.onConsoleMessage(consoleMessage)
            }
        }
    }

    fun authorize(
        action: String,
        params: Map<String, String>,
        postbackUrl: String,
        redirectUrl: String,
    ) {
        this.redirectUrl = redirectUrl
        this.postbackUrl = postbackUrl
        val paramsBytes = buildParamsOkHttp(params, this.postbackUrl)
        postUrl(action, paramsBytes)
    }

    private fun buildParamsOkHttp(params: Map<String, String>, postbackUrl: String): ByteArray {
        val encodingBuilder = FormBody.Builder()
        params.forEach { (key, value) ->
            encodingBuilder.add(key, value)
        }
        encodingBuilder.add("TermUrl", postbackUrl)
        val formBody = encodingBuilder.build()
        val buffer = Buffer()

        try {
            formBody.writeTo(buffer)
        } catch (e: IOException) {
        }

        return buffer.readByteArray()
    }

    interface Listener {
        fun onSuccess()

        fun on3dsPageLoaded()

        fun on3dsPageLoading()
    }
}