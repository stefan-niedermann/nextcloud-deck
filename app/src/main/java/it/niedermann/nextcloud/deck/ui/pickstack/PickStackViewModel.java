package it.niedermann.nextcloud.deck.ui.pickstack;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static androidx.lifecycle.Transformations.distinctUntilChanged;

@SuppressWarnings("WeakerAccess")
public class PickStackViewModel extends AndroidViewModel {

    private final SyncManager syncManager;

    private Account selectedAccount;
    @Nullable
    private Board selectedBoard;
    @Nullable
    private Stack selectedStack;
    private boolean contentIsSatisfied = false;
    private boolean saveInProgress = false;

    private final MutableLiveData<Boolean> submitButtonEnabled$ = new MutableLiveData<>(false);

    public PickStackViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public LiveData<Boolean> submitButtonEnabled() {
        return distinctUntilChanged(submitButtonEnabled$);
    }

    public void setContentIsSatisfied(boolean isSatisfied) {
        this.contentIsSatisfied = isSatisfied;
        this.submitButtonEnabled$.setValue(!saveInProgress && contentIsSatisfied && selectedBoard != null && selectedStack != null);
    }

    public void setSelected(@NonNull Account account, @Nullable Board board, @Nullable Stack stack) {
        this.selectedAccount = account;
        this.selectedBoard = board;
        this.selectedStack = stack;
        this.submitButtonEnabled$.setValue(!saveInProgress && contentIsSatisfied && selectedBoard != null && selectedStack != null);
    }

    public void setSubmitInProgress(boolean saveInProgress) {
        this.saveInProgress = saveInProgress;
        this.submitButtonEnabled$.setValue(!saveInProgress && contentIsSatisfied && selectedBoard != null && selectedStack != null);
    }

    public Account getAccount() {
        return this.selectedAccount;
    }

    public long getBoardLocalId() {
        if (this.selectedBoard == null) {
            throw new IllegalStateException("Check submitButtonEnabled() before calling this method.");
        }
        return this.selectedBoard.getLocalId();
    }

    public long getStackLocalId() {
        if (this.selectedStack == null) {
            throw new IllegalStateException("Check submitButtonEnabled() before calling this method.");
        }
        return this.selectedStack.getLocalId();
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
