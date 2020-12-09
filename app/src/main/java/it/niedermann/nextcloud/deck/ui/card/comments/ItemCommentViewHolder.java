package it.niedermann.nextcloud.deck.ui.card.comments;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import it.niedermann.android.util.ClipboardUtil;
import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCommentBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.ocs.comment.full.FullDeckComment;
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static it.niedermann.nextcloud.deck.util.ViewUtil.setupMentions;

public class ItemCommentViewHolder extends RecyclerView.ViewHolder {
    private final ItemCommentBinding binding;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

    @SuppressWarnings("WeakerAccess")
    public ItemCommentViewHolder(ItemCommentBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull FullDeckComment comment, @NonNull Account account, @ColorInt int mainColor, @NonNull MenuInflater inflater, @NonNull CommentDeletedListener deletedListener, @NonNull CommentSelectAsReplyListener selectAsReplyListener, @NonNull FragmentManager fragmentManager) {
        ViewUtil.addAvatar(binding.avatar, account.getUrl(), comment.getComment().getActorId(), DimensionUtil.INSTANCE.dpToPx(binding.avatar.getContext(), R.dimen.icon_size_details), R.drawable.ic_person_grey600_24dp);
        binding.message.setText(comment.getComment().getMessage());
        binding.actorDisplayName.setText(comment.getComment().getActorDisplayName());
        binding.creationDateTime.setText(DateUtil.getRelativeDateTimeString(binding.creationDateTime.getContext(), comment.getComment().getCreationDateTime().toEpochMilli()));
        itemView.setOnClickListener(View::showContextMenu);

        itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            inflater.inflate(R.menu.comment_menu, menu);
            menu.findItem(android.R.id.copy).setOnMenuItemClickListener(item -> ClipboardUtil.INSTANCE.copyToClipboard(itemView.getContext(), comment.getComment().getMessage()));
            final MenuItem replyMenuItem = menu.findItem(R.id.reply);
            if (comment.getStatusEnum() != DBStatus.LOCAL_EDITED && account.getServerDeckVersionAsObject().supportsCommentsReplys()) {
                replyMenuItem.setOnMenuItemClickListener(item -> {
                    selectAsReplyListener.onSelectAsReply(comment);
                    return true;
                });
                replyMenuItem.setVisible(true);
            } else {
                replyMenuItem.setVisible(false);
            }
            if (account.getUserName().equals(comment.getComment().getActorId())) {
                menu.findItem(R.id.delete).setOnMenuItemClickListener(item -> {
                    deletedListener.onCommentDeleted(comment.getLocalId());
                    return true;
                });
                menu.findItem(android.R.id.edit).setOnMenuItemClickListener(item -> {
                    CardCommentsEditDialogFragment.newInstance(comment.getLocalId(), comment.getComment().getMessage()).show(fragmentManager, CardCommentsAdapter.class.getCanonicalName());
                    return true;
                });
            } else {
                menu.findItem(R.id.delete).setVisible(false);
                menu.findItem(android.R.id.edit).setVisible(false);
            }
        });

        DrawableCompat.setTint(binding.notSyncedYet.getDrawable(), mainColor);
        binding.notSyncedYet.setVisibility(DBStatus.LOCAL_EDITED.equals(comment.getStatusEnum()) ? View.VISIBLE : View.GONE);

        TooltipCompat.setTooltipText(binding.creationDateTime, comment.getComment().getCreationDateTime().atZone(ZoneId.systemDefault()).format(dateFormatter));
        setupMentions(account, comment.getComment().getMentions(), binding.message);

        if (comment.getParent() == null) {
            binding.parentContainer.setVisibility(View.GONE);
        } else {
            final int commentParentMaxLines = itemView.getContext().getResources().getInteger(R.integer.comment_parent_max_lines);
            binding.parentContainer.setVisibility(View.VISIBLE);
            binding.parentBorder.setBackgroundColor(mainColor);
            binding.parent.setText(comment.getParent().getMessage());
            binding.parent.setOnClickListener((v) -> {
                final boolean previouslyCollapsed = binding.parent.getMaxLines() == commentParentMaxLines;
                // TODO animation crashs
//                binding.parent.setEllipsize(previouslyExpanded ? TextUtils.TruncateAt.END : null);
//                ObjectAnimator.ofInt(binding.parent, "maxLines", previouslyCollapsed ? 1000 : commentParentMaxLines)
//                        .start();
                binding.parent.setMaxLines(previouslyCollapsed ? Integer.MAX_VALUE : commentParentMaxLines);
            });
        }
    }
}