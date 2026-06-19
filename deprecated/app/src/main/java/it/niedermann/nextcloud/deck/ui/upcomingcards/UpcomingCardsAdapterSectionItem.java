package it.niedermann.nextcloud.deck.ui.upcomingcards;

import androidx.annotation.NonNull;

public class UpcomingCardsAdapterSectionItem {

    @NonNull
    private final String title;

    public UpcomingCardsAdapterSectionItem(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getTitle() {
        return title;
    }
}