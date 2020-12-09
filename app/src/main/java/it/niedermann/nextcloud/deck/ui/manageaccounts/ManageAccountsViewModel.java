package it.niedermann.nextcloud.deck.ui.manageaccounts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.helper.SingleAccountHelper;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static it.niedermann.nextcloud.deck.DeckApplication.saveCurrentAccountId;

@SuppressWarnings("WeakerAccess")
public class ManageAccountsViewModel extends AndroidViewModel {

    private SyncManager syncManager;

    public ManageAccountsViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public LiveData<Account> readAccount(long id) {
        return syncManager.readAccount(id);
    }

    public LiveData<List<Account>> readAccounts() {
        return syncManager.readAccounts();
    }

    public void setNewAccount(@NonNull Account account) {
        SingleAccountHelper.setCurrentAccount(getApplication(), account.getName());
        syncManager = new SyncManager(getApplication());
        saveCurrentAccountId(getApplication(), account.getId());
    }

    public void deleteAccount(long id) {
        syncManager.deleteAccount(id);
    }
}
