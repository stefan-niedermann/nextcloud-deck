package it.niedermann.nextcloud.deck.ui.filter;

import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;

public interface SelectionListener<T extends IRemoteEntity> {
    void onItemSelected(T item);

    void onItemDeselected(T item);
}
