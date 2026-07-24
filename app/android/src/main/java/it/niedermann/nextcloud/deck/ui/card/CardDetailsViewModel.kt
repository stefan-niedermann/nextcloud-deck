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
import it.niedermann.nextcloud.deck.domain.usecases.activities.ListActivityUseCase
import it.niedermann.nextcloud.deck.domain.usecases.attachments.ListAttachmentsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.GetCardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.UpdateCardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.comments.ListCommentsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import org.reactivestreams.FlowAdapters
import javax.inject.Inject

@HiltViewModel
class CardDetailsViewModel @Inject constructor(
    private val getCardUseCase: GetCardUseCase,
    private val listAttachmentsUseCase: ListAttachmentsUseCase,
    private val listCommentsUseCase: ListCommentsUseCase,
    private val listActivityUseCase: ListActivityUseCase,
    private val updateCardUseCase: UpdateCardUseCase
) : ViewModel() {

    private val _card = MutableStateFlow<Card?>(null)
    val card = _card.asStateFlow()

    private val _attachments = MutableStateFlow<List<Attachment>>(emptyList())
    val attachments = _attachments.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments = _comments.asStateFlow()

    private val _activities = MutableStateFlow<List<Activity>>(emptyList())
    val activities = _activities.asStateFlow()

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun loadCard(cardId: Long) {
        isLoading = true
        error = null
        viewModelScope.launch {
            try {
                val id = Card.ID(cardId)
                
                // Fetch Card
                FlowAdapters.toPublisher(getCardUseCase.execute(id))
                    .asFlow()
                    .collect { _card.value = it }

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
}
