package it.niedermann.nextcloud.deck.ui.boards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.niedermann.nextcloud.deck.domain.model.Board
import it.niedermann.nextcloud.deck.domain.model.CreateBoard
import it.niedermann.nextcloud.deck.domain.usecases.boards.AddBoardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import org.reactivestreams.FlowAdapters
import javax.inject.Inject

@HiltViewModel
class BoardListViewModel @Inject constructor(
    private val getCurrentAccountUseCase: GetCurrentAccountUseCase,
    private val listBoardsUseCase: ListBoardsUseCase,
    private val addBoardUseCase: AddBoardUseCase
) : ViewModel() {

    private val _boards = MutableStateFlow<List<Board>>(emptyList())
    val boards = _boards.asStateFlow()

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
                val accountId = try {
                    getCurrentAccountUseCase.execute().await()
                } catch (e: Exception) {
                    null
                }
                
                if (accountId != null) {
                    FlowAdapters.toPublisher(listBoardsUseCase.execute(accountId))
                        .asFlow()
                        .collect {
                            _boards.value = it
                            isLoading = false
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

    fun addBoard(title: String) {
        viewModelScope.launch {
            try {
                val accountId = getCurrentAccountUseCase.execute().await()
                addBoardUseCase.addBoard(CreateBoard(accountId, title)).await()
                loadBoards() // Manual refresh for mock repository
            } catch (e: Exception) {
                error = e.message ?: "Failed to add board"
            }
        }
    }
}
