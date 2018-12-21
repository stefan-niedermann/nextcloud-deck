package it.niedermann.nextcloud.deck.model.full;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.JoinStackWithCard;
import it.niedermann.nextcloud.deck.model.Stack;

public class FullStack {
    @Embedded
    public Stack stack;

    @Relation(entity = JoinStackWithCard.class, parentColumn = "localId", entityColumn = "cardId")
    public List<Card> cards;

}
