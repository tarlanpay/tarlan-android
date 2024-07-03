package kz.tarlanpayments.storage.androidsdk.sdk.feature.threeds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import kz.tarlanpayments.storage.androidsdk.R
import kz.tarlanpayments.storage.androidsdk.TarlanInput
import kz.tarlanpayments.storage.androidsdk.sdk.TarlanActivity
import kz.tarlanpayments.storage.androidsdk.sdk.TarlanScreens

internal class ThreeDsV2Fragment : Fragment() {

    companion object {
        fun newInstance(
            methodData: String,
            action: String,
            transactionId: Long,
            transactionHash: String
        ) = ThreeDsV2Fragment()
            .apply {
                arguments = Bundle().apply {
                    putString("methodData", methodData)
                    putString("action", action)
                    putLong("transactionId", transactionId)
                    putString("transactionHash", transactionHash)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.three_ds_v2_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView = view.findViewById<ThreeDsV2WebView>(R.id.webView)
        val progressBar = view.findViewById<FrameLayout>(R.id.progressBar)
        webView.authorize(
            action = requireArguments().getString("action")!!,
            methodData = requireArguments().getString("methodData")!!
        )

        webView.authListener = object : ThreeDsV2WebView.Listener {
            override fun on3dsPageLoading() {
                progressBar.visibility = View.VISIBLE
            }

            override fun onSuccess() {
                TarlanActivity.router.newRootScreen(
                    TarlanScreens.MainScreen(
                        input = TarlanInput(
                            transactionId = requireArguments().getLong("transactionId"),
                            hash = requireArguments().getString("transactionHash")!!
                        ),
                        isResume = true
                    )
                )
            }
        }
    }
}