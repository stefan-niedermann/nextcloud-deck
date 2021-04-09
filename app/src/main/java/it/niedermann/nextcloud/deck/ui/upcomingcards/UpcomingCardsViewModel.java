package it.niedermann.nextcloud.deck.ui.upcomingcards;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.enums.ESortCriteria;
import it.niedermann.nextcloud.deck.model.widget.filter.EWidgetType;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetAccount;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetSort;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static androidx.lifecycle.Transformations.map;
import static androidx.lifecycle.Transformations.switchMap;

@SuppressWarnings("WeakerAccess")
public class UpcomingCardsViewModel extends AndroidViewModel {

    private final SyncManager syncManager;

    public UpcomingCardsViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public LiveData<List<UpcomingCardsAdapterItem>> getUpcomingCards() {
        return switchMap(syncManager.readAccounts(), (accounts) -> {
            final FilterWidget config = new FilterWidget();
            config.setWidgetType(EWidgetType.UPCOMING_WIDGET);
            config.setSorts(new FilterWidgetSort(ESortCriteria.DUE_DATE, true));
            config.setAccounts(accounts.stream().map(account -> {
                final FilterWidgetAccount fwa = new FilterWidgetAccount(account.getId(), false);
                // TODO syncManager.getUserByUidDirectly(account.getId(), account.getUserName()).getLocalId())
//                fwa.setUsers(new FilterWidgetUser(new User(1L, "stefan"));
                return fwa;
            }).collect(Collectors.toList()));
            return map(this.syncManager.getCardsForFilterWidget(new FilterWidget()),
                    (filterWidgetCards) -> {
                        return filterWidgetCards.stream().map((filterWidgetCard -> {
                            final Board board = filterWidgetCard.getBoard();
                            return new UpcomingCardsAdapterItem(filterWidgetCard.getCard(), new Account(), board.getId(), board.getLocalId(), board.isPermissionEdit());
                        })).collect(Collectors.toList());
                    });
        });
    }
}
