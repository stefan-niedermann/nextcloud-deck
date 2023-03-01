package it.niedermann.nextcloud.deck.ui.main;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.ui.board.ArchiveBoardListener;
import it.niedermann.nextcloud.deck.ui.board.DeleteBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.accesscontrol.AccessControlDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.edit.EditBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.managelabels.ManageLabelsDialogFragment;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;

public class DrawerMenuInflater<T extends FragmentActivity & ArchiveBoardListener> {
    public static final int MENU_ID_ABOUT = -1;
    public static final int MENU_ID_ADD_BOARD = -2;
    public static final int MENU_ID_SETTINGS = -3;
    public static final int MENU_ID_ARCHIVED_BOARDS = -4;
    public static final int MENU_ID_UPCOMING_CARDS = -5;

    private final T activity;
    private final Menu menu;

    public DrawerMenuInflater(@NonNull T activity, @NonNull Menu menu) {
        this.activity = activity;
        this.menu = menu;
    }

    public Map<Integer, Long> inflateBoards(
            @NonNull Account account,
            @NonNull List<FullBoard> fullBoards,
            @ColorInt int color,
            boolean hasArchivedBoards,
            boolean currentServerVersionIsSupported) {

        final var utils = ThemeUtils.of(color, activity);
        final var navigationMap = new HashMap<Integer, Long>();

        menu.clear();
        menu.add(Menu.NONE, MENU_ID_UPCOMING_CARDS, Menu.NONE, R.string.widget_upcoming_title).setIcon(utils.deck.themeNavigationViewIcon(activity, R.drawable.calendar_blank_grey600_24dp));

        int index = 0;
        for (final var fullBoard : fullBoards) {
            navigationMap.put(index, fullBoard.getLocalId());
            final var menuItem = menu
                    .add(Menu.NONE, index++, Menu.NONE, fullBoard.getBoard().getTitle()).setIcon(utils.deck.getColoredBoardDrawable(activity, fullBoard.getBoard().getColor()))
                    .setCheckable(true);
            if (currentServerVersionIsSupported) {
                if (fullBoard.getBoard().isPermissionManage()) {
                    final var contextMenu = new AppCompatImageButton(activity);
                    contextMenu.setBackgroundDrawable(null);
                    contextMenu.setImageDrawable(utils.deck.themeNavigationViewIcon(activity, R.drawable.ic_menu));
                    contextMenu.setOnClickListener((v) -> {
                        final var popup = new PopupMenu(activity, contextMenu);
                        popup.getMenuInflater().inflate(R.menu.navigation_context_menu, popup.getMenu());
                        final int SHARE_BOARD_ID = -1;
                        if (fullBoard.getBoard().isPermissionShare()) {
                            popup.getMenu().add(Menu.NONE, SHARE_BOARD_ID, 5, R.string.share_board);
                        }
                        popup.setOnMenuItemClickListener((MenuItem item) -> {
                            final String editBoard = activity.getString(R.string.edit_board);
                            int itemId = item.getItemId();
                            if (itemId == SHARE_BOARD_ID) {
                                AccessControlDialogFragment.newInstance(account, fullBoard.getLocalId()).show(activity.getSupportFragmentManager(), AccessControlDialogFragment.class.getSimpleName());
                                return true;
                            } else if (itemId == R.id.edit_board) {
                                EditBoardDialogFragment.newInstance(account, fullBoard.getLocalId()).show(activity.getSupportFragmentManager(), editBoard);
                                return true;
                            } else if (itemId == R.id.manage_labels) {
                                ManageLabelsDialogFragment.newInstance(account, fullBoard.getLocalId()).show(activity.getSupportFragmentManager(), editBoard);
                                return true;
                            } else if (itemId == R.id.clone_board) {
                                activity.onClone(account, fullBoard.getBoard());
                                return true;
                            } else if (itemId == R.id.archive_board) {
                                activity.onArchive(fullBoard.getBoard());
                                return true;
                            } else if (itemId == R.id.delete_board) {
                                DeleteBoardDialogFragment.newInstance(fullBoard.getBoard()).show(activity.getSupportFragmentManager(), DeleteBoardDialogFragment.class.getCanonicalName());
                                return true;
                            }
                            return false;
                        });
                        popup.show();
                    });
                    menuItem.setActionView(contextMenu);
                } else if (fullBoard.getBoard().isPermissionShare()) {
                    final var contextMenu = new AppCompatImageButton(activity);
                    contextMenu.setBackgroundDrawable(null);
                    contextMenu.setImageDrawable(utils.deck.themeNavigationViewIcon(activity, R.drawable.ic_share_grey600_18dp));
                    contextMenu.setOnClickListener((v) -> AccessControlDialogFragment.newInstance(account, fullBoard.getLocalId()).show(activity.getSupportFragmentManager(), AccessControlDialogFragment.class.getSimpleName()));
                    menuItem.setActionView(contextMenu);
                }
            }
        }

        if (hasArchivedBoards) {
            menu.add(Menu.NONE, MENU_ID_ARCHIVED_BOARDS, Menu.NONE, R.string.archived_boards).setIcon(utils.deck.themeNavigationViewIcon(activity, R.drawable.ic_archive_white_24dp));
        }

        if (currentServerVersionIsSupported) {
            menu.add(Menu.NONE, MENU_ID_ADD_BOARD, Menu.NONE, R.string.add_board).setIcon(utils.deck.themeNavigationViewIcon(activity, R.drawable.ic_add_grey_24dp));
        }

        menu.add(Menu.NONE, MENU_ID_SETTINGS, Menu.NONE, R.string.simple_settings).setIcon(utils.deck.themeNavigationViewIcon(activity, R.drawable.ic_settings_grey600_24dp));
        menu.add(Menu.NONE, MENU_ID_ABOUT, Menu.NONE, R.string.about).setIcon(utils.deck.themeNavigationViewIcon(activity, R.drawable.ic_info_outline_grey600_24dp));

        return navigationMap;
    }
}
