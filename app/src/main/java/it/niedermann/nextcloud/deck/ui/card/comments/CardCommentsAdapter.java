package it.niedermann.nextcloud.deck.ui.card.comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCommentBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.DimensionUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class CardCommentsAdapter extends RecyclerView.Adapter<CardCommentsAdapter.ItemCommentViewHolder> {

    private final Context context;
    private final List<DeckComment> comments;
    private final Account account;

    CardCommentsAdapter(@NonNull Context context, @NonNull List<DeckComment> comments, @NonNull Account account) {
        this.context = context;
        this.comments = comments;
        this.account = account;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return comments.get(position).getLocalId();
    }

    @NonNull
    @Override
    public ItemCommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new ItemCommentViewHolder(ItemCommentBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCommentViewHolder viewHolder, int position) {
        DeckComment comment = comments.get(position);
        ViewUtil.addAvatar(context, viewHolder.binding.avatar, account.getUrl(), account.getUserName(), DimensionUtil.getAvatarDimension(context, R.dimen.icon_size_details), R.drawable.ic_person_grey600_24dp);
        viewHolder.binding.message.setText(comment.getMessage());
        viewHolder.binding.actorDisplayName.setText(comment.getActorDisplayName());
        viewHolder.binding.creationDateTime.setText(DateUtil.getRelativeDateTimeString(context, comment.getCreationDateTime().getTime()));
        TooltipCompat.setTooltipText(viewHolder.binding.creationDateTime, DateFormat.getDateTimeInstance().format(comment.getCreationDateTime()));
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
