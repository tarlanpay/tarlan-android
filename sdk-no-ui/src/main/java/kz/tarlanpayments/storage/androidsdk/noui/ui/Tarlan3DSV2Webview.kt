package kz.tarlanpayments.storage.androidsdk.noui.ui

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import okhttp3.FormBody
import okio.Buffer
import java.io.IOException

internal class Tarlan3DSV2Webview @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : WebView(context, attrs, defStyleAttr) {

    var authListener: Listener? = null

    init {
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                authListener?.on3dsPageLoading()
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                authListener?.onSuccess()
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
        methodData: String,
    ) {
        val paramsBytes = buildParamsOkHttp(
            mapOf("method_data" to methodData)
        )
        postUrl(action, paramsBytes)
    }

    private fun buildParamsOkHttp(params: Map<String, String>): ByteArray {
        val encodingBuilder = FormBody.Builder()
        params.forEach { (key, value) ->
            encodingBuilder.add(key, value)
        }
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

        fun on3dsPageLoading()
    }
}