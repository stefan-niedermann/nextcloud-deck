package it.niedermann.nextcloud.deck.ui.archivedboards;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.util.Consumer;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nextcloud.android.common.ui.theme.utils.ColorRole;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemArchivedBoardBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.ui.board.DeleteBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.accesscontrol.AccessControlDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.edit.EditBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;

@SuppressWarnings("WeakerAccess")
public class ArchivedBoardViewHolder extends RecyclerView.ViewHolder {

    private final ItemArchivedBoardBinding binding;

    public ArchivedBoardViewHolder(ItemArchivedBoardBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    void bind(@NonNull Account account, @NonNull Board board, FragmentManager fragmentManager, @NonNull Consumer<Board> dearchiveBoardListener) {
        final Context context = itemView.getContext();
        final var util = ThemeUtils.of(account.getColor(), context);

        binding.boardIcon.setImageDrawable(util.deck.getColoredBoardDrawable(context, board.getColor()));
        binding.boardMenu.setVisibility(View.GONE);
        binding.boardTitle.setText(board.getTitle());
        if (account.getServerDeckVersionAsObject().isSupported()) {
            if (board.isPermissionManage()) {
                binding.boardMenu.setVisibility(View.VISIBLE);
                binding.boardMenu.setImageDrawable(util.platform.tintDrawable(context, R.drawable.ic_menu, ColorRole.ON_SURFACE_VARIANT));
                binding.boardMenu.setOnClickListener((v) -> {
                    PopupMenu popup = new PopupMenu(context, binding.boardMenu);
                    popup.getMenuInflater().inflate(R.menu.archived_board_menu, popup.getMenu());
                    final int SHARE_BOARD_ID = -1;
                    if (board.isPermissionShare()) {
                        popup.getMenu().add(Menu.NONE, SHARE_BOARD_ID, 5, R.string.share_board);
                    }
                    popup.setOnMenuItemClickListener((MenuItem item) -> {
                        final String editBoard = context.getString(R.string.edit_board);
                        int itemId = item.getItemId();
                        if (itemId == SHARE_BOARD_ID) {
                            AccessControlDialogFragment.newInstance(account, board.getLocalId()).show(fragmentManager, AccessControlDialogFragment.class.getSimpleName());
                            return true;
                        } else if (itemId == R.id.edit_board) {
                            EditBoardDialogFragment.newInstance(account, board.getLocalId()).show(fragmentManager, editBoard);
                            return true;
                        } else if (itemId == R.id.dearchive_board) {
                            dearchiveBoardListener.accept(board);
                            return true;
                        } else if (itemId == R.id.delete_board) {
                            DeleteBoardDialogFragment.newInstance(board).show(fragmentManager, DeleteBoardDialogFragment.class.getSimpleName());
                            return true;
                        }
                        return false;
                    });
                    popup.show();
                });
            } else if (board.isPermissionShare()) {
                binding.boardMenu.setVisibility(View.VISIBLE);
                binding.boardMenu.setImageDrawable(util.platform.tintDrawable(context, R.drawable.ic_share_18dp, ColorRole.ON_SURFACE_VARIANT));
                binding.boardMenu.setOnClickListener((v) -> AccessControlDialogFragment.newInstance(account, board.getLocalId()).show(fragmentManager, AccessControlDialogFragment.class.getSimpleName()));
            }
            binding.boardMenu.setVisibility(View.VISIBLE);
        } else {
            binding.boardMenu.setVisibility(View.GONE);
        }
    }
}
