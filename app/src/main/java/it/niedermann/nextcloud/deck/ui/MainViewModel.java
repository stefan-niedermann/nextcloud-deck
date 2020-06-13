package it.niedermann.nextcloud.deck.ui;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;

@SuppressWarnings("WeakerAccess")
public class MainViewModel extends ViewModel {

    private MutableLiveData<Account> currentAccount = new MutableLiveData<>();
    private Board currentBoard;
    private boolean currentAccountHasArchivedBoards = false;

    private boolean currentAccountIsSupportedVersion = false;

    public Account getCurrentAccount() {
        return currentAccount.getValue();
    }

    public LiveData<Account> getCurrentAccountLiveData() {
        return this.currentAccount;
    }

    public void setCurrentAccount(Account currentAccount, boolean versionIsSupported) {
        this.currentAccount.setValue(currentAccount);
        this.currentAccountIsSupportedVersion = versionIsSupported;
    }

    public void setCurrentBoard(Board currentBoard) {
        this.currentBoard = currentBoard;
    }

    public Long getCurrentBoardLocalId() {
        return this.currentBoard.getLocalId();
    }

    @Nullable
    public Long getCurrentBoardRemoteId() {
        return this.currentBoard.getId();
    }

    public boolean currentBoardHasEditPermission() {
        return this.currentBoard != null && this.currentBoard.isPermissionEdit() && currentAccountIsSupportedVersion;
    }

    public boolean currentAccountHasArchivedBoards() {
        return currentAccountHasArchivedBoards;
    }

    public void setCurrentAccountHasArchivedBoards(boolean currentAccountHasArchivedBoards) {
        this.currentAccountHasArchivedBoards = currentAccountHasArchivedBoards;
    }

    public boolean isCurrentAccountIsSupportedVersion() {
        return currentAccountIsSupportedVersion;
    }
}
