package it.niedermann.nextcloud.deck.ui.widget.stack;

import android.app.Application;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

@SuppressWarnings("WeakerAccess")
public class StackWidgetConfigurationViewModel extends BaseViewModel {

    public StackWidgetConfigurationViewModel(@NonNull Application application) {
        super(application);
    }

    public void addStackWidget(@NonNull FilterWidget config, @NonNull ResponseCallback<Integer> callback) {
        baseRepository.createFilterWidget(config, callback);
    }
}
