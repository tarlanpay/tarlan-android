package kz.tarlanpayments.storage.androidsdk.sdk.feature.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import kz.tarlanpayments.storage.androidsdk.TarlanInput
import kz.tarlanpayments.storage.androidsdk.noui.ui.Tarlan3DSContract
import kz.tarlanpayments.storage.androidsdk.noui.ui.Tarlan3DSInput
import kz.tarlanpayments.storage.androidsdk.noui.ui.Tarlan3DSV2Contract
import kz.tarlanpayments.storage.androidsdk.noui.ui.Tarlan3DSV2Input
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

        val launcher3DS =
            rememberLauncherForActivityResult(contract = Tarlan3DSContract()) { result ->
                TarlanActivity.router.newRootScreen(
                    TarlanScreens.Status(
                        transactionId = launcher.transactionId,
                        hash = launcher.hash
                    )
                )
            }

        val launcher3DSV2 =
            rememberLauncherForActivityResult(contract = Tarlan3DSV2Contract()) { result ->
                TarlanActivity.router.newRootScreen(
                    TarlanScreens.MainScreen(
                        input = TarlanInput(
                            isDebug = launcher.isDebug,
                            localeCode = launcher.localeCode,
                            transactionId = launcher.transactionId,
                            hash = launcher.hash
                        ),
                        isResume = true
                    )
                )
            }


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
                    launcher = launcher,
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
                        launcher3DS.launch(
                            Tarlan3DSInput(
                                params = it.params,
                                termUrl = it.termUrl,
                                action = it.action,
                                transactionId = it.transactionId,
                                transactionHash = it.hash
                            )
                        )
                    }

                    is MainEffect.ShowFingerprint -> {
                        launcher3DSV2.launch(
                            Tarlan3DSV2Input(
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