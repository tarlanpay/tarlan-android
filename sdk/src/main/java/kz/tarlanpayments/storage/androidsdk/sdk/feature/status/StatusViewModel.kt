package kz.tarlanpayments.storage.androidsdk.sdk.feature.status

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kz.tarlanpayments.storage.androidsdk.sdk.DepsHolder
import kz.tarlanpayments.storage.androidsdk.sdk.core.BaseViewModel
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionStatusRs
import kz.tarlanpayments.storage.androidsdk.sdk.feature.main.MainEffect


internal sealed interface StatusState {
    data object Loading : StatusState
    data class Success(val transactionStatusRs: TransactionStatusRs) : StatusState
    data class Error(val transactionId: Long, val hash: String) : StatusState
}

internal class StatusViewModel(
    private val transactionId: Long,
    private val hash: String
) :
    BaseViewModel<StatusState, Unit, MainEffect>(StatusState.Loading) {

    private val repository by lazy { DepsHolder.tarlanRepository }

    init {
        viewModelScope.launch {
            try {
                val result = repository.getTransactionStatus(
                    transactionId = transactionId,
                    hash = hash
                )
                setState {
                    StatusState.Success(result)
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
                setState { StatusState.Error(transactionId, hash) }
            }
        }
    }
}

internal class FailViewModelFactory(
    private val transactionId: Long,
    private val hash: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatusViewModel(transactionId, hash) as T
    }
}