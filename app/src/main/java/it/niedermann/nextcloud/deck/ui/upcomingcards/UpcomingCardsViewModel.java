package it.niedermann.nextcloud.deck.ui.upcomingcards;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

@SuppressWarnings("WeakerAccess")
public class UpcomingCardsViewModel extends AndroidViewModel {

    private final SyncManager syncManager;

    public UpcomingCardsViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public LiveData<List<UpcomingCardsAdapterItem>> getUpcomingCards() {
        return  this.syncManager.getCardsForFilterWidget(new FilterWidget());
//        final MutableLiveData<List<UpcomingCardsAdapterItem>> ret = new MutableLiveData<>();
//        new Thread(() -> {
//            List<Account> accounts = syncManager.readAccountsDirectly();
//
//            final FilterWidget config = new FilterWidget();
//            config.setWidgetType(EWidgetType.UPCOMING_WIDGET);
//            config.setSorts(new FilterWidgetSort(ESortCriteria.DUE_DATE, true));
//            config.setAccounts(accounts.stream().map(account -> {
//                final FilterWidgetAccount fwa = new FilterWidgetAccount(account.getId(), false);
//                fwa.setUsers(new FilterWidgetUser(syncManager.getUserByUidDirectly(account.getId(), account.getUserName()).getLocalId()));
//                return fwa;
//            }).collect(Collectors.toList()));
//            List<FilterWidgetCard> filterWidgetCards = this.syncManager.getCardsForFilterWidget(new FilterWidget());
//            ret.postValue(filterWidgetCards.stream().map((filterWidgetCard -> {
//                final Board board = syncManager.getBoardById(
//                        syncManager.getBoardLocalIdByLocalCardIdDirectly(filterWidgetCard.getCard().getLocalId())
//                );
//                return new UpcomingCardsAdapterItem(filterWidgetCard.getCard(), new Account(), board.getId(), board.getLocalId(), board.isPermissionEdit());
//            })).collect(Collectors.toList()));
//        }).start();
//        return ret;
    }
}
