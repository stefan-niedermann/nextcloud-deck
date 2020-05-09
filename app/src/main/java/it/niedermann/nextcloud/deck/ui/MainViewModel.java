package it.niedermann.nextcloud.deck.ui;

import androidx.lifecycle.ViewModel;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;

@SuppressWarnings("WeakerAccess")
public class MainViewModel extends ViewModel {

    private Account currentAccount;
    private Board currentBoard;
    private boolean currentAccountHasArchivedBoards = false;

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }

    public void setCurrentBoard(Board currentBoard) {
        this.currentBoard = currentBoard;
    }

    public Long getCurrentBoardLocalId() {
        return this.currentBoard.getLocalId();
    }

    public boolean currentBoardHasEditPermission() {
        return this.currentBoard != null && this.currentBoard.isPermissionEdit();
    }

    public boolean currentAccountHasArchivedBoards() {
        return currentAccountHasArchivedBoards;
    }

    public void setCurrentAccountHasArchivedBoards(boolean currentAccountHasArchivedBoards) {
        this.currentAccountHasArchivedBoards = currentAccountHasArchivedBoards;
    }
}
