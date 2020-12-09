package it.niedermann.nextcloud.deck.ui.pickstack;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

@SuppressWarnings("WeakerAccess")
public class PickStackViewModel extends AndroidViewModel {

    private final SyncManager syncManager;

    public PickStackViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public LiveData<Boolean> hasAccounts() {
        return syncManager.hasAccounts();
    }

    public LiveData<List<Account>> readAccounts() {
        return syncManager.readAccounts();
    }

    public LiveData<List<Board>> getBoards(long accountId) {
        return syncManager.getBoards(accountId);
    }

    public LiveData<List<Board>> getBoardsWithEditPermission(long accountId) {
        return syncManager.getBoardsWithEditPermission(accountId);
    }

    public LiveData<List<Stack>> getStacksForBoard(long accountId, long localBoardId) {
        return syncManager.getStacksForBoard(accountId, localBoardId);
    }
}
