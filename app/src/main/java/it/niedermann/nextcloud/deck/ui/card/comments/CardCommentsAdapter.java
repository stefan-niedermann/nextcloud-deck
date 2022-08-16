package it.niedermann.nextcloud.deck.ui.card.comments;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getSecondaryForegroundColorDependingOnTheme;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.readBrandMainColor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.databinding.ItemCommentBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.comment.full.FullDeckComment;

public class CardCommentsAdapter extends RecyclerView.Adapter<ItemCommentViewHolder> {

    private final int mainColor;
    @NonNull
    private final List<FullDeckComment> comments = new ArrayList<>();
    @NonNull
    private final Account account;
    @NonNull
    private final MenuInflater menuInflater;
    @NonNull
    private final CommentDeletedListener deletedListener;
    @NonNull
    private final CommentSelectAsReplyListener selectAsReplyListener;
    @NonNull
    private final FragmentManager fragmentManager;
    @NonNull
    private final CommentEditedListener editListener;

    CardCommentsAdapter(@NonNull Context context, @NonNull Account account, @NonNull MenuInflater menuInflater, @NonNull CommentDeletedListener deletedListener, @NonNull CommentSelectAsReplyListener selectAsReplyListener, @NonNull FragmentManager fragmentManager, CommentEditedListener editListener) {
        this.account = account;
        this.menuInflater = menuInflater;
        this.deletedListener = deletedListener;
        this.selectAsReplyListener = selectAsReplyListener;
        this.fragmentManager = fragmentManager;
        this.editListener = editListener;
        this.mainColor = getSecondaryForegroundColorDependingOnTheme(context, readBrandMainColor(context));
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
    public void onBindViewHolder(@NonNull ItemCommentViewHolder holder, int position) {
        final var comment = comments.get(position);
        holder.bind(comment, account, mainColor, menuInflater, deletedListener, selectAsReplyListener, fragmentManager, (changedText) -> {
            if (!Objects.equals(changedText, comment.getComment().getMessage())) {
                DeckLog.info("Toggled checkbox in comment with localId", comment.getLocalId());
                this.editListener.onCommentEdited(comment.getLocalId(), changedText.toString());
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull ItemCommentViewHolder holder) {
        super.onViewRecycled(holder);
        holder.unbind();
    }

    @SuppressWarnings("WeakerAccess")
    public void updateComments(@NonNull List<FullDeckComment> comments) {
        this.comments.clear();
        this.comments.addAll(comments);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
