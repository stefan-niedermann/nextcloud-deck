package it.niedermann.nextcloud.deck.ui.widget.singlecard;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class SingleCardWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new SingleCardWidgetFactory(this.getApplicationContext(), intent);
    }
}
