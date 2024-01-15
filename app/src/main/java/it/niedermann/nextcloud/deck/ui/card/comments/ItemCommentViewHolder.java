package it.niedermann.nextcloud.deck.ui.card.comments;

import android.text.method.LinkMovementMethod;
import android.view.MenuInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nextcloud.android.common.ui.theme.utils.ColorRole;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.function.Consumer;

import it.niedermann.android.util.ClipboardUtil;
import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCommentBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.ocs.comment.full.FullDeckComment;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.util.DateUtil;

public class ItemCommentViewHolder extends RecyclerView.ViewHolder {
    private final ItemCommentBinding binding;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

    @SuppressWarnings("WeakerAccess")
    public ItemCommentViewHolder(ItemCommentBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        this.binding.message.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void bind(@NonNull FullDeckComment comment, @NonNull Account account, @Nullable ThemeUtils utils, @NonNull MenuInflater inflater, @NonNull CommentDeletedListener deletedListener, @NonNull CommentSelectAsReplyListener selectAsReplyListener, @NonNull FragmentManager fragmentManager, @NonNull Consumer<CharSequence> editListener) {
        Glide.with(binding.avatar.getContext())
                .load(account.getAvatarUrl(DimensionUtil.INSTANCE.dpToPx(binding.avatar.getContext(), R.dimen.avatar_size), comment.getComment().getActorId()))
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_person_grey600_24dp)
                .error(R.drawable.ic_person_grey600_24dp)
                .into(binding.avatar);

        final var mentions = new HashMap<String, String>(comment.getComment().getMentions().size());
        for (final var mention : comment.getComment().getMentions()) {
            mentions.put(mention.getMentionId(), mention.getMentionDisplayName());
        }
        binding.message.setText(comment.getComment().getMessage());
        binding.message.setMarkdownStringAndHighlightMentions(comment.getComment().getMessage(), mentions);
        binding.message.setMarkdownStringChangedListener(editListener);
        binding.actorDisplayName.setText(comment.getComment().getActorDisplayName());
        binding.creationDateTime.setText(DateUtil.getRelativeDateTimeString(binding.creationDateTime.getContext(), comment.getComment().getCreationDateTime().toEpochMilli()));

        itemView.setOnClickListener(View::showContextMenu);
        itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            inflater.inflate(R.menu.comment_menu, menu);
            menu.findItem(android.R.id.copy).setOnMenuItemClickListener(item -> ClipboardUtil.INSTANCE.copyToClipboard(itemView.getContext(), comment.getComment().getMessage()));
            final var replyMenuItem = menu.findItem(R.id.reply);
            if (comment.getStatusEnum() != DBStatus.LOCAL_EDITED && account.getServerDeckVersionAsObject().supportsCommentsReplies()) {
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
                    CardCommentsEditDialogFragment.newInstance(comment.getLocalId(), comment.getComment().getMessage()).show(fragmentManager, CardCommentsEditDialogFragment.class.getCanonicalName());
                    return true;
                });
            } else {
                menu.findItem(R.id.delete).setVisible(false);
                menu.findItem(android.R.id.edit).setVisible(false);
            }
        });

        TooltipCompat.setTooltipText(binding.creationDateTime, comment.getComment().getCreationDateTime().atZone(ZoneId.systemDefault()).format(dateFormatter));
        if (utils != null) {
            utils.platform.colorImageView(binding.notSyncedYet, ColorRole.PRIMARY);
        }
        binding.notSyncedYet.setVisibility(DBStatus.LOCAL_EDITED.equals(comment.getStatusEnum()) ? View.VISIBLE : View.GONE);

        if (comment.getParent() == null) {
            binding.parentContainer.setVisibility(View.GONE);
        } else {
            final int commentParentMaxLines = itemView.getContext().getResources().getInteger(R.integer.comment_parent_max_lines);
            binding.parentContainer.setVisibility(View.VISIBLE);
            if (utils != null) {
                utils.platform.colorViewBackground(binding.parentBorder);
            }
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

    public void unbind() {
        binding.message.setText("");
        binding.message.setMarkdownStringChangedListener(null);
    }
}