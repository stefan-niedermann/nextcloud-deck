package it.niedermann.nextcloud.deck.deprecated.ui.widget.stack;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.repository.WidgetRepository;

@SuppressWarnings("WeakerAccess")
public class StackWidgetConfigurationViewModel extends AndroidViewModel {

    private final WidgetRepository widgetRepository;

    public StackWidgetConfigurationViewModel(@NonNull Application application) {
        super(application);
        this.widgetRepository = new WidgetRepository(application);
    }

    public void addStackWidget(@NonNull FilterWidget config, @NonNull ResponseCallback<Integer> callback) {
        widgetRepository.createFilterWidget(config, callback);
    }
}
