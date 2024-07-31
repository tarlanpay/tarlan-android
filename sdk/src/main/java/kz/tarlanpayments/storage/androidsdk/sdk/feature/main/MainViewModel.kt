package kz.tarlanpayments.storage.androidsdk.sdk.feature.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import kz.tarlanpayments.storage.androidsdk.noui.TarlanInstance
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionDescriptionModel
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionStateModel
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionStatusModel
import kz.tarlanpayments.storage.androidsdk.sdk.core.BaseViewModel

internal sealed interface MainState {
    data object Loading : MainState

    data class Success(val transactionDescription: TarlanTransactionDescriptionModel) : MainState

    data object Error : MainState
}

internal sealed interface MainEffect {
    val transactionId: Long
    val hash: String

    data class ShowSuccess(
        override val transactionId: Long,
        override val hash: String
    ) : MainEffect

    data class ShowError(
        override val transactionId: Long,
        override val hash: String
    ) : MainEffect

    data class Show3ds(
        val termUrl: String,
        val action: String,
        val params: Map<String, String>,
        override val transactionId: Long,
        override val hash: String
    ) : MainEffect

    data class ShowFingerprint(
        val methodData: String,
        val action: String,
        override val transactionId: Long,
        override val hash: String
    ) : MainEffect
}

internal class MainViewModel(
    private val transactionId: Long,
    private val hash: String,
    isResume: Boolean
) :
    BaseViewModel<MainState, Unit, MainEffect>(MainState.Loading) {

    private val repository by lazy { TarlanInstance.tarlanRepository }

    init {
        when (isResume) {
            true -> resumeTransaction()
            false -> getTransaction()
        }
    }

    private fun resumeTransaction() {
        viewModelScope.launch {
            try {
                repository.resumeTransaction(
                    transactionId = transactionId,
                    hash = hash
                ).handleState()
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
                setEffect {
                    MainEffect.ShowError(transactionId, hash)
                }
            }
        }
    }

    private fun getTransaction() {
        Log.d("TarlanOutput", "getTransaction: $transactionId, $hash")
        viewModelScope.launch {
            try {
                val result = repository.getTransactionStatus(
                    transactionId = transactionId,
                    hash = hash
                )
                if (result.isTransactionCompleted()) {
                    Log.d("TarlanOutput", "isTransactionCompleted true")
                    result.doTransactionCompleted()
                } else {
                    Log.d("TarlanOutput", "isTransactionCompleted false")
                    val transactionDescription = repository.getTransactionDescription(
                        transactionId = transactionId,
                        hash = hash
                    )
                    Log.d("TarlanOutput", "isTransactionCompleted false")
                    setState { MainState.Success(transactionDescription) }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
                setEffect { MainEffect.ShowError(transactionId, hash) }
            }
        }
    }

    private fun TarlanTransactionStateModel.handleState() {
        when (this) {
            is TarlanTransactionStateModel.Success -> setEffect {
                MainEffect.ShowSuccess(transactionId, hash)
            }

            is TarlanTransactionStateModel.Waiting3DS ->
                setEffect {
                    MainEffect.Show3ds(
                        termUrl = this.termUrl,
                        action = this.action,
                        params = this.params.toMutableMap(),
                        hash = hash,
                        transactionId = transactionId
                    )
                }

            is TarlanTransactionStateModel.FingerPrint ->
                setEffect {
                    MainEffect.ShowFingerprint(
                        methodData = this.methodData,
                        action = this.methodUrl,
                        transactionId = transactionId,
                        hash = hash,
                    )
                }

            else -> {
                setEffect {
                    MainEffect.ShowError(
                        transactionId,
                        hash
                    )
                }
            }
        }
    }

    private fun TarlanTransactionStatusModel.doTransactionCompleted() {
        when (this) {
            TarlanTransactionStatusModel.Success -> {
                setEffect {
                    MainEffect.ShowSuccess(
                        transactionId,
                        hash
                    )
                }
            }

            TarlanTransactionStatusModel.Fail -> {
                setEffect {
                    MainEffect.ShowError(
                        transactionId,
                        hash
                    )
                }
            }

            TarlanTransactionStatusModel.Refund -> {
                setEffect {
                    MainEffect.ShowError(
                        transactionId,
                        hash
                    )
                }
            }

            else -> {
                setEffect {
                    MainEffect.ShowError(
                        transactionId,
                        hash
                    )
                }
            }
        }
    }
}

internal class MainViewModelFactory(
    private val hash: String,
    private val transactionId: Long,
    private val isResume: Boolean
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return MainViewModel(
            transactionId = transactionId,
            hash = hash,
            isResume = isResume
        ) as T
    }
}
