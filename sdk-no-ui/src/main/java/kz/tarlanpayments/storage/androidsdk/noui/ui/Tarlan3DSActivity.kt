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
data class Tarlan3DSInput(
    val params: Map<String, String>,
    val termUrl: String,
    val action: String,
    val transactionId: Long,
    val transactionHash: String
) : Parcelable

internal class Tarlan3DSActivity : Activity() {

    companion object {
        fun intent(context: Context, input: Tarlan3DSInput): Intent {
            return Intent(context, Tarlan3DSActivity::class.java).apply {
                putExtra("launcher", input)
            }
        }
    }

    private val launcher by lazy {
        intent.getParcelableExtra<Tarlan3DSInput>("launcher")!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.three_ds_webview)

        val webView = findViewById<Tarlan3DSWebview>(R.id.webView)
        val progressBar = findViewById<FrameLayout>(R.id.progressBar)
        webView.authorize(
            params = launcher.params,
            action = launcher.action,
            postbackUrl = launcher.termUrl,
            redirectUrl = "https://process.tarlanpayments.kz/"
        )

        webView.authListener = object : Tarlan3DSWebview.Listener {
            override fun on3dsPageLoading() {
                progressBar.visibility = View.VISIBLE
            }

            override fun on3dsPageLoaded() {
                progressBar.visibility = View.GONE
            }

            override fun onSuccess() {
                this@Tarlan3DSActivity.setResult(RESULT_OK)
            }
        }
    }
}