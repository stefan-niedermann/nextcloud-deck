package it.niedermann.nextcloud.deck.ui.filter;

import it.niedermann.nextcloud.deck.model.internal.FilterInformation;

public interface FilterChangeListener {
    void onFilterChanged(FilterInformation filterInformation);
}
