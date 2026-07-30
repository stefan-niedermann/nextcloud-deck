package it.niedermann.nextcloud.deck.ui.boards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.niedermann.nextcloud.deck.domain.model.Board
import it.niedermann.nextcloud.deck.domain.model.CreateBoard
import it.niedermann.nextcloud.deck.domain.state.SyncStatus
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler
import it.niedermann.nextcloud.deck.domain.usecases.boards.AddBoardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext
import org.reactivestreams.FlowAdapters

@HiltViewModel
class BoardListViewModel @Inject constructor(
    private val getCurrentAccountUseCase: GetCurrentAccountUseCase,
    private val listBoardsUseCase: ListBoardsUseCase,
    private val addBoardUseCase: AddBoardUseCase,
    private val syncScheduler: SyncScheduler
) : ViewModel() {

    private val _boards = MutableStateFlow<List<Board>>(emptyList())
    val boards = _boards.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _syncStatus = MutableStateFlow<SyncStatus?>(null)
    val syncStatus = _syncStatus.asStateFlow()

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init {
        loadBoards()
    }

    fun loadBoards() {
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                val accountId = withContext(Dispatchers.IO) {
                    try {
                        getCurrentAccountUseCase.execute().await()
                    } catch (e: Exception) {
                        null
                    }
                }
                
                if (accountId != null) {
                    launch(Dispatchers.IO) {
                        FlowAdapters.toPublisher(listBoardsUseCase.execute(accountId))
                            .asFlow()
                            .collect {
                                withContext(Dispatchers.Main) {
                                    _boards.value = it
                                    isLoading = false
                                }
                            }
                    }
                } else {
                    _boards.value = emptyList()
                    isLoading = false
                }
            } catch (e: Exception) {
                error = e.message ?: "Failed to load boards"
                isLoading = false
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val accountId = withContext(Dispatchers.IO) {
                    getCurrentAccountUseCase.execute().await()
                }
                if (accountId != null) {
                    launch(Dispatchers.IO) {
                        FlowAdapters.toPublisher(syncScheduler.scheduleSynchronization(accountId))
                            .asFlow()
                            .collect { status ->
                                _syncStatus.value = status
                            }
                    }
                }
            } catch (e: Exception) {
                error = e.message ?: "Sync failed"
            } finally {
                _isRefreshing.value = false
                _syncStatus.value = null
                loadBoards()
            }
        }
    }

    fun addBoard(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accountId = getCurrentAccountUseCase.execute().await()
                addBoardUseCase.addBoard(CreateBoard(accountId, title)).await()
                withContext(Dispatchers.Main) {
                    loadBoards() // Manual refresh for mock repository
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = e.message ?: "Failed to add board"
                }
            }
        }
    }
}
