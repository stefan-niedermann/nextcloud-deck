package it.niedermann.nextcloud.deck.ui.widget.singlecard;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import it.niedermann.nextcloud.deck.model.SingleCardWidgetModel;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.owncloud.notes.R;

public class SingleCardWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context context;
    private final int appWidgetId;

    private SyncManager syncManager;
    private SingleCardWidgetModel singleCardWidgetModel;

    private static final String TAG = SingleCardWidget.class.getSimpleName();

    SingleCardWidgetFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        syncManager = new SyncManager(context);
    }


    @Override
    public void onDataSetChanged() {
        syncManager.getSingleCardWidgetModel(appWidgetId).observe(this, (model) -> {
            long noteID = sp.getLong(SingleCardWidget.WIDGET_KEY + appWidgetId, -1);

            if (noteID >= 0) {
                Log.v(TAG, "Fetch note for account " + SingleCardWidget.ACCOUNT_ID_KEY + appWidgetId);
                Log.v(TAG, "Fetch note for account " + sp.getLong(SingleCardWidget.ACCOUNT_ID_KEY + appWidgetId, -1));
                Log.v(TAG, "Fetch note with id " + noteID);
                singleCardWidgetModel = syncManager.getNote(sp.getLong(SingleCardWidget.ACCOUNT_ID_KEY + appWidgetId, -1), noteID);

                if (singleCardWidgetModel == null) {
                    Log.e(TAG, "Error: note not found");
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        //NoOp
    }

    /**
     * Returns the number of items in the data set. In this case, always 1 as a single note is
     * being displayed. Will return 0 when the note can't be displayed.
     */
    @Override
    public int getCount() {
        return (singleCardWidgetModel != null) ? 1 : 0;
    }

    /**
     * Returns a RemoteView containing the note content in a TextView and
     * a fillInIntent to handle the user tapping on the item in the list view.
     *
     * @param position The position of the item in the list
     * @return The RemoteView at the specified position in the list
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (singleCardWidgetModel == null) {
            return null;
        }
        long noteID = sp.getLong(SingleCardWidget.WIDGET_KEY + appWidgetId, -1);

        if (noteID >= 0) {
            Log.v(TAG, "Fetch note for account " + SingleCardWidget.ACCOUNT_ID_KEY + appWidgetId);
            Log.v(TAG, "Fetch note for account " + sp.getLong(SingleCardWidget.ACCOUNT_ID_KEY + appWidgetId, -1));
            Log.v(TAG, "Fetch note with id " + noteID);
            singleCardWidgetModel = syncManager.getNote(sp.getLong(SingleCardWidget.ACCOUNT_ID_KEY + appWidgetId, -1), noteID);

            if (singleCardWidgetModel == null) {
                Log.e(TAG, "Error: note not found");
            }
        }

        RemoteViews widgetRemoteView;
        final Intent fillInIntent = EditActivity.createEditCardIntent(context, singleCardWidgetModel.getAccount(), singleCardWidgetModel.getBoardLocalId(), singleCardWidgetModel.getCardLocalId().getLocalId());
        widgetRemoteView = new RemoteViews(context.getPackageName(), R.layout.widget_single_note_content);
        widgetRemoteView.setOnClickFillInIntent(R.id.single_note_content_tv, fillInIntent);
        widgetRemoteView.setTextViewText(R.id.single_note_content_tv, singleCardWidgetModel.getCardLocalId().getCard().getTitle());

        return widgetRemoteView;
    }


    // TODO Set loading view
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
