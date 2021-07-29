package it.niedermann.nextcloud.deck.ui.widget.stack;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProvider;

import java.util.Collections;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.widget.filter.EWidgetType;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetAccount;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetBoard;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetStack;
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
    protected void onSubmit(Account account, long boardId, long stackId, @NonNull IResponseCallback<Void> callback) {
        final FilterWidget config = new FilterWidget(appWidgetId, EWidgetType.STACK_WIDGET);
        final FilterWidgetAccount filterWidgetAccount = new FilterWidgetAccount(account.getId(), false);
        filterWidgetAccount.setIncludeNoProject(false);
        FilterWidgetBoard filterWidgetBoard = new FilterWidgetBoard(boardId, Collections.singletonList(new FilterWidgetStack(stackId)));
        filterWidgetBoard.setIncludeNoLabel(false);
        filterWidgetAccount.setBoards(
                Collections.singletonList(filterWidgetBoard));
        config.setAccounts(Collections.singletonList(filterWidgetAccount));

        stackWidgetConfigurationViewModel.addStackWidget(config, new ResponseCallback<>(account) {
            @Override
            public void onResponse(Integer response) {
                final Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null,
                        getApplicationContext(), StackWidget.class)
                        .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, updateIntent);
                getApplicationContext().sendBroadcast(updateIntent);
                callback.onResponse(null);
                finish();
            }

            @Override
            @SuppressLint("MissingSuperCall")
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    @Override
    protected boolean showBoardsWithoutEditPermission() {
        return true;
    }
}
