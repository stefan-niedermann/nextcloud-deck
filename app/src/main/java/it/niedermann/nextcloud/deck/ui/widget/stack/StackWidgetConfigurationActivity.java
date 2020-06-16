package it.niedermann.nextcloud.deck.ui.widget.stack;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.PickStackActivity;

public class StackWidgetConfigurationActivity extends PickStackActivity {
    private int appWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        final Bundle extras = getIntent().getExtras();

        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            DeckLog.error("INVALID_APPWIDGET_ID");
            finish();
        }
    }

    @Override
    protected void onSubmit(Account account, long boardId, long stackId) {

        syncManager.addStackWidget(appWidgetId, account.getId(), stackId,false);
        Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null,
                getApplicationContext(), StackWidget.class);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, updateIntent);
        getApplicationContext().sendBroadcast(updateIntent);

        finish();
    }

    @Override
    protected boolean showBoardsWithoutEditPermission() {
        return true;
    }
}
