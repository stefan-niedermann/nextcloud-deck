package it.niedermann.nextcloud.deck.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(indices = {@Index(value = "name", unique = true)})
public class Account implements Serializable {
    @Ignore
    private static final long serialVersionUID = 0;

    @PrimaryKey(autoGenerate = true)
    protected Long id;

    @NonNull
    private String name;

    public Account(Long id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }

    public Account() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
