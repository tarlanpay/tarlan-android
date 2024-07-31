package kz.tarlanpayments.storage.androidsdk.noui.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import kz.tarlanpayments.storage.androidsdk.noui.R

class Tarlan3DSFragment : Fragment() {

    companion object {
        const val TARLAN_3DS_REQUEST_KEY = "TARLAN_3DS_REQUEST_KEY"
        const val TARLAN_3DS_RESULT = "TARLAN_3DS_RESULT"

        fun newInstance(
            params: Map<String, String>,
            termUrl: String,
            action: String,
            transactionId: Long,
            transactionHash: String
        ) = Tarlan3DSFragment()
            .apply {
                arguments = Bundle().apply {
                    putSerializable("params", HashMap(params))
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

        val webView = view.findViewById<Tarlan3DSWebview>(R.id.webView)
        val progressBar = view.findViewById<FrameLayout>(R.id.progressBar)
        webView.authorize(
            params = requireArguments().getSerializable(
                "params",
            ) as HashMap<String, String>,
            action = requireArguments().getString("action")!!,
            postbackUrl = requireArguments().getString("termUrl")!!,
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
                setFragmentResult(TARLAN_3DS_REQUEST_KEY, Bundle().apply {
                    putBoolean("TARLAN_3DS_RESULT", true)
                })
            }
        }
    }
}