package it.niedermann.nextcloud.deck.ui.board

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.niedermann.nextcloud.deck.domain.model.Account
import it.niedermann.nextcloud.deck.domain.model.Board
import it.niedermann.nextcloud.deck.domain.model.Card
import it.niedermann.nextcloud.deck.domain.model.Column
import it.niedermann.nextcloud.deck.domain.model.CreateCard
import it.niedermann.nextcloud.deck.domain.model.CreateColumn
import it.niedermann.nextcloud.deck.domain.model.Label
import it.niedermann.nextcloud.deck.domain.model.User
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard
import it.niedermann.nextcloud.deck.domain.state.SyncStatus
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler
import it.niedermann.nextcloud.deck.domain.usecases.cards.AddCardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.AssignCardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.ListCardPreviewsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.MoveCardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.UnassignCardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.columns.AddColumnUseCase
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.labels.ListLabelsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentBoardUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext
import org.reactivestreams.FlowAdapters

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val listColumnsUseCase: ListColumnsUseCase,
    private val getColumnUseCase: GetColumnUseCase,
    private val listCardPreviewsUseCase: ListCardPreviewsUseCase,
    private val addCardUseCase: AddCardUseCase,
    private val assignCardUseCase: AssignCardUseCase,
    private val unassignCardUseCase: UnassignCardUseCase,
    private val addColumnUseCase: AddColumnUseCase,
    private val moveCardUseCase: MoveCardUseCase,
    private val listLabelsUseCase: ListLabelsUseCase,
    private val getCurrentAccountUseCase: GetCurrentAccountUseCase,
    private val setCurrentBoardUseCase: SetCurrentBoardUseCase,
    private val syncScheduler: SyncScheduler
) : ViewModel() {

    private val _columns = MutableStateFlow<List<Column>>(emptyList())
    val columns = _columns.asStateFlow()

    private val _cardsByColumn = MutableStateFlow<Map<Long, List<PreviewCard>>>(emptyMap())
    val cardsByColumn = _cardsByColumn.asStateFlow()

    private val _labels = MutableStateFlow<Map<Long, Label>>(emptyMap())
    val labels = _labels.asStateFlow()

    private val _currentAccountId = MutableStateFlow<Account.ID?>(null)
    val currentAccountId = _currentAccountId.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _syncStatus = MutableStateFlow<SyncStatus?>(null)
    val syncStatus = _syncStatus.asStateFlow()

    var draggingCardId by mutableStateOf<Card.ID?>(null)
    var dropTargetColumnId by mutableStateOf<Column.ID?>(null)
    var dropTargetIndex by mutableStateOf(-1)

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    private var currentBoardId: Long? = null

    fun loadBoard(boardId: Long) {
        currentBoardId = boardId
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                val accountId = withContext(Dispatchers.IO) {
                    getCurrentAccountUseCase.execute().await()
                }
                _currentAccountId.value = accountId
                withContext(Dispatchers.IO) {
                    setCurrentBoardUseCase.execute(accountId, Board.ID(boardId))
                }

                launch(Dispatchers.IO) {
                    FlowAdapters.toPublisher(listLabelsUseCase.execute(Board.ID(boardId)))
                        .asFlow()
                        .collectLatest { labels ->
                            _labels.value = labels.associateBy { it.id().value() }
                        }
                }

                launch(Dispatchers.IO) {
                    FlowAdapters.toPublisher(listColumnsUseCase.execute(Board.ID(boardId)))
                        .asFlow()
                        .collect { colIds ->
                            val loadedCols = colIds.map { id ->
                                FlowAdapters.toPublisher(getColumnUseCase.execute(id)).asFlow().first()
                            }
                            withContext(Dispatchers.Main) {
                                _columns.value = loadedCols
                                isLoading = false
                            }
                            loadedCols.forEach { col ->
                                observeCards(col.id.value())
                            }
                        }
                }
            } catch (e: Exception) {
                error = e.message ?: "Failed to load board"
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
                currentBoardId?.let { loadBoard(it) }
            }
        }
    }

    private fun observeCards(columnId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            FlowAdapters.toPublisher(listCardPreviewsUseCase.execute(Column.ID(columnId)))
                .asFlow()
                .collect { cards ->
                    _cardsByColumn.value = _cardsByColumn.value + (columnId to cards)
                }
        }
    }

    fun addCard(columnId: Long, title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                addCardUseCase.execute(CreateCard(Column.ID(columnId), title)).await()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = e.message ?: "Failed to add card"
                }
            }
        }
    }

    fun addColumn(boardId: Long, title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                addColumnUseCase.execute(CreateColumn(Board.ID(boardId), title, 0)).await()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = e.message ?: "Failed to add column"
                }
            }
        }
    }

    fun moveCard(cardId: Card.ID, targetColumnId: Column.ID, targetOrder: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                moveCardUseCase.execute(cardId, targetColumnId, targetOrder).await()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = e.message ?: "Failed to move card"
                }
            }
        }
    }

    fun toggleAssignment(cardId: Card.ID, assignedToMe: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accountId = getCurrentAccountUseCase.execute().await()
                // Assuming username matches User.ID value for the mock
                val userId = User.ID("jdoe") 
                if (assignedToMe) {
                    unassignCardUseCase.execute(cardId, userId).await()
                } else {
                    assignCardUseCase.execute(cardId, userId).await()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = e.message ?: "Failed to update assignment"
                }
            }
        }
    }
}
