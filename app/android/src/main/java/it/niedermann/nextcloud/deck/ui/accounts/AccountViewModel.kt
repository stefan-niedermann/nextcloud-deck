package it.niedermann.nextcloud.deck.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.niedermann.nextcloud.deck.domain.model.Account
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.accounts.RemoveAccountUseCase
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase
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
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getCurrentAccountUseCase: GetCurrentAccountUseCase,
    private val setCurrentAccountUseCase: SetCurrentAccountUseCase,
    private val removeAccountUseCase: RemoveAccountUseCase
) : ViewModel() {

    private val _currentAccountId = MutableStateFlow<Account.ID?>(null)
    val currentAccountId = _currentAccountId.asStateFlow()

    val accounts: StateFlow<List<Account>> = FlowAdapters.toPublisher(getAccountsUseCase.execute())
        .asFlow()
        .map { it.toList() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        refreshCurrentAccount()
    }

    fun refreshCurrentAccount() {
        viewModelScope.launch {
            try {
                _currentAccountId.value = getCurrentAccountUseCase.execute().await()
            } catch (e: Exception) {
                // Catch EmptyResultSetException when no accounts exist
                _currentAccountId.value = null
            }
        }
    }

    fun switchAccount(accountId: Account.ID) {
        viewModelScope.launch {
            try {
                setCurrentAccountUseCase.execute(accountId).await()
                _currentAccountId.value = accountId
            } catch (e: Exception) {
                // Ignore or handle session persist error
            }
        }
    }

    fun deleteAccount(accountId: Account.ID) {
        viewModelScope.launch {
            try {
                removeAccountUseCase.execute(accountId).await()
            } catch (e: Exception) {
                // Ignore EmptyResultSetException if the DELETE query returns no row
            }
            if (_currentAccountId.value == accountId) {
                refreshCurrentAccount()
            }
        }
    }
}
