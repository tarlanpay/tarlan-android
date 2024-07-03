package kz.tarlanpayments.storage.androidsdk.sdk

import com.github.terrakok.cicerone.androidx.FragmentScreen
import kz.tarlanpayments.storage.androidsdk.TarlanInput
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.MainFragment
import kz.tarlanpayments.storage.androidsdk.sdk.feature.status.StatusFragment
import kz.tarlanpayments.storage.androidsdk.sdk.feature.threeds.ThreeDsFragment
import kz.tarlanpayments.storage.androidsdk.sdk.feature.threeds.ThreeDsV2Fragment

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
        params: HashMap<String, String>,
    ): FragmentScreen {
        return FragmentScreen {
            ThreeDsFragment.newInstance(
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
            ThreeDsV2Fragment.newInstance(
                methodData = methodData,
                action = action,
                transactionId = transactionId,
                transactionHash = transactionHash
            )
        }
    }
}