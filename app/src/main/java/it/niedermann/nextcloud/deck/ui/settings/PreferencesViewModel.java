package it.niedermann.nextcloud.deck.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.repository.PreferencesRepository;
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

    public void setAppTheme(int setting) {
        preferencesRepository.setAppTheme(setting);
    }

    public LiveData<Boolean> isDebugModeEnabled$() {
        return preferencesRepository.isDebugModeEnabled$();
    }

    public boolean backupDatabase() {
        return baseRepository.backupDatabase();
    }

    public List<User> getUsersForAccountDirectly(long accountId) {
        return baseRepository.getUsersForAccountDirectly(accountId);
    }

    public boolean restoreDatabase() {
        return baseRepository.restoreDatabase();
    }

    public boolean hasBackup() {
        return baseRepository.hasBackup();
    }

    public void setRestoreAccount(long accountId) {
        preferencesRepository.setRestoreAccount(accountId);
    }

    public long getRestoreAccount() {
        return preferencesRepository.getRestoreAccount();
    }

    public void clearRestoreAccount() {
        preferencesRepository.clearRestoreAccount();
    }
}
