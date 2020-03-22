package it.niedermann.nextcloud.deck.ui.card.comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.niedermann.nextcloud.deck.databinding.ItemCommentBinding;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;

public class CardCommentsAdapter extends RecyclerView.Adapter<CardCommentsAdapter.ItemCommentViewHolder> {

    private Context context;
    private List<DeckComment> comments;

    CardCommentsAdapter(@NonNull Context context, @NonNull List<DeckComment> comments) {
        this.context = context;
        this.comments = comments;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return comments.get(position).getLocalId();
    }

    @NonNull
    @Override
    public ItemCommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ItemCommentBinding binding = ItemCommentBinding.inflate(layoutInflater, viewGroup, false);
        return new ItemCommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCommentViewHolder viewHolder, int position) {
        DeckComment comment = comments.get(position);
        viewHolder.binding.actorDisplayName.setText(comment.getActorDisplayName());
        viewHolder.binding.message.setText(comment.getMessage());
    }


    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    static class ItemCommentViewHolder extends RecyclerView.ViewHolder {
        private ItemCommentBinding binding;

        private ItemCommentViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
