package it.niedermann.nextcloud.deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.niedermann.nextcloud.deck.domain.model.Account
import it.niedermann.nextcloud.deck.domain.model.Board
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import org.reactivestreams.FlowAdapters

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getCurrentAccountUseCase: GetCurrentAccountUseCase,
    private val getCurrentBoardUseCase: GetCurrentBoardUseCase,
    getAccountsUseCase: GetAccountsUseCase
) : ViewModel() {

    private val _currentAccountId = MutableStateFlow<Account.ID?>(null)
    val currentAccountId: StateFlow<Account.ID?> = _currentAccountId.asStateFlow()

    private val _currentBoardId = MutableStateFlow<Board.ID?>(null)
    val currentBoardId: StateFlow<Board.ID?> = _currentBoardId.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized = _isInitialized.asStateFlow()

    val hasAccounts: StateFlow<Boolean> = FlowAdapters.toPublisher(getAccountsUseCase.execute())
        .asFlow()
        .map { it.isNotEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true // Assume true until loaded to avoid flickering
        )

    init {
        refreshCurrentAccount()
    }

    fun refreshCurrentAccount() {
        viewModelScope.launch {
            try {
                val accountId = getCurrentAccountUseCase.execute().await()
                _currentAccountId.value = accountId
                _currentBoardId.value = try {
                    getCurrentBoardUseCase.execute(accountId).await()
                } catch (e: Exception) {
                    null
                }
            } catch (e: Exception) {
                // If no accounts are found, GetCurrentAccountUseCase might throw EmptyResultSetException
                // or if the CompletableFuture fails.
                _currentAccountId.value = null
                _currentBoardId.value = null
            } finally {
                _isInitialized.value = true
            }
        }
    }
}
