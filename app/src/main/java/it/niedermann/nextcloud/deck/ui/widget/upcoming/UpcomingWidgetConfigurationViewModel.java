package it.niedermann.nextcloud.deck.ui.widget.upcoming;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.enums.ESortCriteria;
import it.niedermann.nextcloud.deck.model.widget.filter.EWidgetType;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetAccount;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetSort;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetUser;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

public class UpcomingWidgetConfigurationViewModel extends BaseViewModel {

    @NonNull
    private final MutableLiveData<FilterWidget> config$ = new MutableLiveData<>();

    public UpcomingWidgetConfigurationViewModel(@NonNull Application application) {
        super(application);
    }

    @NonNull
    public LiveData<FilterWidget> getConfig() {
        return config$;
    }

    /**
     * Loads the existing configuration for {@param appWidgetId} (the "Edit widget" path), or
     * builds the same default configuration {@link UpcomingWidget#onUpdate} would create for a
     * freshly placed widget (all accounts, sorted by due date) when none exists yet.
     */
    public void loadConfig(int appWidgetId) {
        executor.submit(() -> {
            final FilterWidget config;
            try {
                config = baseRepository.getFilterWidgetByIdDirectly(appWidgetId);
            } catch (NoSuchElementException e) {
                config$.postValue(buildDefaultConfig(appWidgetId));
                return;
            }
            config$.postValue(config);
        });
    }

    @NonNull
    private FilterWidget buildDefaultConfig(int appWidgetId) {
        final FilterWidget config = new FilterWidget(appWidgetId, EWidgetType.UPCOMING_WIDGET);
        config.setSorts(new FilterWidgetSort(ESortCriteria.DUE_DATE, true));
        final List<Account> accountsList = baseRepository.readAccountsDirectly();
        config.setAccounts(accountsList.stream().map(account -> {
            final FilterWidgetAccount fwa = new FilterWidgetAccount(account.getId(), false);
            fwa.setUsers(new FilterWidgetUser(baseRepository.getUserByUidDirectly(account.getId(), account.getUserName()).getLocalId()));
            return fwa;
        }).collect(Collectors.toList()));
        return config;
    }

    public interface SaveCallback {
        void onSaved();
    }

    public void saveConfig(@NonNull FilterWidget config, @NonNull SaveCallback callback) {
        executor.submit(() -> {
            if (baseRepository.filterWidgetExists(config.getId())) {
                baseRepository.updateFilterWidgetDirectly(config);
            } else {
                baseRepository.createFilterWidgetDirectly(config);
            }
            callback.onSaved();
        });
    }
}
