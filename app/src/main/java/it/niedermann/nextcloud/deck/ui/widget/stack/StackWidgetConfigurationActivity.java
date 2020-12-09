package it.niedermann.nextcloud.deck.ui.widget.stack;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.PickStackActivity;

public class StackWidgetConfigurationActivity extends PickStackActivity {
    private int appWidgetId;
    private StackWidgetConfigurationViewModel stackWidgetConfigurationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stackWidgetConfigurationViewModel = new ViewModelProvider(this).get(StackWidgetConfigurationViewModel.class);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.add_stack_widget);
        }

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
        final Bundle extras = new Bundle();

        stackWidgetConfigurationViewModel.addStackWidget(appWidgetId, account.getId(), stackId, false);
        Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null,
                getApplicationContext(), StackWidget.class);
        extras.putSerializable(StackWidget.ACCOUNT_KEY, account);
        extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        // The `extras` bundle is added to the intent this way because using putExtras(extras) 
        // would have the OS attempt to reassemle the data and cause a crash 
        // when it finds classes that are only known to this application.
        updateIntent.putExtra(StackWidget.BUNDLE_KEY, extras);
        setResult(RESULT_OK, updateIntent);
        getApplicationContext().sendBroadcast(updateIntent);

        finish();
    }

    @Override
    protected boolean showBoardsWithoutEditPermission() {
        return true;
    }
}
