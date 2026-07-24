package it.niedermann.nextcloud.deck.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.niedermann.nextcloud.deck.domain.model.SyncStatus
import it.niedermann.nextcloud.deck.domain.usecases.accounts.ImportAccountUseCase
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import org.reactivestreams.FlowAdapters
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val importAccountUseCase: ImportAccountUseCase,
    private val setCurrentAccountUseCase: SetCurrentAccountUseCase
) : ViewModel() {

    var url by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var syncStatus by mutableStateOf<SyncStatus?>(null)

    private val _loginSuccess = MutableSharedFlow<Unit>()
    val loginSuccess = _loginSuccess.asSharedFlow()

    fun login() {
        val serverUrl = try {
            URL(url)
        } catch (e: Exception) {
            error = "Invalid URL"
            return
        }

        isLoading = true
        error = null

        viewModelScope.launch {
            try {
                FlowAdapters.toPublisher(importAccountUseCase.execute(serverUrl, username, password))
                    .asFlow()
                    .collect { status ->
                        syncStatus = status
                        setCurrentAccountUseCase.execute(status.account().id()).await()
                    }
                _loginSuccess.emit(Unit)
            } catch (e: Exception) {
                error = e.message ?: "Login failed"
            } finally {
                isLoading = false
            }
        }
    }
}
