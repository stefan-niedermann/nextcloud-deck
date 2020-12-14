package it.niedermann.nextcloud.deck.model.widget.filter.dto;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;

public class FilterWidgetCard {
    private FullCard card;
    private Stack stack;
    private Board board;

    public FilterWidgetCard() {
        // Default constructor
    }

    public FilterWidgetCard(FullCard card, Stack stack, Board board) {
        this.card = card;
        this.stack = stack;
        this.board = board;
    }

    public FullCard getCard() {
        return card;
    }

    public void setCard(FullCard card) {
        this.card = card;
    }

    public Stack getStack() {
        return stack;
    }

    public void setStack(Stack stack) {
        this.stack = stack;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
