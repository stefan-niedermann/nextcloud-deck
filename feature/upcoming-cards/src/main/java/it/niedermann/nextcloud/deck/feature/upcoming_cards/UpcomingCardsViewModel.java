package it.niedermann.nextcloud.deck.feature.upcoming_cards;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.SavedStateHandle;

public class UpcomingCardsViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;

    public UpcomingCardsViewModel(@NonNull Application application, @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
    }
}
