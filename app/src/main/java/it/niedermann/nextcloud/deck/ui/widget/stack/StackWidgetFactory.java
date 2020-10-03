package it.niedermann.nextcloud.deck.ui.widget.stack;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.lifecycle.LiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;

public class StackWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private final int appWidgetId;
    private final long accountId;
    private final long stackId;

    private Account account;
    private FullStack stack;
    private List<FullCard> cardList;

    StackWidgetFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        accountId = intent.getLongExtra(StackWidget.ACCOUNT_ID_KEY + appWidgetId, -1);
        stackId = intent.getLongExtra(StackWidget.STACK_ID_KEY + appWidgetId, -1);
        if (intent.hasExtra(StackWidget.BUNDLE_KEY + appWidgetId)) {
            account = (Account) intent.getBundleExtra(StackWidget.BUNDLE_KEY + appWidgetId).getSerializable(StackWidget.ACCOUNT_KEY + appWidgetId);
        }
    }

    @Override
    public void onCreate() {
        SyncManager syncManager = new SyncManager(context);

        LiveData<FullStack> stackLiveData = syncManager.getStack(accountId, stackId);
        stackLiveData.observeForever((FullStack fullStack) -> {
            if (fullStack != null) {
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stack);
                stack = fullStack;
                views.setTextViewText(R.id.widget_stack_title_tv, stack.getStack().getTitle());

                LiveData<FullBoard> fullBoardLiveData = syncManager.getFullBoardById(accountId, stack.getStack().getBoardId());
                fullBoardLiveData.observeForever((FullBoard fullBoard) -> {
                    if (fullBoard != null) {
                        views.setInt(R.id.widget_stack_header_icon, "setColorFilter", fullBoard.getBoard().getColor());
                        notifyAppWidgetUpdate(views);
                    }
                });

                LiveData<List<FullCard>> fullCardData = syncManager.getFullCardsForStack(accountId, stackId, null);
                fullCardData.observeForever((List<FullCard> fullCards) -> cardList = fullCards);
                notifyAppWidgetUpdate(views);
            }
        });
    }

    @Override
    public void onDataSetChanged() {

    }


    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return stack == null ? 0 : stack.getCards().size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews widget_entry;

        if (cardList == null || i > (cardList.size() - 1) || cardList.get(i) == null) {
            DeckLog.error("Card not found at position " + i);
            return null;
        }

        FullCard card = cardList.get(i);

        widget_entry = new RemoteViews(context.getPackageName(), R.layout.widget_stack_entry);
        widget_entry.setTextViewText(R.id.widget_entry_content_tv, card.card.getTitle());

        final Intent intent = EditActivity.createEditCardIntent(context, account, stack.getStack().getBoardId(), card.getCard().getLocalId());
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        widget_entry.setOnClickFillInIntent(R.id.widget_stack_entry, intent);

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
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void notifyAppWidgetUpdate(RemoteViews views) {
        AppWidgetManager awm = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = awm.getAppWidgetIds(new ComponentName(context, StackWidget.class));
        awm.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack_widget_lv);
        awm.updateAppWidget(appWidgetId, views);
    }
}
