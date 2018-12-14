package it.niedermann.nextcloud.deck.model;

import android.arch.persistence.room.Entity;

import it.niedermann.nextcloud.deck.model.interfaces.RemoteEntity;

@Entity(inheritSuperIndices = true)
public class Label extends RemoteEntity {
    private String title;
    private String color;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
