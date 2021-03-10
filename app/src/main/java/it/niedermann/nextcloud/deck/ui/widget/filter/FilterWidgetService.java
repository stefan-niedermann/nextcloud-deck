package it.niedermann.nextcloud.deck.ui.widget.filter;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class FilterWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FilterWidgetFactory(this.getApplicationContext(), intent);
    }
}
