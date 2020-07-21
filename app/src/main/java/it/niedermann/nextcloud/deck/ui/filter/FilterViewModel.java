package it.niedermann.nextcloud.deck.ui.filter;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;

import static it.niedermann.nextcloud.deck.model.internal.FilterInformation.hasActiveFilter;

@SuppressWarnings("WeakerAccess")
public class FilterViewModel extends ViewModel {

    @IntRange(from = 0, to = 2)
    private int currentFilterTab = 0;

    @NonNull
    private MutableLiveData<FilterInformation> filterInformationDraft = new MutableLiveData<>(new FilterInformation());
    @NonNull
    private MutableLiveData<FilterInformation> filterInformation = new MutableLiveData<>();

    public void publishFilterInformationDraft() {
        this.filterInformation.postValue(hasActiveFilter(filterInformationDraft.getValue()) ? filterInformationDraft.getValue() : null);
    }

    public void clearFilterInformation() {
        this.filterInformationDraft.setValue(new FilterInformation());
        this.publishFilterInformationDraft();
        this.currentFilterTab = 0;
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

    public void setNotAssignedUser(boolean notAssignedUser) {
        FilterInformation newDraft = new FilterInformation(filterInformationDraft.getValue());
        newDraft.setNoAssignedUser(notAssignedUser);
        this.filterInformationDraft.postValue(newDraft);
    }

    public void setNotAssignedLabel(boolean notAssignedLabel) {
        FilterInformation newDraft = new FilterInformation(filterInformationDraft.getValue());
        newDraft.setNoAssignedLabel(notAssignedLabel);
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

    public void setCurrentFilterTab(@IntRange(from = 0, to = 2) int newFilterTab) {
        this.currentFilterTab = newFilterTab;
    }

    @IntRange(from = 0, to = 2)
    public int getCurrentFilterTab() {
        return this.currentFilterTab;
    }
}
