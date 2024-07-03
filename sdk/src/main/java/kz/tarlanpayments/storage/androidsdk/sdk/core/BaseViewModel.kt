package kz.tarlanpayments.storage.androidsdk.sdk.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal open class BaseViewModel<State, Action, Effect>(initialState: State) : ViewModel() {

    protected val currentState: State
        get() = viewState.value

    private val _viewState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val viewState = _viewState.asStateFlow()

    private val _action: MutableSharedFlow<Action> = MutableSharedFlow()
    val action = _action.asSharedFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        subscribeEvents()
    }

    fun setAction(event: Action) {
        val newEvent = event
        viewModelScope.launch { _action.emit(newEvent) }
    }

    protected open fun handleEvent(event: Action) {

    }

    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        _viewState.value = newState
    }

    protected fun setEffect(builder: () -> Effect) {
        val actionValue = builder()
        viewModelScope.launch { _effect.send(actionValue) }
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            action.collect {
                handleEvent(it)
            }
        }
    }
}