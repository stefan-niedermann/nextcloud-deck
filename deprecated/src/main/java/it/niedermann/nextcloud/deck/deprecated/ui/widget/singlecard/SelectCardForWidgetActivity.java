package it.niedermann.nextcloud.deck.deprecated.ui.widget.singlecard;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.deprecated.ui.card.SelectCardListener;
import it.niedermann.nextcloud.deck.deprecated.ui.main.MainActivity;

public class SelectCardForWidgetActivity extends MainActivity implements SelectCardListener {

    private int appWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final var intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        final var args = intent.getExtras();
        if (args == null) {
            finish();
            return;
        }
        appWidgetId = args.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    @Override
    public void onCardSelected(@NonNull FullCard fullCard, long boardId) {
        mainViewModel.addOrUpdateSingleCardWidget(appWidgetId, fullCard.getAccountId(), boardId, fullCard.getLocalId());
        final var intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null,
                getApplicationContext(), SingleCardWidget.class)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, intent);
        getApplicationContext().sendBroadcast(intent);
        finish();
    }

    @Override
    protected void applyBoard(@NonNull Account account, @NonNull Map<Integer, Long> navigationMap, @Nullable FullBoard currentBoard) {
        super.applyBoard(account, navigationMap, currentBoard);
        binding.toolbar.setTitle(R.string.simple_select);
    }
}
