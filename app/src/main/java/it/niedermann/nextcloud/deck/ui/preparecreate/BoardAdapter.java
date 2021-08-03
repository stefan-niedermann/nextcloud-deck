package it.niedermann.nextcloud.deck.ui.preparecreate;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemPrepareCreateBoardBinding;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class BoardAdapter extends AbstractAdapter<Board> {

    @SuppressWarnings("WeakerAccess")
    public BoardAdapter(@NonNull Context context) {
        super(context, R.layout.item_prepare_create_board);
    }

    @Override
    protected long getItemId(@NonNull Board item) {
        return item.getLocalId();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ItemPrepareCreateBoardBinding binding;
        if (convertView == null) {
            binding = ItemPrepareCreateBoardBinding.inflate(inflater, parent, false);
        } else {
            binding = ItemPrepareCreateBoardBinding.bind(convertView);
        }

        final var board = getItem(position);
        if (board != null) {
            binding.boardTitle.setText(board.getTitle());
            binding.avatar.setImageDrawable(ViewUtil.getTintedImageView(binding.avatar.getContext(), R.drawable.circle_grey600_36dp, board.getColor()));
        } else {
            DeckLog.logError(new IllegalArgumentException("No item for position " + position));
        }
        return binding.getRoot();
    }
}
