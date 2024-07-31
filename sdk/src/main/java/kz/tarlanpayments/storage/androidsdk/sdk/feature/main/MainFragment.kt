package kz.tarlanpayments.storage.androidsdk.sdk.feature.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import kz.tarlanpayments.storage.androidsdk.TarlanInput
import kz.tarlanpayments.storage.androidsdk.noui.ui.Tarlan3DSFragment
import kz.tarlanpayments.storage.androidsdk.noui.ui.Tarlan3DSV2Fragment
import kz.tarlanpayments.storage.androidsdk.sdk.GooglePayFacade
import kz.tarlanpayments.storage.androidsdk.sdk.TarlanActivity
import kz.tarlanpayments.storage.androidsdk.sdk.TarlanScreens
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success.MainSuccessView
import kz.tarlanpayments.storage.androidsdk.sdk.ui.theme.KitTheme

internal class MainFragment : Fragment() {
    private val googlePayFacade = GooglePayFacade()

    companion object {
        fun newInstance(launcher: TarlanInput, isResume: Boolean = false) = MainFragment().apply {
            arguments = Bundle().apply {
                this.putParcelable("launcher", launcher)
                this.putBoolean("isResume", isResume)
            }
        }
    }

    private val launcher by lazy { requireArguments().getParcelable<TarlanInput>("launcher")!! }
    private val isResume by lazy { requireArguments().getBoolean("isResume") }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        this.setContent {
            KitTheme {
                MainContent(this@MainFragment)
            }
        }
    }

    @Composable
    private fun MainContent(fragment: Fragment) {
        val viewModel = viewModel<MainViewModel>(
            factory = MainViewModelFactory(
                transactionId = launcher.transactionId,
                hash = launcher.hash,
                isResume = isResume
            )
        )

        val state by viewModel.viewState.collectAsState()

        when (state) {
            is MainState.Error -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Ошибка",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            MainState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp)
                    )
                }
            }

            is MainState.Success -> {
                val currentState = state as MainState.Success
                MainSuccessView(
                    transactionId = launcher.transactionId,
                    transactionHash = launcher.hash,
                    transactionDescription = currentState.transactionDescription,
                    fragment = fragment,
                    googlePayFacade = googlePayFacade
                )
            }
        }

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest {
                when (it) {
                    is MainEffect.ShowSuccess -> {
                        TarlanActivity.router.newRootScreen(
                            TarlanScreens.Status(
                                it.transactionId,
                                it.hash
                            )
                        )
                    }

                    is MainEffect.ShowError -> {
                        TarlanActivity.router.newRootScreen(
                            TarlanScreens.Status(
                                it.transactionId,
                                it.hash
                            )
                        )
                    }

                    is MainEffect.Show3ds -> {
                        setFragmentResultListener(Tarlan3DSFragment.TARLAN_3DS_REQUEST_KEY) { _, bundle ->
                            TarlanActivity.router.newRootScreen(
                                TarlanScreens.Status(
                                    it.transactionId,
                                    it.hash
                                )
                            )
                        }
                        TarlanActivity.router.newRootScreen(
                            TarlanScreens.ThreeDs(
                                termUrl = it.termUrl,
                                action = it.action,
                                transactionId = it.transactionId,
                                transactionHash = it.hash,
                                params = it.params,
                            )
                        )
                    }

                    is MainEffect.ShowFingerprint -> {
                        setFragmentResultListener(Tarlan3DSV2Fragment.TARLAN_3DS_REQUEST_KEY) { _, bundle ->
                            TarlanActivity.router.newRootScreen(
                                TarlanScreens.Status(
                                    it.transactionId,
                                    it.hash
                                )
                            )
                        }
                        TarlanActivity.router.newRootScreen(
                            TarlanScreens.Fingerprint(
                                methodData = it.methodData,
                                action = it.action,
                                transactionId = it.transactionId,
                                transactionHash = it.hash
                            )
                        )
                    }
                }
            }
        }
    }
}