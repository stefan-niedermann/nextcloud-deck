package it.niedermann.nextcloud.deck.deprecated.ui.tiles;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import it.niedermann.nextcloud.deck.deprecated.ui.preparecreate.PrepareCreateActivity;

public class EditCardTileService extends TileService {

    @Override
    public void onStartListening() {
        final var tile = getQsTile();
        tile.setState(Tile.STATE_ACTIVE);
        tile.updateTile();
    }

    @SuppressLint("StartActivityAndCollapseDeprecated")
    @Override
    public void onClick() {

        final var intent = new Intent(getApplicationContext(), PrepareCreateActivity.class);

        // ensure it won't open twice if already running
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        // ask to unlock the screen if locked, then start new note intent
        unlockAndRun(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startActivityAndCollapse(getActivity(this, 0, intent, FLAG_IMMUTABLE));

            } else {
                startActivityAndCollapse(intent);
            }
        });
    }
}
