package it.niedermann.nextcloud.deck.ui.widget.stack;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackWidgetFactory(this.getApplicationContext(), intent);
    }
}
