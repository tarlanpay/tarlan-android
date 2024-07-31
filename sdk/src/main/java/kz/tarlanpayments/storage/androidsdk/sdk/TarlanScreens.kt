package kz.tarlanpayments.storage.androidsdk.sdk

import com.github.terrakok.cicerone.androidx.FragmentScreen
import kz.tarlanpayments.storage.androidsdk.TarlanInput
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.MainFragment
import kz.tarlanpayments.storage.androidsdk.sdk.feature.status.StatusFragment
import kz.tarlanpayments.storage.androidsdk.noui.ui.Tarlan3DSFragment
import kz.tarlanpayments.storage.androidsdk.noui.ui.Tarlan3DSV2Fragment

internal object TarlanScreens {

    fun MainScreen(input: TarlanInput, isResume: Boolean): FragmentScreen {
        return FragmentScreen { MainFragment.newInstance(input, isResume) }
    }

    fun Status(transactionId: Long, hash: String): FragmentScreen {
        return FragmentScreen { StatusFragment.newInstance(transactionId, hash) }
    }

    fun ThreeDs(
        termUrl: String,
        action: String,
        transactionId: Long,
        transactionHash: String,
        params: Map<String, String>,
    ): FragmentScreen {
        return FragmentScreen {
            Tarlan3DSFragment.newInstance(
                params = params,
                termUrl = termUrl,
                action = action,
                transactionId = transactionId,
                transactionHash = transactionHash,
            )
        }
    }

    fun Fingerprint(
        methodData: String,
        action: String,
        transactionId: Long,
        transactionHash: String,
    ): FragmentScreen {
        return FragmentScreen {
            Tarlan3DSV2Fragment.newInstance(
                methodData = methodData,
                action = action,
                transactionId = transactionId,
                transactionHash = transactionHash
            )
        }
    }
}