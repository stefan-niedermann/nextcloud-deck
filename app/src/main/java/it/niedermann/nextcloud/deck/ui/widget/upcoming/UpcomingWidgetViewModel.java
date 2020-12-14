package it.niedermann.nextcloud.deck.ui.widget.upcoming;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetAccount;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetUser;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static androidx.lifecycle.Transformations.switchMap;

public class UpcomingWidgetViewModel extends AndroidViewModel {

    @NonNull
    private final SyncManager syncManager;

    public UpcomingWidgetViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public LiveData<Integer> addUpcomingWidget(int appWidgetId) {
        return switchMap(syncManager.readAccounts(), accountsList -> {
            final MutableLiveData<Integer> result$ = new MutableLiveData<>();
            new Thread(() -> {
                final FilterWidget config = new FilterWidget();
                config.setId(appWidgetId);
                config.setAccounts(accountsList.stream().map(account -> {
                    final FilterWidgetAccount fwa = new FilterWidgetAccount();
                    fwa.setAccountId(account.getId());
                    final FilterWidgetUser fwu = new FilterWidgetUser();
                    fwu.setUserId(syncManager.getUserByUidDirectly(account.getId(), account.getUserName()).getId());
                    fwa.setUsers(Collections.singletonList(fwu));
                    return fwa;
                }).collect(Collectors.toList()));
                syncManager.createFilterWidget(config, new IResponseCallback<Integer>(null) {
                    @Override
                    public void onResponse(Integer response) {
                        result$.postValue(response);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                    }
                });
            }).start();
            return result$;
        });
    }
}
