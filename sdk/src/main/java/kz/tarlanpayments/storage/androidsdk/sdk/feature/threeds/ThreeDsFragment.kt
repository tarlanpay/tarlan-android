package kz.tarlanpayments.storage.androidsdk.sdk.feature.threeds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import kz.tarlanpayments.storage.androidsdk.R
import kz.tarlanpayments.storage.androidsdk.sdk.TarlanActivity
import kz.tarlanpayments.storage.androidsdk.sdk.TarlanScreens

internal class ThreeDsFragment : Fragment() {

    companion object {
        fun newInstance(
            params: HashMap<String, String>,
            termUrl: String,
            action: String,
            transactionId: Long,
            transactionHash: String
        ) = ThreeDsFragment()
            .apply {
                arguments = Bundle().apply {
                    putSerializable("params", params)
                    putString("termUrl", termUrl)
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
        return inflater.inflate(R.layout.three_ds_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView = view.findViewById<ThreeDsWebView>(R.id.webView)
        val progressBar = view.findViewById<FrameLayout>(R.id.progressBar)
        webView.authorize(
            params = requireArguments().getSerializable(
                "params",
            ) as HashMap<String, String>,
            action = requireArguments().getString("action")!!,
            postbackUrl = requireArguments().getString("termUrl")!!,
            redirectUrl = "https://process.tarlanpayments.kz/"
        )

        webView.authListener = object : ThreeDsWebView.Listener {
            override fun on3dsPageLoading() {
                progressBar.visibility = View.VISIBLE
            }

            override fun on3dsPageLoaded() {
                progressBar.visibility = View.GONE
            }

            override fun onSuccess() {
                TarlanActivity.router.newRootScreen(
                    TarlanScreens.Status(
                        requireArguments().getLong("transactionId"),
                        requireArguments().getString("transactionHash")!!
                    )
                )
            }
        }
    }
}