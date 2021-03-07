package it.niedermann.nextcloud.deck.ui.widget.filter;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

public class FilterWidgetViewModel extends AndroidViewModel {

    @NonNull
    private final SyncManager syncManager;
    @NonNull
    private final MutableLiveData<FilterWidget> config$ = new MutableLiveData<>(new FilterWidget());

    public FilterWidgetViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public LiveData<FilterWidget> getFilterWidgetConfiguration() {
        return this.config$;
    }

    public void updateFilterWidget(@NonNull IResponseCallback<Integer> callback) {
        syncManager.createFilterWidget(config$.getValue(), callback);
    }
}
