package it.niedermann.nextcloud.deck.ui.widget.singlecard;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.MainActivity;
import it.niedermann.nextcloud.deck.ui.branding.BrandingUtil;
import it.niedermann.nextcloud.deck.ui.card.SelectCardListener;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.saveBrandColors;

public class SelectCardForWidgetActivity extends MainActivity implements SelectCardListener {

    private int appWidgetId;
    @ColorInt private int originalBrandColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        final Bundle args = intent.getExtras();
        if (args == null) {
            finish();
            return;
        }
        appWidgetId = args.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        originalBrandColor = BrandingUtil.readBrandMainColor(this);
    }

    @Override
    public void onCardSelected(FullCard fullCard) {
        mainViewModel.addOrUpdateSingleCardWidget(appWidgetId, mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId(), fullCard.getLocalId());
        final Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null,
                getApplicationContext(), SingleCardWidget.class)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, updateIntent);
        getApplicationContext().sendBroadcast(updateIntent);
        saveBrandColors(this, originalBrandColor);
        finish();
    }

    @Override
    protected void setCurrentBoard(@NonNull Board board) {
        super.setCurrentBoard(board);
        binding.listMenuButton.setVisibility(View.GONE);
        binding.fab.setVisibility(View.GONE);
        binding.toolbar.setTitle(R.string.simple_select);
    }

    @Override
    protected void showFabIfEditPermissionGranted() { /* Silence is gold */ }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
