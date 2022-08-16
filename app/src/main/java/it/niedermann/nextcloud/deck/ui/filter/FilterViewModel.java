package it.niedermann.nextcloud.deck.ui.filter;

import static androidx.lifecycle.Transformations.distinctUntilChanged;
import static androidx.lifecycle.Transformations.map;

import android.app.Application;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

@SuppressWarnings("WeakerAccess")
public class FilterViewModel extends AndroidViewModel {

    private final SyncManager syncManager;

    @IntRange(from = 0, to = 2)
    private int currentFilterTab = 0;

    @NonNull
    private final MutableLiveData<FilterInformation> filterInformationDraft = new MutableLiveData<>(new FilterInformation());
    @NonNull
    private final MutableLiveData<FilterInformation> filterInformation = new MutableLiveData<>();

    public FilterViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
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
        return distinctUntilChanged(map(getFilterInformation(), FilterInformation::hasActiveFilter));
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

    public LiveData<List<User>> findProposalsForUsersToAssign(final long accountId, long boardId) {
        return syncManager.findProposalsForUsersToAssign(accountId, boardId, -1L, -1);
    }

    public LiveData<List<Label>> findProposalsForLabelsToAssign(final long accountId, final long boardId) {
        return syncManager.findProposalsForLabelsToAssign(accountId, boardId, -1L);
    }
}
