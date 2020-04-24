package it.niedermann.nextcloud.deck.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;

public class MainViewModel extends ViewModel {

    @NonNull
    private MutableLiveData<FilterInformation> filterInformation = new MutableLiveData<>();
    private Account currentAccount;
    private FullBoard currentBoard;

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

    public FullBoard getCurrentBoard() {
        return currentBoard;
    }

    public void setCurrentBoard(FullBoard currentBoard) {
        this.currentBoard = currentBoard;
    }
}
