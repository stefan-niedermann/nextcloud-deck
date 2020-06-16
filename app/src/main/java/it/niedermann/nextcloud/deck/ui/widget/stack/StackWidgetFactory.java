package it.niedermann.nextcloud.deck.ui.widget.stack;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.lifecycle.LiveData;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class StackWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private final int appWidgetId;
    private final long accountId;
    private final long stackId;

    private FullStack stack;
    private int boardColor = Color.GRAY;

    StackWidgetFactory(Context context, Intent intent) {
        this.context = context;

        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        accountId = intent.getLongExtra(StackWidget.ACCOUNT_ID_KEY + appWidgetId, -1);
        stackId = intent.getLongExtra(StackWidget.STACK_ID_KEY + appWidgetId, -1);
    }

    @Override
    public void onCreate() {
        SyncManager syncManager = new SyncManager(context);
        LiveData<FullStack> stackLiveData = syncManager.getStack(accountId, stackId);
        stackLiveData.observeForever((FullStack fullStack) -> {
            if (fullStack != null) {
                stack = fullStack;

//                stack.getStack().getBoardId();
//                LiveData<Board> fb = syncManager.getBoard(accountId, stack.getStack().getBoardId());

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stack);
                views.setImageViewResource(R.id.widget_stack_header_icon, R.drawable.circle_grey600_8dp);
                views.setInt(R.id.widget_stack_header_icon, "setColorFilter", boardColor);
                views.setTextViewText(R.id.widget_stack_title_tv, stack.getStack().getTitle());

                stack.getStack().getBoardId();

                AppWidgetManager awm = AppWidgetManager.getInstance(context);
                int[] appWidgetIds = awm.getAppWidgetIds(new ComponentName(context, StackWidget.class));
                awm.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack_widget_lv);
                awm.updateAppWidget(appWidgetId, views);

            }
        });

        LiveData<FullBoard> fullBoardLiveData = syncManager.getFullBoard(accountId, stack.getStack().getBoardId());
        fullBoardLiveData.observeForever((FullBoard fullBoard) -> {
            if (fullBoard != null) {
                
            }
        });
    }

    @Override
    public void onDataSetChanged() {
        if (stack == null) {
            return;
        }

        DataBaseAdapter db = new DataBaseAdapter(context);
        FullBoard Fullboard = db.getFullBoardByLocalIdDirectly(accountId, stack.getStack().getBoardId());
        boardColor = Color.parseColor("#" + Fullboard.getBoard().getColor());

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

        if (stack == null || i > (stack.getCards().size() - 1) || stack.getCards().get(i) == null) {
            DeckLog.log("Card not found at position " + i, DeckLog.Severity.ERROR);
            return null;
        }

        Card card = stack.getCards().get(i);

        widget_entry = new RemoteViews(context.getPackageName(), R.layout.widget_stack_entry);
        widget_entry.setTextViewText(R.id.widget_entry_content_tv, card.getTitle());
//        widget_entry.setOnClickFillInIntent(R.id.widget_stack_entry, intent);

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
}
