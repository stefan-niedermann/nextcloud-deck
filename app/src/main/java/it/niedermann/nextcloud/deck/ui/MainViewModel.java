package it.niedermann.nextcloud.deck.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;

import static it.niedermann.nextcloud.deck.model.internal.FilterInformation.hasActiveFilter;

@SuppressWarnings("WeakerAccess")
public class MainViewModel extends ViewModel {

    @NonNull
    private MutableLiveData<FilterInformation> filterInformationDraft = new MutableLiveData<>(new FilterInformation());
    @NonNull
    private MutableLiveData<FilterInformation> filterInformation = new MutableLiveData<>();
    private Account currentAccount;
    private Board currentBoard;

    public void publishFilterInformationDraft() {
        this.filterInformation.postValue(hasActiveFilter(filterInformationDraft.getValue()) ? filterInformationDraft.getValue() : null);
    }

    public void clearFilterInformation() {
        this.filterInformationDraft.postValue(new FilterInformation());
        this.publishFilterInformationDraft();
    }

    @NonNull
    public LiveData<FilterInformation> getFilterInformationDraft() {
        return this.filterInformationDraft;
    }

    public void createFilterInformationDraft() {
        this.filterInformationDraft.postValue(new FilterInformation(this.filterInformation.getValue()));
    }

    @NonNull
    public LiveData<FilterInformation> getFilterInformation() {
        return this.filterInformation;
    }

    public void setFilterInformationDraftDueType(@NonNull EDueType dueType) {
        FilterInformation newDraft = new FilterInformation(filterInformationDraft.getValue());
        newDraft.setDueType(dueType);
        this.filterInformationDraft.postValue(newDraft);
    }

    public void addFilterInformationDraftLabel(@NonNull Label label) {
        FilterInformation newDraft = new FilterInformation(filterInformationDraft.getValue());
        newDraft.addLabel(label);
        this.filterInformationDraft.postValue(newDraft);
    }

    public void addFilterInformationUser(@NonNull User user) {
        FilterInformation newDraft = new FilterInformation(filterInformationDraft.getValue());
        newDraft.addUser(user);
        this.filterInformationDraft.postValue(newDraft);
    }

    public void removeFilterInformationLabel(@NonNull Label label) {
        FilterInformation newDraft = new FilterInformation(filterInformationDraft.getValue());
        newDraft.removeLabel(label);
        this.filterInformationDraft.postValue(newDraft);
    }

    public void removeFilterInformationUser(@NonNull User user) {
        FilterInformation newDraft = new FilterInformation(filterInformationDraft.getValue());
        newDraft.removeUser(user);
        this.filterInformationDraft.postValue(newDraft);
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
