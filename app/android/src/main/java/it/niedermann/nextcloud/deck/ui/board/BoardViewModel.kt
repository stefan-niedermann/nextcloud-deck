package it.niedermann.nextcloud.deck.ui.board

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.niedermann.nextcloud.deck.domain.model.Board
import it.niedermann.nextcloud.deck.domain.model.Card
import it.niedermann.nextcloud.deck.domain.model.Column
import it.niedermann.nextcloud.deck.domain.model.CreateCard
import it.niedermann.nextcloud.deck.domain.model.CreateColumn
import it.niedermann.nextcloud.deck.domain.usecases.cards.AddCardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.ListCardsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.columns.AddColumnUseCase
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import org.reactivestreams.FlowAdapters
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val listColumnsUseCase: ListColumnsUseCase,
    private val getColumnUseCase: GetColumnUseCase,
    private val listCardsUseCase: ListCardsUseCase,
    private val addCardUseCase: AddCardUseCase,
    private val addColumnUseCase: AddColumnUseCase
) : ViewModel() {

    private val _columns = MutableStateFlow<List<Column>>(emptyList())
    val columns = _columns.asStateFlow()

    private val _cardsByColumn = MutableStateFlow<Map<Long, List<Card>>>(emptyMap())
    val cardsByColumn = _cardsByColumn.asStateFlow()

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun loadBoard(boardId: Long) {
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                FlowAdapters.toPublisher(listColumnsUseCase.execute(Board.ID(boardId)))
                    .asFlow()
                    .collect { colIds ->
                        val loadedCols = colIds.map { id ->
                            FlowAdapters.toPublisher(getColumnUseCase.execute(id)).asFlow().first()
                        }
                        _columns.value = loadedCols
                        loadedCols.forEach { col ->
                            observeCards(col.id.value())
                        }
                        isLoading = false
                    }
            } catch (e: Exception) {
                error = e.message ?: "Failed to load board"
                isLoading = false
            }
        }
    }

    private fun observeCards(columnId: Long) {
        viewModelScope.launch {
            FlowAdapters.toPublisher(listCardsUseCase.execute(Column.ID(columnId)))
                .asFlow()
                .collect { cards ->
                    _cardsByColumn.value = _cardsByColumn.value + (columnId to cards)
                }
        }
    }

    fun addCard(columnId: Long, title: String) {
        viewModelScope.launch {
            try {
                addCardUseCase.execute(CreateCard(Column.ID(columnId), title)).await()
                // In a real app, the flow would emit the new card. 
                // For mock, we might need to refresh manually.
            } catch (e: Exception) {
                error = e.message ?: "Failed to add card"
            }
        }
    }

    fun addColumn(boardId: Long, title: String) {
        viewModelScope.launch {
            try {
                addColumnUseCase.execute(CreateColumn(Board.ID(boardId), title, 0)).await()
            } catch (e: Exception) {
                error = e.message ?: "Failed to add column"
            }
        }
    }
}
