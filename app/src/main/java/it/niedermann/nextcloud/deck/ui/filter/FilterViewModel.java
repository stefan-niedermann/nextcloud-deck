package it.niedermann.nextcloud.deck.ui.filter;

import android.app.Application;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.EDoneType;
import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

@SuppressWarnings("WeakerAccess")
public class FilterViewModel extends BaseViewModel {

    @IntRange(from = 0, to = 2)
    private int currentFilterTab = 0;

    @NonNull
    private final MutableLiveData<FilterInformation> filterInformationDraft = new MutableLiveData<>(new FilterInformation());
    @NonNull
    private final MutableLiveData<FilterInformation> filterInformation = new MutableLiveData<>();

    public FilterViewModel(@NonNull Application application) {
        super(application);
    }

    public CompletableFuture<Account> getCurrentAccount() {
        return baseRepository.getCurrentAccountId().thenApplyAsync(baseRepository::readAccountDirectly);
    }

    public void publishFilterInformationDraft() {
        this.filterInformation.postValue(FilterInformation.hasActiveFilter(filterInformationDraft.getValue()) ? filterInformationDraft.getValue() : null);
    }

    public void clearFilterInformation(boolean alsoFilterText) {
        final var newFilterInformation = new FilterInformation();
        if (alsoFilterText) {
            final FilterInformation oldFilterInformation = this.filterInformation.getValue();
            newFilterInformation.setFilterText(oldFilterInformation != null ? oldFilterInformation.getFilterText() : "");
        }
        this.filterInformationDraft.setValue(newFilterInformation);
        this.publishFilterInformationDraft();
        this.currentFilterTab = 0;
    }

    @NonNull
    public LiveData<FilterInformation> getFilterInformationDraft() {
        return this.filterInformationDraft;
    }

    @NonNull
    public LiveData<Boolean> hasActiveFilter() {
        return new ReactiveLiveData<>(getFilterInformation())
                .map(FilterInformation::hasActiveFilter)
                .distinctUntilChanged();
    }

    public void createFilterInformationDraft() {
        this.filterInformationDraft.postValue(new FilterInformation(this.filterInformation.getValue()));
    }

    @NonNull
    public LiveData<FilterInformation> getFilterInformation() {
        return this.filterInformation;
    }

    public void setFilterInformationDraftDueType(@NonNull EDueType dueType) {
        final var newDraft = new FilterInformation(filterInformationDraft.getValue());
        newDraft.setDueType(dueType);
        this.filterInformationDraft.postValue(newDraft);
    }

    public void setFilterInformationDraftDoneType(@NonNull EDoneType doneType) {
        final var newDraft = new FilterInformation(filterInformationDraft.getValue());
        newDraft.setDoneType(doneType);
        this.filterInformationDraft.postValue(newDraft);
    }

    public void addFilterInformationDraftLabel(@NonNull Label label) {
        final var newDraft = new FilterInformation(filterInformationDraft.getValue());
        newDraft.addLabel(label);
        this.filterInformationDraft.postValue(newDraft);
    }

    public void addFilterInformationUser(@NonNull User user) {
        final var newDraft = new FilterInformation(filterInformationDraft.getValue());
        newDraft.addUser(user);
        this.filterInformationDraft.postValue(newDraft);
    }

    public void setNotAssignedUser(boolean notAssignedUser) {
        final var newDraft = new FilterInformation(filterInformationDraft.getValue());
        newDraft.setNoAssignedUser(notAssignedUser);
        this.filterInformationDraft.postValue(newDraft);
    }

    public void setNotAssignedLabel(boolean notAssignedLabel) {
        final var newDraft = new FilterInformation(filterInformationDraft.getValue());
        newDraft.setNoAssignedLabel(notAssignedLabel);
        this.filterInformationDraft.postValue(newDraft);
    }

    public void removeFilterInformationLabel(@NonNull Label label) {
        final var newDraft = new FilterInformation(filterInformationDraft.getValue());
        newDraft.removeLabel(label);
        this.filterInformationDraft.postValue(newDraft);
    }

    public void removeFilterInformationUser(@NonNull User user) {
        final var newDraft = new FilterInformation(filterInformationDraft.getValue());
        newDraft.removeUser(user);
        this.filterInformationDraft.postValue(newDraft);
    }

    public void setCurrentFilterTab(@IntRange(from = 0, to = 2) int newFilterTab) {
        this.currentFilterTab = newFilterTab;
    }

    public void setFilterText(@NonNull String filterText) {
        DeckLog.info("New filterText:", filterText);
        final var newDraft = new FilterInformation(filterInformation.getValue());
        newDraft.setFilterText(filterText);
        this.filterInformation.postValue(newDraft);
    }

    @IntRange(from = 0, to = 2)
    public int getCurrentFilterTab() {
        return this.currentFilterTab;
    }

    // TODO Use in Filter fragments
    public LiveData<Integer> getCurrentBoardColor$() {
        return new ReactiveLiveData<>(baseRepository.getCurrentAccountId$())
                .combineWith(baseRepository::getCurrentBoardId$)
                .flatMap(ids -> baseRepository.getBoardColor$(ids.first, ids.second));
    }

    public LiveData<List<User>> findProposalsForUsersToAssign() {
        return new ReactiveLiveData<>(baseRepository.getCurrentAccountId$())
                .combineWith(baseRepository::getCurrentBoardId$)
                .flatMap(ids -> baseRepository.findProposalsForUsersToAssignForCards(ids.first, ids.second, -1L, -1));
    }

    public LiveData<List<Label>> findProposalsForLabelsToAssign() {
        return new ReactiveLiveData<>(baseRepository.getCurrentAccountId$())
                .combineWith(baseRepository::getCurrentBoardId$)
                .flatMap(ids -> baseRepository.findProposalsForLabelsToAssign(ids.first, ids.second, -1L));
    }
}
