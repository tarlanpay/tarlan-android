package kz.tarlanpayments.storage.androidsdk.sdk

import com.github.terrakok.cicerone.androidx.FragmentScreen
import kz.tarlanpayments.storage.androidsdk.TarlanInput
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.MainFragment
import kz.tarlanpayments.storage.androidsdk.sdk.feature.status.StatusFragment

internal object TarlanScreens {

    fun MainScreen(input: TarlanInput, isResume: Boolean): FragmentScreen {
        return FragmentScreen { MainFragment.newInstance(input, isResume) }
    }

    fun Status(transactionId: Long, hash: String): FragmentScreen {
        return FragmentScreen { StatusFragment.newInstance(transactionId, hash) }
    }
}