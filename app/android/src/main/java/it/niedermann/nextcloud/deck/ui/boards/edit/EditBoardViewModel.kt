package it.niedermann.nextcloud.deck.ui.boards.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.niedermann.nextcloud.deck.domain.model.Board
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.boards.UpdateBoardUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import org.reactivestreams.FlowAdapters

@HiltViewModel
class EditBoardViewModel @Inject constructor(
    private val getBoardUseCase: GetBoardUseCase,
    private val updateBoardUseCase: UpdateBoardUseCase
) : ViewModel() {

    private val _board = MutableStateFlow<Board?>(null)
    val board = _board.asStateFlow()

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun loadBoard(boardId: Long) {
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                FlowAdapters.toPublisher(getBoardUseCase.execute(Board.ID(boardId)))
                    .asFlow()
                    .collect {
                        _board.value = it
                        isLoading = false
                    }
            } catch (e: Exception) {
                error = e.message ?: "Failed to load board"
                isLoading = false
            }
        }
    }

    fun updateBoard(board: Board) {
        viewModelScope.launch {
            try {
                updateBoardUseCase.execute(board).await()
            } catch (e: Exception) {
                error = e.message ?: "Failed to update board"
            }
        }
    }
}
