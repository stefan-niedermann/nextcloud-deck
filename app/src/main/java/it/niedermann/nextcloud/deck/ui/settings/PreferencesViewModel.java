package it.niedermann.nextcloud.deck.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import it.niedermann.nextcloud.deck.persistence.PreferencesRepository;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

public class PreferencesViewModel extends BaseViewModel {

    private final PreferencesRepository preferencesRepository;

    public PreferencesViewModel(@NonNull Application application) {
        this(application, new PreferencesRepository(application));
    }

    public PreferencesViewModel(@NonNull Application application, @NonNull PreferencesRepository preferencesRepository) {
        super(application);
        this.preferencesRepository = preferencesRepository;
    }

    public LiveData<Long> getCurrentAccountId$() {
        return baseRepository.getCurrentAccountId$();
    }

    public LiveData<Integer> getAccountColor(long accountId) {
        return baseRepository.getAccountColor(accountId);
    }

    public void setAppTheme(int setting) {
        preferencesRepository.setAppTheme(setting);
    }

    public LiveData<Boolean> isDebugModeEnabled$() {
        return preferencesRepository.isDebugModeEnabled$();
    }
}
