package it.niedermann.nextcloud.deck.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.niedermann.nextcloud.deck.domain.model.Card
import it.niedermann.nextcloud.deck.domain.usecases.cards.SearchCardsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import org.reactivestreams.FlowAdapters
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchCardsUseCase: SearchCardsUseCase
) : ViewModel() {

    private val _results = MutableStateFlow<List<Card>>(emptyList())
    val results = _results.asStateFlow()

    fun search(query: String) {
        if (query.isBlank()) {
            _results.value = emptyList()
            return
        }
        viewModelScope.launch {
            FlowAdapters.toPublisher(searchCardsUseCase.execute(query))
                .asFlow()
                .collect {
                    _results.value = it.toList()
                }
        }
    }
}
