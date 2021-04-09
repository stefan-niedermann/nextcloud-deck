package it.niedermann.nextcloud.deck.ui.upcomingcards;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static androidx.lifecycle.Transformations.map;

@SuppressWarnings("WeakerAccess")
public class UpcomingCardsViewModel extends AndroidViewModel {

    private final SyncManager syncManager;

    public UpcomingCardsViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public LiveData<List<UpcomingCardsAdapterItem>> getUpcomingCards() {
        // FIXME remove mapping after https://github.com/stefan-niedermann/nextcloud-deck/issues/923
        return map(this.syncManager.getCardsForFilterWidget(new FilterWidget()), (cards) ->
                cards.stream().filter(card -> card.getAccount() != null).collect(Collectors.toList())
        );
    }
}
