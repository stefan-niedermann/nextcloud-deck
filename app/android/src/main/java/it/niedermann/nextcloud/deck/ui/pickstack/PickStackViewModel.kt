package it.niedermann.nextcloud.deck.ui.pickstack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.niedermann.nextcloud.deck.domain.model.Account
import it.niedermann.nextcloud.deck.domain.model.Board
import it.niedermann.nextcloud.deck.domain.model.Card
import it.niedermann.nextcloud.deck.domain.model.Column
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.CopyCardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.MoveCardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnsUseCase
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
class PickStackViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val listBoardsUseCase: ListBoardsUseCase,
    private val listColumnsUseCase: ListColumnsUseCase,
    private val moveCardUseCase: MoveCardUseCase,
    private val copyCardUseCase: CopyCardUseCase
) : ViewModel() {

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts = _accounts.asStateFlow()

    private val _boards = MutableStateFlow<List<Board>>(emptyList())
    val boards = _boards.asStateFlow()

    private val _columns = MutableStateFlow<List<Column>>(emptyList())
    val columns = _columns.asStateFlow()

    private val _selectedAccount = MutableStateFlow<Account?>(null)
    val selectedAccount = _selectedAccount.asStateFlow()

    private val _selectedBoard = MutableStateFlow<Board?>(null)
    val selectedBoard = _selectedBoard.asStateFlow()

    private val _selectedColumn = MutableStateFlow<Column?>(null)
    val selectedColumn = _selectedColumn.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            FlowAdapters.toPublisher(getAccountsUseCase.execute())
                .asFlow()
                .collect {
                    _accounts.value = it.toList()
                    if (it.isNotEmpty() && _selectedAccount.value == null) {
                        selectAccount(it.first())
                    }
                }
        }
    }

    fun selectAccount(account: Account) {
        _selectedAccount.value = account
        _selectedBoard.value = null
        _selectedColumn.value = null
        viewModelScope.launch(Dispatchers.IO) {
            FlowAdapters.toPublisher(listBoardsUseCase.execute(account.id))
                .asFlow()
                .collect {
                    _boards.value = it.toList()
                    if (it.isNotEmpty() && _selectedBoard.value == null) {
                        selectBoard(it.first())
                    }
                }
        }
    }

    fun selectBoard(board: Board) {
        _selectedBoard.value = board
        _selectedColumn.value = null
        viewModelScope.launch(Dispatchers.IO) {
            FlowAdapters.toPublisher(listColumnsUseCase.execute(board.id()))
                .asFlow()
                .collect {
                    _columns.value = it.toList()
                }
        }
    }

    fun selectColumn(column: Column) {
        _selectedColumn.value = column
    }

    fun performAction(cardId: Card.ID, mode: Mode, onDone: () -> Unit) {
        val columnId = _selectedColumn.value?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (mode == Mode.MOVE) {
                    moveCardUseCase.execute(cardId, columnId, 0).await()
                } else {
                    copyCardUseCase.execute(cardId, columnId, 0).await()
                }
                withContext(Dispatchers.Main) {
                    onDone()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    enum class Mode {
        MOVE, COPY
    }
}
