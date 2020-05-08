package it.niedermann.nextcloud.deck.ui.archivedboards;

import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.databinding.ItemArchivedBoardBinding;
import it.niedermann.nextcloud.deck.model.Board;

public class ArchivedBoardViewHolder extends RecyclerView.ViewHolder {

    ItemArchivedBoardBinding binding;

    public ArchivedBoardViewHolder(ItemArchivedBoardBinding binding) {
        super(binding.getRoot());
    }

    void bind(Board board) {
        binding.boardTitle.setText(board.getTitle());
    }
}
