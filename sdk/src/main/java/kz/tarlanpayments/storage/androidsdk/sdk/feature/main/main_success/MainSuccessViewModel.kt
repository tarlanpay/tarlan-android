package kz.tarlanpayments.storage.androidsdk.sdk.feature.main.main_success

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kz.tarlanpayments.storage.androidsdk.noui.TarlanInstance
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionDescriptionModel
import kz.tarlanpayments.storage.androidsdk.noui.TarlanTransactionStateModel
import kz.tarlanpayments.storage.androidsdk.sdk.core.BaseViewModel

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
        val params: Map<String, String>,
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
    private val transactionDescription: TarlanTransactionDescriptionModel
) : BaseViewModel<MainSuccessState, MainSuccessAction, MainSuccessEffect>(MainSuccessState.Default) {

    private val repository = TarlanInstance.tarlanRepository

    override fun handleEvent(event: MainSuccessAction) {
        when (event) {
            is MainSuccessAction.FromInterface -> {
                when (transactionDescription.type) {
                    TarlanTransactionDescriptionModel.TransactionType.Out -> {
                        setState { MainSuccessState.Loading }
                        launchSaved {
                            val state = repository.outRq(
                                transactionId = transactionId,
                                hash = transactionHash,
                                cardNumber = event.cardNumber,
                                email = event.email,
                                phone = event.phone
                            )
                            state.handleState(transactionDescription.type)
                        }
                    }

                    TarlanTransactionDescriptionModel.TransactionType.In -> {
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
                            state.handleState(transactionDescription.type)
                        }
                    }

                    TarlanTransactionDescriptionModel.TransactionType.CardLink -> {
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
                            state.handleState(transactionDescription.type)
                        }
                    }
                }
            }

            is MainSuccessAction.FromSavedCard -> {
                when (transactionDescription.type) {
                    TarlanTransactionDescriptionModel.TransactionType.Out -> {
                        setState { MainSuccessState.Loading }
                        launchSaved {
                            val state = repository.outFromSaved(
                                transactionId = transactionId,
                                hash = transactionHash,
                                email = event.email,
                                phone = event.phone,
                                encryptedId = event.encryptedId
                            )
                            state.handleState(transactionDescription.type)
                        }
                    }

                    TarlanTransactionDescriptionModel.TransactionType.In -> {
                        setState { MainSuccessState.Loading }
                        launchSaved {
                            val state = repository.inFromSavedRq(
                                transactionId = transactionId,
                                hash = transactionHash,
                                email = event.email,
                                phone = event.phone,
                                encryptedId = event.encryptedId
                            )
                            state.handleState(transactionDescription.type)
                        }
                    }

                    else -> Unit
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
                    state.handleState(transactionDescription.type)
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

    private fun TarlanTransactionStateModel.handleState(type: TarlanTransactionDescriptionModel.TransactionType) {
        setState { MainSuccessState.Default }

        when (this) {
            is TarlanTransactionStateModel.Success -> {
                setEffect {
                    MainSuccessEffect.ShowSuccess(
                        transactionHash,
                        transactionId
                    )
                }
            }

            is TarlanTransactionStateModel.Waiting3DS -> {
                setEffect {
                    MainSuccessEffect.Show3ds(
                        termUrl = this.termUrl,
                        action = this.action,
                        params = this.params,
                        transactionHash = transactionHash,
                        transactionId = transactionId,
                        isCardLink = type == TarlanTransactionDescriptionModel.TransactionType.CardLink
                    )
                }
            }

            is TarlanTransactionStateModel.FingerPrint -> {
                setEffect {
                    MainSuccessEffect.ShowFingerprint(
                        methodData = this.methodData,
                        action = this.methodUrl,
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
        private val transactionDescription: TarlanTransactionDescriptionModel,
        private val transactionId: Long,
        private val transactionHash: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainSuccessViewModel(
                transactionId = transactionId,
                transactionHash = transactionHash,
                transactionDescription = transactionDescription
            ) as T
        }
    }
}