package it.niedermann.nextcloud.deck.util;

import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.ui.board.ArchiveBoardListener;
import it.niedermann.nextcloud.deck.ui.board.DeleteBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.EditBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.accesscontrol.AccessControlDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.managelabels.ManageLabelsDialogFragment;

public class DrawerMenuUtil {
    public static final int MENU_ID_ABOUT = -1;
    public static final int MENU_ID_ADD_BOARD = -2;
    public static final int MENU_ID_SETTINGS = -3;
    public static final int MENU_ID_ARCHIVED_BOARDS = -4;

    private DrawerMenuUtil() {

    }

    public static <T extends FragmentActivity & ArchiveBoardListener> void inflateBoards(
            @NonNull T context,
            @NonNull Menu menu,
            @NonNull List<Board> boards,
            boolean hasArchivedBoards,
            boolean currentServerVersionIsSupported) {
        SubMenu boardsMenu = menu.addSubMenu(R.string.simple_boards);
        int index = 0;
        for (Board board : boards) {
            MenuItem m = boardsMenu.add(Menu.NONE, index++, Menu.NONE, board.getTitle()).setIcon(ViewUtil.getTintedImageView(context, R.drawable.circle_grey600_36dp, board.getColor()));
            if (currentServerVersionIsSupported) {
                if (board.isPermissionManage()) {
                    AppCompatImageButton contextMenu = new AppCompatImageButton(context);
                    contextMenu.setBackgroundDrawable(null);
                    contextMenu.setImageDrawable(ViewUtil.getTintedImageView(context, R.drawable.ic_menu, ContextCompat.getColor(context, R.color.grey600)));
                    contextMenu.setOnClickListener((v) -> {
                        PopupMenu popup = new PopupMenu(context, contextMenu);
                        popup.getMenuInflater().inflate(R.menu.navigation_context_menu, popup.getMenu());
                        final int SHARE_BOARD_ID = -1;
                        if (board.isPermissionShare()) {
                            popup.getMenu().add(Menu.NONE, SHARE_BOARD_ID, 5, R.string.share_board);
                        }
                        popup.setOnMenuItemClickListener((MenuItem item) -> {
                            final String editBoard = context.getString(R.string.edit_board);
                            int itemId = item.getItemId();
                            if (itemId == SHARE_BOARD_ID) {
                                AccessControlDialogFragment.newInstance(board.getLocalId()).show(context.getSupportFragmentManager(), AccessControlDialogFragment.class.getSimpleName());
                                return true;
                            } else if (itemId == R.id.edit_board) {
                                EditBoardDialogFragment.newInstance(board.getLocalId()).show(context.getSupportFragmentManager(), editBoard);
                                return true;
                            } else if (itemId == R.id.manage_labels) {
                                ManageLabelsDialogFragment.newInstance(board.getLocalId()).show(context.getSupportFragmentManager(), editBoard);
                                return true;
                            } else if (itemId == R.id.clone_board) {
                                context.onClone(board);
                                return true;
                            } else if (itemId == R.id.archive_board) {
                                context.onArchive(board);
                                return true;
                            } else if (itemId == R.id.delete_board) {
                                DeleteBoardDialogFragment.newInstance(board).show(context.getSupportFragmentManager(), DeleteBoardDialogFragment.class.getCanonicalName());
                                return true;
                            }
                            return false;
                        });
                        popup.show();
                    });
                    m.setActionView(contextMenu);
                } else if (board.isPermissionShare()) {
                    AppCompatImageButton contextMenu = new AppCompatImageButton(context);
                    contextMenu.setBackgroundDrawable(null);
                    contextMenu.setImageDrawable(ViewUtil.getTintedImageView(context, R.drawable.ic_share_grey600_18dp, ContextCompat.getColor(context, R.color.grey600)));
                    contextMenu.setOnClickListener((v) -> AccessControlDialogFragment.newInstance(board.getLocalId()).show(context.getSupportFragmentManager(), AccessControlDialogFragment.class.getSimpleName()));
                    m.setActionView(contextMenu);
                }
            }
        }

        if (hasArchivedBoards) {
            boardsMenu.add(Menu.NONE, MENU_ID_ARCHIVED_BOARDS, Menu.NONE, R.string.archived_boards).setIcon(ViewUtil.getTintedImageView(context, R.drawable.ic_archive_white_24dp, ContextCompat.getColor(context, R.color.grey600)));
        }

        if (currentServerVersionIsSupported) {
            boardsMenu.add(Menu.NONE, MENU_ID_ADD_BOARD, Menu.NONE, R.string.add_board).setIcon(R.drawable.ic_add_grey_24dp);
        }

        menu.add(Menu.NONE, MENU_ID_SETTINGS, Menu.NONE, R.string.simple_settings).setIcon(R.drawable.ic_settings_grey600_24dp);
        menu.add(Menu.NONE, MENU_ID_ABOUT, Menu.NONE, R.string.about).setIcon(R.drawable.ic_info_outline_grey600_24dp);
    }
}
