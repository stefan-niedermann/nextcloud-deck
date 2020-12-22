package it.niedermann.nextcloud.deck.ui.widget.singlecard;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.android.markdown.MarkdownUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.full.FullSingleCardWidgetModel;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;

public class SingleCardWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private final int appWidgetId;
    private final SyncManager syncManager;
    private FullSingleCardWidgetModel model;

    public SingleCardWidgetFactory(@NonNull Context context, @NonNull Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        DeckLog.log("Constructor with appWidgetId " + appWidgetId);
        this.syncManager = new SyncManager(context);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        this.model = syncManager.getSingleCardWidgetModelDirectly(appWidgetId);
        DeckLog.log("onDataSetChanged() with appWidgetId " + appWidgetId + " model: " + this.model.getFullCard().getCard().getTitle());
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return getDescriptionOrNull(model) == null ? 0 : 1;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final CharSequence description = getDescriptionOrNull(model);
        if (description == null) {
            return null;
        }
        DeckLog.log("getViewAt() with appWidgetId " + appWidgetId + " model: " + this.model.getFullCard().getCard().getTitle());

        final Intent intent = EditActivity.createEditCardIntent(context, model.getAccount(), model.getModel().getBoardId(), model.getFullCard().getLocalId());
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        final RemoteViews widget_entry = new RemoteViews(context.getPackageName(), R.layout.widget_single_card_content);
        widget_entry.setTextViewText(R.id.description, MarkdownUtil.renderForWidget(context, description));
        widget_entry.setOnClickFillInIntent(R.id.description, intent);

        return widget_entry;
    }

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

    @Nullable
    private static CharSequence getDescriptionOrNull(@Nullable FullSingleCardWidgetModel model) {
        if (model == null || model.getFullCard() == null && model.getFullCard().getCard() == null && TextUtils.isEmpty(model.getFullCard().getCard().getDescription())) {
            return null;
        }
        return model.getFullCard().getCard().getDescription();
    }
}
