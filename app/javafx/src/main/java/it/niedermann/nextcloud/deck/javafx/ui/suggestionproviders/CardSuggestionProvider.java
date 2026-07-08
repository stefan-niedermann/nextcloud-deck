package it.niedermann.nextcloud.deck.javafx.ui.suggestionproviders;

import com.dlsc.gemsfx.SearchField;

import java.util.Collection;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class CardSuggestionProvider implements javafx.util.Callback<SearchField.SearchFieldSuggestionRequest, Collection<Card>> {

    private final CardRepository cardRepository;

    @Inject
    public CardSuggestionProvider(
            CardRepository cardRepository
    ) {
        this.cardRepository = cardRepository;
    }

    @Override
    public Collection<Card> call(SearchField.SearchFieldSuggestionRequest param) {
        return Maybe.fromPublisher(cardRepository.find(param.getUserText())).blockingGet();
    }
}
