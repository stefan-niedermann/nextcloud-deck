package it.niedermann.nextcloud.deck.ui.widget.filter;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

public class FilterWidgetViewModel extends BaseViewModel {

    @NonNull
    private final MutableLiveData<FilterWidget> config$ = new MutableLiveData<>(new FilterWidget());

    public FilterWidgetViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<FilterWidget> getFilterWidgetConfiguration() {
        return this.config$;
    }

    public void updateFilterWidget(@NonNull IResponseCallback<Integer> callback) {
        baseRepository.createFilterWidget(config$.getValue(), callback);
    }
}
