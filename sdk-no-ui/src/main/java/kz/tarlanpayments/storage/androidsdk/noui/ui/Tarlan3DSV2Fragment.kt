package kz.tarlanpayments.storage.androidsdk.noui.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import kz.tarlanpayments.storage.androidsdk.noui.R

class Tarlan3DSV2Fragment : Fragment() {

    companion object {
        const val TARLAN_3DS_REQUEST_KEY = "TARLAN_3DS_REQUEST_KEY"
        const val TARLAN_3DS_RESULT = "TARLAN_3DS_RESULT"
        fun newInstance(
            methodData: String,
            action: String,
            transactionId: Long,
            transactionHash: String
        ) = Tarlan3DSV2Fragment()
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

        val webView = view.findViewById<Tarlan3DSV2Webview>(R.id.webView)
        val progressBar = view.findViewById<FrameLayout>(R.id.progressBar)
        webView.authorize(
            action = requireArguments().getString("action")!!,
            methodData = requireArguments().getString("methodData")!!
        )

        webView.authListener = object : Tarlan3DSV2Webview.Listener {
            override fun on3dsPageLoading() {
                progressBar.visibility = View.VISIBLE
            }

            override fun onSuccess() {
                setFragmentResult(Tarlan3DSFragment.TARLAN_3DS_REQUEST_KEY, Bundle().apply {
                    putBoolean(TARLAN_3DS_RESULT, true)
                })
            }
        }
    }
}