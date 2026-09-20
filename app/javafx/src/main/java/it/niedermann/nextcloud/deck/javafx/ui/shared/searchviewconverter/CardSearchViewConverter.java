package it.niedermann.nextcloud.deck.javafx.ui.shared.searchviewconverter;

import it.niedermann.nextcloud.deck.domain.model.Card;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.util.StringConverter;

@Singleton
public class CardSearchViewConverter extends StringConverter<Card> {

    @Inject
    public CardSearchViewConverter() {
    }

    @Override
    public String toString(Card card) {
        if (card == null) {
            return "";
        }
        if (card.remoteId() == null) {
            return card.title();
        }
        return "#" + card.remoteId().value() + " " + card.title();
    }

    @Override
    public Card fromString(String string) {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
