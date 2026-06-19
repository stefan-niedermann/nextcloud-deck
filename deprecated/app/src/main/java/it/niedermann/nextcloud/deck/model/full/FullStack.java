package it.niedermann.nextcloud.deck.model.full;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;

public class FullStack implements IRemoteEntity {
    @Embedded
    public Stack stack;

    @Relation(entity = Card.class, parentColumn = "localId", entityColumn = "stackId")
    public List<Card> cards;


    public Stack getStack() {
        return stack;
    }

    public void setStack(Stack stack) {
        this.stack = stack;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    @Ignore
    @Override
    public Stack getEntity() {
        return stack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullStack fullStack = (FullStack) o;

        if (!Objects.equals(stack, fullStack.stack)) return false;
        return Objects.equals(cards, fullStack.cards);
    }

    @Override
    public int hashCode() {
        int result = stack != null ? stack.hashCode() : 0;
        result = 31 * result + (cards != null ? cards.hashCode() : 0);
        return result;
    }
}
