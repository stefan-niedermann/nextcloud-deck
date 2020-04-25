package it.niedermann.nextcloud.deck.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;

@SuppressWarnings("WeakerAccess")
public class MainViewModel extends ViewModel {

    @NonNull
    private MutableLiveData<FilterInformation> filterInformation = new MutableLiveData<>();
    private Account currentAccount;
    private Board currentBoard;

    public void postFilterInformation(@Nullable FilterInformation filterInformation) {
        this.filterInformation.postValue(filterInformation);
    }

    @NonNull
    public LiveData<FilterInformation> getFilterInformation() {
        return this.filterInformation;
    }

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
}
