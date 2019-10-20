package it.niedermann.nextcloud.deck.ui.tiles;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import it.niedermann.nextcloud.deck.ui.EditActivity;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

@TargetApi(Build.VERSION_CODES.N)
public class EditCardTileService extends TileService {

    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        tile.setState(Tile.STATE_ACTIVE);
        tile.updateTile();
    }

    @Override
    public void onClick() {
        Intent intent = new Intent(getApplicationContext(), EditActivity.class);
        // create new note intent
        intent.putExtra(BUNDLE_KEY_ACCOUNT_ID, -1);
        intent.putExtra(BUNDLE_KEY_BOARD_ID, -1);
        intent.putExtra(BUNDLE_KEY_LOCAL_ID, -1);
        // ensure it won't open twice if already running
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        // ask to unlock the screen if locked, then start new note intent
        unlockAndRun(() -> startActivityAndCollapse(intent));
    }
}
