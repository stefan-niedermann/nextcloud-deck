package it.niedermann.nextcloud.deck.ui.widget.upcoming;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class UpcomingWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new UpcomingWidgetFactory(this.getApplicationContext(), intent);
    }
}
