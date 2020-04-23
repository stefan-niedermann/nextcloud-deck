package it.niedermann.nextcloud.deck.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.niedermann.nextcloud.deck.model.internal.FilterInformation;

public class MainViewModel extends ViewModel {

    private MutableLiveData<FilterInformation> filterInformation = new MutableLiveData<>();

    public void postFilterInformation(FilterInformation filterInformation) {
        this.filterInformation.postValue(filterInformation);
    }

    public LiveData<FilterInformation> getFilterInformation() {
        return this.filterInformation;
    }
}
