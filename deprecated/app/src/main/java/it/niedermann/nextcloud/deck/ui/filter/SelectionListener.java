package it.niedermann.nextcloud.deck.ui.filter;

import androidx.annotation.Nullable;

public interface SelectionListener<T> {
    void onItemSelected(@Nullable T item);

    default void onItemDeselected(@Nullable T item) {
        // Deselecting is optional
    }
}
