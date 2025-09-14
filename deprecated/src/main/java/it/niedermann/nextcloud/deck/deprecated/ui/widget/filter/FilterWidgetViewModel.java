package it.niedermann.nextcloud.deck.deprecated.ui.widget.filter;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.repository.WidgetRepository;

public class FilterWidgetViewModel extends AndroidViewModel {

    private final WidgetRepository widgetRepository;

    @NonNull
    private final MutableLiveData<FilterWidget> config$ = new MutableLiveData<>(new FilterWidget());

    public FilterWidgetViewModel(@NonNull Application application) {
        super(application);
        this.widgetRepository = new WidgetRepository(application);
    }

    public LiveData<FilterWidget> getFilterWidgetConfiguration() {
        return this.config$;
    }

    public void updateFilterWidget(@NonNull IResponseCallback<Integer> callback) {
        widgetRepository.createFilterWidget(config$.getValue(), callback);
    }
}
