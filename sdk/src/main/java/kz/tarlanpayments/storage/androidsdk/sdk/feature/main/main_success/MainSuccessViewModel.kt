package kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kz.tarlanpayments.storage.androidsdk.sdk.DepsHolder
import kz.tarlanpayments.storage.androidsdk.sdk.core.BaseViewModel
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionInfoMainRs
import kz.tarlanpayments.storage.androidsdk.sdk.data.dto.TransactionRs

internal sealed interface MainSuccessState {
    data object Loading : MainSuccessState
    data object Default : MainSuccessState
}

internal sealed interface MainSuccessAction {
    data class FromInterface(
        val cardNumber: String,
        val cvv: String? = null,
        val year: String? = null,
        val month: String? = null,
        val cardHolder: String? = null,
        val email: String? = null,
        val phone: String? = null,
        val saveCard: Boolean = false
    ) : MainSuccessAction

    data class FromSavedCard(
        val encryptedId: String,
        val email: String? = null,
        val phone: String? = null,
    ) : MainSuccessAction

    data class FromGooglePay(
        val paymentMethodData: Map<String, Any>
    ) : MainSuccessAction

    data class DeleteCard(
        val projectId: Long,
        val cardId: String,
    ) : MainSuccessAction

}

internal sealed interface MainSuccessEffect {

    data class ShowError(val transactionHash: String, val transactionId: Long) : MainSuccessEffect
    data class ShowSuccess(val transactionHash: String, val transactionId: Long) : MainSuccessEffect

    data class Show3ds(
        val params: HashMap<String, String>,
        val termUrl: String,
        val action: String, val transactionId: Long, val transactionHash: String,
        val isCardLink: Boolean
    ) : MainSuccessEffect

    data class ShowFingerprint(
        val methodData: String,
        val action: String,
        val transactionHash: String,
        val transactionId: Long
    ) : MainSuccessEffect

}

internal class MainSuccessViewModel(
    private val transactionId: Long,
    private val transactionHash: String,
    private val transactionInfoMainRs: TransactionInfoMainRs
) : BaseViewModel<MainSuccessState, MainSuccessAction, MainSuccessEffect>(MainSuccessState.Default) {

    private val repository = DepsHolder.tarlanRepository

    override fun handleEvent(event: MainSuccessAction) {
        when (event) {
            is MainSuccessAction.FromInterface -> {
                when (transactionInfoMainRs.transactionType.code) {
                    TransactionInfoMainRs.TransactionTypeDto.OUT -> {
                        setState { MainSuccessState.Loading }
                        launchSaved {
                            val state = repository.outRq(
                                transactionId = transactionId,
                                hash = transactionHash,
                                cardNumber = event.cardNumber,
                                email = event.email,
                                phone = event.phone
                            )
                            state.result.handleState()
                        }
                    }

                    TransactionInfoMainRs.TransactionTypeDto.IN -> {
                        setState { MainSuccessState.Loading }
                        launchSaved {
                            val state = repository.inRq(
                                transactionId = transactionId,
                                hash = transactionHash,
                                cardNumber = event.cardNumber,
                                cvv = event.cvv!!,
                                month = event.month!!,
                                year = event.year!!,
                                cardHolder = event.cardHolder!!,
                                email = event.email,
                                phone = event.phone,
                                savaCard = event.saveCard
                            )
                            state.result.handleState()
                        }
                    }

                    TransactionInfoMainRs.TransactionTypeDto.CARD_LINK -> {
                        setState { MainSuccessState.Loading }
                        launchSaved {
                            val state = repository.cardLink(
                                transactionId = transactionId,
                                hash = transactionHash,
                                cardNumber = event.cardNumber,
                                cvv = event.cvv!!,
                                month = event.month!!,
                                year = event.year!!,
                                cardHolder = event.cardHolder!!,
                                email = event.email,
                                phone = event.phone,

                                )
                            state.result.handleState()
                        }
                    }
                }
            }

            is MainSuccessAction.FromSavedCard -> {
                when (transactionInfoMainRs.transactionType.code) {
                    TransactionInfoMainRs.TransactionTypeDto.OUT -> {
                        setState { MainSuccessState.Loading }
                        launchSaved {
                            val state = repository.outFromSaved(
                                transactionId = transactionId,
                                hash = transactionHash,
                                email = event.email,
                                phone = event.phone,
                                encryptedId = event.encryptedId
                            )
                            state.result.handleState()
                        }
                    }

                    TransactionInfoMainRs.TransactionTypeDto.IN -> {
                        setState { MainSuccessState.Loading }
                        launchSaved {
                            val state = repository.inFromSavedRq(
                                transactionId = transactionId,
                                hash = transactionHash,
                                email = event.email,
                                phone = event.phone,
                                encryptedId = event.encryptedId
                            )
                            state.result.handleState()
                        }
                    }
                }
            }

            is MainSuccessAction.FromGooglePay -> {
                setState { MainSuccessState.Loading }
                launchSaved {
                    val state = repository.googlePay(
                        transactionId = transactionId,
                        hash = transactionHash,
                        paymentMethodData = event.paymentMethodData,
                    )
                    state.result.handleState()
                }
            }

            is MainSuccessAction.DeleteCard -> {
                viewModelScope.launch {
                    try {
                        repository.deleteCard(
                            transactionHash = transactionHash,
                            transactionId = transactionId,
                            projectId = event.projectId,
                            encryptedCardId = event.cardId
                        )
                    } catch (e: Exception) {
//                        setEffect { MainSuccessEffect.ShowError(transactionHash, transactionId) }
                    }
                }
            }
        }
    }

    private fun TransactionRs.handleState() {
        setState { MainSuccessState.Default }

        when (this.transactionStatusCode) {
            TransactionRs.Success -> {
                setEffect {
                    MainSuccessEffect.ShowSuccess(
                        transactionHash,
                        transactionId
                    )
                }
            }

            TransactionRs.ThreeDsWaiting -> {
                setEffect {
                    MainSuccessEffect.Show3ds(
                        termUrl = this.threeDs?.termUrl ?: "",
                        action = this.threeDs?.action ?: "",
                        params = this.threeDs?.params ?: hashMapOf(),
                        transactionHash = transactionHash,
                        transactionId = transactionId,
                        isCardLink = transactionInfoMainRs.transactionType.code == TransactionInfoMainRs.TransactionTypeDto.CARD_LINK
                    )
                }
            }

            TransactionRs.Fingerprint -> {
                setEffect {
                    MainSuccessEffect.ShowFingerprint(
                        methodData = this.fingerprint?.methodData ?: "",
                        action = this.fingerprint?.methodUrl ?: "",
                        transactionHash = transactionHash,
                        transactionId = transactionId
                    )
                }
            }

            else -> {
                setEffect {
                    MainSuccessEffect.ShowError(
                        transactionHash,
                        transactionId
                    )
                }
            }
        }
    }

    private fun launchSaved(action: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            try {
                action()
            } catch (e: Exception) {
                setEffect { MainSuccessEffect.ShowError(transactionHash, transactionId) }
                setState { MainSuccessState.Default }
            }
        }
    }

    internal class MainSuccessViewModelFactory(
        private val transactionInfoMainRs: TransactionInfoMainRs,
        private val transactionId: Long,
        private val transactionHash: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainSuccessViewModel(
                transactionId = transactionId,
                transactionHash = transactionHash,
                transactionInfoMainRs = transactionInfoMainRs
            ) as T
        }
    }
}