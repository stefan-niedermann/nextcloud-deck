package it.niedermann.nextcloud.deck.ui.tiles;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import it.niedermann.nextcloud.deck.ui.preparecreate.PrepareCreateActivity;

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
        Intent intent = new Intent(getApplicationContext(), PrepareCreateActivity.class);
        // ensure it won't open twice if already running
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        // ask to unlock the screen if locked, then start new note intent
        unlockAndRun(() -> startActivityAndCollapse(intent));
    }
}
