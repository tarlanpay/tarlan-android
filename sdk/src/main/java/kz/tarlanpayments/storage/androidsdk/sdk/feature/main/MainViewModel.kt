package kz.tarlanpayments.storage.androidsdk.sdk.feature.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import kz.tarlanpayments.storage.androidsdk.sdk.DepsHolder
import kz.tarlanpayments.storage.androidsdk.sdk.core.BaseViewModel
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionColorRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionInfoMainRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionInfoPayFormRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionRs

internal sealed interface MainState {
    object Loading : MainState

    data class Success(
        val colorsDto: TransactionColorRs,
        val payFormRs: TransactionInfoPayFormRs,
        val mainInfo: TransactionInfoMainRs
    ) : MainState

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
        val params: HashMap<String, String>,
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

    private val repository by lazy { DepsHolder.tarlanRepository }

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
                )
                    .result.handleState()
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
                setEffect {
                    MainEffect.ShowError(transactionId, hash)
                }
            }
        }
    }

    private fun getTransaction() {
        viewModelScope.launch {
            try {
                val result = repository.getTransactionStatus(
                    transactionId = transactionId,
                    hash = hash
                )

                if (result.transactionInfoMain.isTransactionCompleted()) {
                    result.transactionInfoMain.doTransactionCompleted()
                } else {
                    setState {
                        MainState.Success(
                            result.transactionColor,
                            result.transactionPayForm,
                            result.transactionInfoMain
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
                setEffect { MainEffect.ShowError(transactionId, hash) }
            }
        }
    }

    private fun TransactionRs.handleState() {
        when (this.transactionStatusCode) {
            TransactionRs.Success -> setEffect {
                MainEffect.ShowSuccess(
                    transactionId,
                    hash
                )
            }

            TransactionRs.ThreeDsWaiting ->
                setEffect {
                    MainEffect.Show3ds(
                        termUrl = this.threeDs?.termUrl ?: "",
                        action = this.threeDs?.action ?: "",
                        params = this.threeDs?.params ?: hashMapOf(),
                        hash = hash,
                        transactionId = transactionId
                    )
                }

            TransactionRs.Fingerprint ->
                setEffect {
                    MainEffect.ShowFingerprint(
                        methodData = this.fingerprint?.methodData ?: "",
                        action = this.fingerprint?.methodUrl ?: "",
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


    private fun TransactionInfoMainRs.isTransactionCompleted(): Boolean {
        return when (this.transactionStatus.code) {
            TransactionInfoMainRs.TransactionStatusDto.NEW -> false
            else -> true
        }
    }

    private fun TransactionInfoMainRs.doTransactionCompleted() {
        when (this.transactionStatus.code) {
            TransactionInfoMainRs.TransactionStatusDto.SUCCESS -> {
                setEffect {
                    MainEffect.ShowSuccess(
                        transactionId,
                        hash
                    )
                }
            }

            TransactionInfoMainRs.TransactionStatusDto.FAIL -> {
                setEffect {
                    MainEffect.ShowError(
                        transactionId,
                        hash
                    )
                }
            }

            TransactionInfoMainRs.TransactionStatusDto.REFUND -> {
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
