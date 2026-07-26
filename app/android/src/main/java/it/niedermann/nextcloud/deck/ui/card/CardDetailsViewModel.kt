package it.niedermann.nextcloud.deck.ui.card

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.niedermann.nextcloud.deck.domain.model.Activity
import it.niedermann.nextcloud.deck.domain.model.Attachment
import it.niedermann.nextcloud.deck.domain.model.Card
import it.niedermann.nextcloud.deck.domain.model.Comment
import it.niedermann.nextcloud.deck.domain.model.CreateComment
import it.niedermann.nextcloud.deck.domain.model.Label
import it.niedermann.nextcloud.deck.domain.model.User
import it.niedermann.nextcloud.deck.domain.usecases.activities.ListActivityUseCase
import it.niedermann.nextcloud.deck.domain.usecases.attachments.AddAttachmentUseCase
import it.niedermann.nextcloud.deck.domain.usecases.attachments.ListAttachmentsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.GetCardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.UpdateCardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase
import it.niedermann.nextcloud.deck.domain.usecases.comments.AddCommentUseCase
import it.niedermann.nextcloud.deck.domain.usecases.comments.DeleteCommentUseCase
import it.niedermann.nextcloud.deck.domain.usecases.comments.ListCommentsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.comments.UpdateCommentUseCase
import it.niedermann.nextcloud.deck.domain.usecases.labels.ListLabelsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.users.SearchUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext
import org.reactivestreams.FlowAdapters
import java.time.LocalDateTime
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CardDetailsViewModel @Inject constructor(
    private val getCardUseCase: GetCardUseCase,
    private val listAttachmentsUseCase: ListAttachmentsUseCase,
    private val addAttachmentUseCase: AddAttachmentUseCase,
    private val listCommentsUseCase: ListCommentsUseCase,
    private val listActivityUseCase: ListActivityUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val listLabelsUseCase: ListLabelsUseCase,
    private val getColumnUseCase: GetColumnUseCase,
    private val searchUserUseCase: SearchUserUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val updateCommentUseCase: UpdateCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase
) : ViewModel() {

    private val _card = MutableStateFlow<Card?>(null)
    val card = _card.asStateFlow()

    private val _attachments = MutableStateFlow<List<Attachment>>(emptyList())
    val attachments = _attachments.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments = _comments.asStateFlow()

    private val _activities = MutableStateFlow<List<Activity>>(emptyList())
    val activities = _activities.asStateFlow()

    private val _boardLabels = MutableStateFlow<List<Label>>(emptyList())
    val boardLabels = _boardLabels.asStateFlow()

    private val _commentMessage = MutableStateFlow("")
    val commentMessage = _commentMessage.asStateFlow()

    private val _respondingToComment = MutableStateFlow<Comment?>(null)
    val respondingToComment = _respondingToComment.asStateFlow()

    private val _editingComment = MutableStateFlow<Comment?>(null)
    val editingComment = _editingComment.asStateFlow()

    private val _userSearchQuery = MutableStateFlow("")
    val userSearchResults = _userSearchQuery
        .debounce(300L)
        .flatMapLatest { query ->
            if (query.length < 2) flowOf(emptyList())
            else FlowAdapters.toPublisher(searchUserUseCase.execute(query)).asFlow()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun onUserSearchQueryChange(query: String) {
        _userSearchQuery.value = query
    }

    fun onCommentMessageChange(message: String) {
        _commentMessage.value = message
    }

    fun respondToComment(comment: Comment?) {
        _respondingToComment.value = comment
        _editingComment.value = null
    }

    fun editComment(comment: Comment?) {
        _editingComment.value = comment
        _respondingToComment.value = null
        _commentMessage.value = comment?.message() ?: ""
    }

    fun cancelCommentAction() {
        _editingComment.value = null
        _respondingToComment.value = null
        _commentMessage.value = ""
    }

    fun submitComment() {
        val currentCard = _card.value ?: return
        val message = _commentMessage.value
        if (message.isBlank()) return

        viewModelScope.launch {
            try {
                val editing = _editingComment.value
                if (editing != null) {
                    withContext(Dispatchers.IO) {
                        updateCommentUseCase.execute(editing.id(), message).get()
                    }
                } else {
                    val parentId = _respondingToComment.value?.id()
                    val createComment = CreateComment(currentCard.id(), message, parentId)
                    withContext(Dispatchers.IO) {
                        addCommentUseCase.execute(createComment).get()
                    }
                }
                cancelCommentAction()
                refreshComments(currentCard.id())
            } catch (e: Exception) {
                error = "Failed to submit comment: ${e.message}"
            }
        }
    }

    fun deleteComment(commentId: Comment.ID) {
        val currentCard = _card.value ?: return
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    deleteCommentUseCase.execute(commentId).get()
                }
                refreshComments(currentCard.id())
            } catch (e: Exception) {
                error = "Failed to delete comment: ${e.message}"
            }
        }
    }

    private fun refreshComments(cardId: Card.ID) {
        viewModelScope.launch {
            FlowAdapters.toPublisher(listCommentsUseCase.execute(cardId))
                .asFlow()
                .collect { _comments.value = it }
        }
    }

    fun loadCard(cardId: Long) {
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                val id = Card.ID(cardId)
                
                // Fetch Card
                FlowAdapters.toPublisher(getCardUseCase.execute(id))
                    .asFlow()
                    .collect { card ->
                        _card.value = card
                        if (card != null) {
                            // Fetch Board Labels
                            FlowAdapters.toPublisher(getColumnUseCase.execute(card.columnId()))
                                .asFlow()
                                .collect { column ->
                                    FlowAdapters.toPublisher(listLabelsUseCase.execute(column.boardId()))
                                        .asFlow()
                                        .collect { _boardLabels.value = it.toList() }
                                }
                        }
                    }

                // Fetch Attachments
                FlowAdapters.toPublisher(listAttachmentsUseCase.execute(id))
                    .asFlow()
                    .collect { _attachments.value = it.toList() }

                // Fetch Comments
                FlowAdapters.toPublisher(listCommentsUseCase.execute(id))
                    .asFlow()
                    .collect { _comments.value = it }

                // Fetch Activity
                FlowAdapters.toPublisher(listActivityUseCase.execute(id))
                    .asFlow()
                    .collect { _activities.value = it }

                isLoading = false
            } catch (e: Exception) {
                error = e.message ?: "Failed to load card details"
                isLoading = false
            }
        }
    }

    fun updateCardDescription(description: String) {
        val currentCard = _card.value ?: return
        if (currentCard.description() == description) return
        updateCard(currentCard.withDescription(description))
    }

    fun updateCardDates(startDate: LocalDateTime?, dueDate: LocalDateTime?) {
        val currentCard = _card.value ?: return
        updateCard(currentCard.withStartDate(startDate).withDueDate(dueDate))
    }

    fun toggleLabel(labelId: Label.ID) {
        val currentCard = _card.value ?: return
        val labels = currentCard.labels().toMutableSet()
        if (labels.contains(labelId)) {
            labels.remove(labelId)
        } else {
            labels.add(labelId)
        }
        updateCard(currentCard.withLabels(labels))
    }

    fun toggleAssignee(userId: User.ID) {
        val currentCard = _card.value ?: return
        val assignees = currentCard.assignees().toMutableSet()
        if (assignees.contains(userId)) {
            assignees.remove(userId)
        } else {
            assignees.add(userId)
        }
        updateCard(currentCard.withAssignees(assignees))
    }

    fun addAttachment(uri: android.net.Uri) {
        val currentCard = _card.value ?: return
        viewModelScope.launch {
            try {
                // In a real app, we would copy the URI content to a local file/cache
                // and then call the use case with that Path.
                // For now, we simulate success as the UseCase is a mock.
                withContext(Dispatchers.IO) {
                    // addAttachmentUseCase.execute(currentCard.id(), path).get()
                }
                // Refresh attachments
                FlowAdapters.toPublisher(listAttachmentsUseCase.execute(currentCard.id()))
                    .asFlow()
                    .collect { _attachments.value = it.toList() }
            } catch (e: Exception) {
                error = "Failed to add attachment: ${e.message}"
            }
        }
    }

    private fun updateCard(card: Card) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    updateCardUseCase.execute(card).get()
                }
                _card.value = card
            } catch (e: Exception) {
                error = "Failed to update card: ${e.message}"
            }
        }
    }
}
