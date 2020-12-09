package it.niedermann.nextcloud.deck.ui.archivedboards;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemArchivedBoardBinding;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.ui.board.DeleteBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.EditBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.accesscontrol.AccessControlDialogFragment;
import it.niedermann.nextcloud.deck.util.ViewUtil;

@SuppressWarnings("WeakerAccess")
public class ArchivedBoardViewHolder extends RecyclerView.ViewHolder {

    private final ItemArchivedBoardBinding binding;

    public ArchivedBoardViewHolder(ItemArchivedBoardBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    void bind(boolean isSupportedVersion, Board board, FragmentManager fragmentManager, Consumer<Board> dearchiveBoardListener) {
        final Context context = itemView.getContext();
        binding.boardIcon.setImageDrawable(ViewUtil.getTintedImageView(binding.boardIcon.getContext(), R.drawable.circle_grey600_36dp, board.getColor()));
        binding.boardMenu.setVisibility(View.GONE);
        binding.boardTitle.setText(board.getTitle());
        if (isSupportedVersion) {
            if (board.isPermissionManage()) {
                binding.boardMenu.setVisibility(View.VISIBLE);
                binding.boardMenu.setImageDrawable(ViewUtil.getTintedImageView(context, R.drawable.ic_menu, ContextCompat.getColor(context, R.color.grey600)));
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
                            AccessControlDialogFragment.newInstance(board.getLocalId()).show(fragmentManager, AccessControlDialogFragment.class.getSimpleName());
                            return true;
                        } else if (itemId == R.id.edit_board) {
                            EditBoardDialogFragment.newInstance(board.getLocalId()).show(fragmentManager, editBoard);
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
                binding.boardMenu.setImageDrawable(ViewUtil.getTintedImageView(context, R.drawable.ic_share_grey600_18dp, ContextCompat.getColor(context, R.color.grey600)));
                binding.boardMenu.setOnClickListener((v) -> AccessControlDialogFragment.newInstance(board.getLocalId()).show(fragmentManager, AccessControlDialogFragment.class.getSimpleName()));
            }
            binding.boardMenu.setVisibility(View.VISIBLE);
        } else {
            binding.boardMenu.setVisibility(View.GONE);
        }
    }
}
