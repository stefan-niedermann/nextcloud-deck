package it.niedermann.nextcloud.deck.model.full;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;

public class FullStack implements IRemoteEntity {
    @Embedded
    public Stack stack;

    @Relation(entity =  Card.class, parentColumn = "localId", entityColumn = "stackId")
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
    public IRemoteEntity getEntity() {
        return stack;
    }
}
