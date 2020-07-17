package it.niedermann.nextcloud.deck.model.full;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.List;

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

        if (stack != null ? !stack.equals(fullStack.stack) : fullStack.stack != null) return false;
        return cards != null ? cards.equals(fullStack.cards) : fullStack.cards == null;
    }

    @Override
    public int hashCode() {
        int result = stack != null ? stack.hashCode() : 0;
        result = 31 * result + (cards != null ? cards.hashCode() : 0);
        return result;
    }
}
