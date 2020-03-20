package it.niedermann.nextcloud.deck.ui.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.ui.preparecreate.PrepareCreateActivity;

public class StackWidgetConfigurationActivity extends PrepareCreateActivity {
    private int appWidgetId;

    private final String TAG = Activity.class.getSimpleName();

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
            DeckLog.log("INVALID_APPWIDGET_ID", DeckLog.Severity.ERROR);
            finish();
        }
    }

    @Override
    protected void onSubmit() {
        long accountId = -1;

        try {
            accountId = accountAdapter.getItem(binding.accountSelect.getSelectedItemPosition()).getId();
        } catch (NullPointerException e) {
            DeckLog.log("Account not found.", DeckLog.Severity.ERROR);
            e.printStackTrace();
            finish();
        }

        syncManager.addStackWidget(appWidgetId, accountId, binding.stackSelect.getSelectedItemId(),false);

        Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null,
                getApplicationContext(), StackWidget.class);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, updateIntent);
        getApplicationContext().sendBroadcast(updateIntent);
        finish();
    }
}
