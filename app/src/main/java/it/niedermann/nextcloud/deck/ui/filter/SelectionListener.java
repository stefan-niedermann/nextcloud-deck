package it.niedermann.nextcloud.deck.ui.filter;

public interface SelectionListener<T> {
    void onItemSelected(T item);

    default void onItemDeselected(T item) {
        // Deselecting is optional
    }
}
