package it.niedermann.nextcloud.deck.ui.widget.stack;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

@SuppressWarnings("WeakerAccess")
public class StackWidgetConfigurationViewModel extends AndroidViewModel {

    private final SyncManager syncManager;

    public StackWidgetConfigurationViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public void addStackWidget(@NonNull FilterWidget config, @NonNull IResponseCallback<Integer> callback) {
        syncManager.createFilterWidget(config, callback);
    }
}
