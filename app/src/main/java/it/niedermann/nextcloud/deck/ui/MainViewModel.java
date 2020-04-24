package it.niedermann.nextcloud.deck.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.niedermann.nextcloud.deck.model.internal.FilterInformation;

public class MainViewModel extends ViewModel {

    @NonNull
    private MutableLiveData<FilterInformation> filterInformation = new MutableLiveData<>();

    public void postFilterInformation(@Nullable FilterInformation filterInformation) {
        this.filterInformation.postValue(filterInformation);
    }

    @NonNull
    public LiveData<FilterInformation> getFilterInformation() {
        return this.filterInformation;
    }
}
