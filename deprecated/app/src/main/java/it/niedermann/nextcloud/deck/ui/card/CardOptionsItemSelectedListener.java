package it.niedermann.nextcloud.deck.ui.card;

import android.view.MenuItem;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.model.full.FullCard;

public interface CardOptionsItemSelectedListener {
    boolean onCardOptionsItemSelected(@NonNull MenuItem menuItem, @NonNull FullCard fullCard);
}