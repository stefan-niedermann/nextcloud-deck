package it.niedermann.nextcloud.deck.model;

import java.io.Serializable;

public class Board implements Serializable {
    long id;
    String title;

    public Board(long id, String title) {
        this.id = id;
        this.title = title;
    }
}
