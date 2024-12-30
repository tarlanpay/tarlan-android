package kz.tarlanpayments.storage.androidsdk.noui.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.FrameLayout
import kotlinx.parcelize.Parcelize
import kz.tarlanpayments.storage.androidsdk.noui.R

@Parcelize
data class Tarlan3DSV2Input(
    val methodData: String,
    val action: String,
    val transactionId: Long,
    val transactionHash: String
) : Parcelable

internal class Tarlan3DSV2Activity : Activity() {

    companion object {
        fun intent(context: Context, input: Tarlan3DSV2Input): Intent {
            return Intent(context, Tarlan3DSV2Activity::class.java).apply {
                putExtra("launcher", input)
            }
        }
    }

    private val launcher by lazy {
        intent.getParcelableExtra<Tarlan3DSV2Input>("launcher")!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.three_ds_v2_webview)
        val webView = findViewById<Tarlan3DSV2Webview>(R.id.webView)
        val progressBar = findViewById<FrameLayout>(R.id.progressBar)
        webView.authorize(
            action = launcher.action,
            methodData = launcher.methodData
        )

        webView.authListener = object : Tarlan3DSV2Webview.Listener {
            override fun on3dsPageLoading() {
                progressBar.visibility = View.VISIBLE
            }

            override fun onSuccess() {
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}