package it.niedermann.nextcloud.deck.ui.widget.filter;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetConfiguration;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

public class FilterWidgetViewModel extends AndroidViewModel {

    @NonNull
    private final SyncManager syncManager;
    @NonNull
    private final MutableLiveData<FilterWidgetConfiguration> filterWidgetConfiguration$ = new MutableLiveData<>(new FilterWidgetConfiguration());

    public FilterWidgetViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public LiveData<FilterWidgetConfiguration> getFilterWidgetConfiguration() {
        return this.filterWidgetConfiguration$;
    }

    public void updateFilterWidget() {
        //noinspection ConstantConditions
        syncManager.updateFilterWidgetConfiguration(filterWidgetConfiguration$.getValue());
    }
}
