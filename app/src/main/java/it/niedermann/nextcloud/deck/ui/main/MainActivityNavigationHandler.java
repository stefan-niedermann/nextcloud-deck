package it.niedermann.nextcloud.deck.ui.main;

import static it.niedermann.nextcloud.deck.ui.main.DrawerMenuInflater.MENU_ID_ABOUT;
import static it.niedermann.nextcloud.deck.ui.main.DrawerMenuInflater.MENU_ID_ADD_BOARD;
import static it.niedermann.nextcloud.deck.ui.main.DrawerMenuInflater.MENU_ID_ARCHIVED_BOARDS;
import static it.niedermann.nextcloud.deck.ui.main.DrawerMenuInflater.MENU_ID_SETTINGS;
import static it.niedermann.nextcloud.deck.ui.main.DrawerMenuInflater.MENU_ID_UPCOMING_CARDS;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.ui.about.AboutActivity;
import it.niedermann.nextcloud.deck.ui.archivedboards.ArchivedBoardsActivity;
import it.niedermann.nextcloud.deck.ui.board.edit.EditBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.settings.SettingsActivity;
import it.niedermann.nextcloud.deck.ui.upcomingcards.UpcomingCardsActivity;

public class MainActivityNavigationHandler implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Keys: {@link MenuItem#getItemId()}
     * Values: {@link FullBoard#getLocalId()}
     */
    private final Map<Integer, Long> navigationMap = new HashMap<>();
    private final AppCompatActivity activity;
    private final DrawerLayout drawerLayout;
    private final BiConsumer<Long, Long> onBoardSelected;
    private final ActivityResultLauncher<Intent> settingsLauncher;
    @Nullable
    private Account account = null;

    public MainActivityNavigationHandler(
            @NonNull AppCompatActivity activity,
            @NonNull DrawerLayout drawerLayout,
            @NonNull BiConsumer<Long, Long> onBoardSelected
    ) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        this.onBoardSelected = onBoardSelected;
        this.settingsLauncher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                ActivityCompat.recreate(activity);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (account == null) {
            DeckLog.warn("Current account is null, can handle selected navigation item #" + item.getItemId() + ": \"" + item.getTitle() + "\"");
            return false;
        }

        switch (item.getItemId()) {
            case MENU_ID_ABOUT:
                activity.startActivity(AboutActivity.createIntent(activity, account));
                break;
            case MENU_ID_SETTINGS:
                settingsLauncher.launch(SettingsActivity.createIntent(activity, account));
                break;
            case MENU_ID_ADD_BOARD:
                EditBoardDialogFragment.newInstance(account).show(activity.getSupportFragmentManager(), EditBoardDialogFragment.class.getSimpleName());
                break;
            case MENU_ID_ARCHIVED_BOARDS:
                activity.startActivity(ArchivedBoardsActivity.createIntent(activity, account));
                break;
            case MENU_ID_UPCOMING_CARDS:
                activity.startActivity(UpcomingCardsActivity.createIntent(activity, account));
                break;
            default:
                onBoardSelected.accept(account.getId(), navigationMap.get(item.getItemId()));
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavigationMap(@NonNull Map<Integer, Long> navigationMap) {
        this.navigationMap.clear();
        this.navigationMap.putAll(navigationMap);
    }

    public void setCurrentAccount(@NonNull Account account) {
        this.account = account;
    }
}
