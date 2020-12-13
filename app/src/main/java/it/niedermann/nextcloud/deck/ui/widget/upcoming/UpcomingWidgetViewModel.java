package it.niedermann.nextcloud.deck.ui.widget.upcoming;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

public class UpcomingWidgetViewModel extends AndroidViewModel {

    @NonNull
    private final SyncManager syncManager;

    public UpcomingWidgetViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public void addUpcomingWidget(int appWidgetId, IResponseCallback<Integer> callback) {
        final FilterWidget config = new FilterWidget();
        config.setId(appWidgetId);
        syncManager.createFilterWidget(config, callback);
    }
}
