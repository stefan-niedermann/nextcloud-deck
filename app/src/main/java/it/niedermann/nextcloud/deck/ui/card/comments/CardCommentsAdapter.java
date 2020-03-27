package it.niedermann.nextcloud.deck.ui.card.comments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.text.DateFormat;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCommentBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.Mention;
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static android.content.Context.CLIPBOARD_SERVICE;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static it.niedermann.nextcloud.deck.util.DimensionUtil.getAvatarDimension;

public class CardCommentsAdapter extends RecyclerView.Adapter<CardCommentsAdapter.ItemCommentViewHolder> {

    @NonNull
    private final Context context;
    @NonNull
    private final List<DeckComment> comments;
    @NonNull
    private final Account account;
    @NonNull
    private final MenuInflater menuInflater;
    @NonNull
    private final CommentDeletedListener commentDeletedListener;

    CardCommentsAdapter(@NonNull Context context, @NonNull List<DeckComment> comments, @NonNull Account account, @NonNull MenuInflater menuInflater, @NonNull CommentDeletedListener commentDeletedListener) {
        this.context = context;
        this.comments = comments;
        this.account = account;
        this.menuInflater = menuInflater;
        this.commentDeletedListener = commentDeletedListener;
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
        DeckComment comment = comments.get(position);
        ViewUtil.addAvatar(context, holder.binding.avatar, account.getUrl(), account.getUserName(), getAvatarDimension(context, R.dimen.icon_size_details), R.drawable.ic_person_grey600_24dp);
        holder.binding.message.setText(comment.getMessage());
        holder.binding.actorDisplayName.setText(comment.getActorDisplayName());
        holder.binding.creationDateTime.setText(DateUtil.getRelativeDateTimeString(context, comment.getCreationDateTime().getTime()));
        holder.binding.getRoot().setOnClickListener(View::showContextMenu);
        holder.binding.getRoot().setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            menuInflater.inflate(R.menu.comment_menu, menu);
            menu.findItem(android.R.id.copy).setOnMenuItemClickListener(item -> {
                final ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(comment.getMessage(), comment.getMessage());
                if (clipboardManager == null) {
                    Log.e(TAG, "clipboardManager is null");
                    Toast.makeText(context, R.string.could_not_copy_to_clipboard, Toast.LENGTH_SHORT).show();
                    return false;
                }
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(context, R.string.simple_copied, Toast.LENGTH_SHORT).show();
                return true;
            });
            menu.findItem(R.id.delete).setOnMenuItemClickListener(item -> {
                commentDeletedListener.onCommentDeleted(comment.getLocalId());
                return true;
            });
        });

        if (DBStatus.LOCAL_EDITED.equals(comment.getStatusEnum())) {
            holder.binding.notSyncedYet.setVisibility(View.VISIBLE);
        }

        TooltipCompat.setTooltipText(holder.binding.creationDateTime, DateFormat.getDateTimeInstance().format(comment.getCreationDateTime()));
        setupMentions(comment.getMentions(), holder.binding.message);
    }

    private void setupMentions(List<Mention> mentions, TextView tv) {
        SpannableStringBuilder messageBuilder = new SpannableStringBuilder(tv.getText());

        // Step 1
        // Add avatar icons and display names
        for (Mention m : mentions) {
            final String mentionId = "@" + m.getMentionId();
            final String mentionDisplayName = " " + m.getMentionDisplayName();
            int index = messageBuilder.toString().lastIndexOf(mentionId);
            while (index >= 0) {
                messageBuilder.setSpan(new ImageSpan(context, R.drawable.ic_person_grey600_24dp), index, index + mentionId.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                messageBuilder.insert(index + mentionId.length(), mentionDisplayName);
                index = messageBuilder.toString().substring(0, index).lastIndexOf(mentionId);
            }
        }
        tv.setText(messageBuilder);

        // Step 2
        // Replace avatar icons with real avatars
        final ImageSpan[] list = messageBuilder.getSpans(0, messageBuilder.length(), ImageSpan.class);
        for (ImageSpan span : list) {
            final int spanStart = messageBuilder.getSpanStart(span);
            final int spanEnd = messageBuilder.getSpanEnd(span);
            Glide.with(context)
                    .asBitmap()
                    .placeholder(R.drawable.ic_person_grey600_24dp)
                    .load(account.getUrl() + "/index.php/avatar/" + messageBuilder.subSequence(spanStart + 1, spanEnd).toString() + "/" + getAvatarDimension(context, R.dimen.icon_size_details))
                    .apply(RequestOptions.circleCropTransform())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            messageBuilder.removeSpan(span);
                            messageBuilder.setSpan(new ImageSpan(context, resource), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // silence is gold
                        }
                    });
        }
        tv.setText(messageBuilder);
    }


    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class ItemCommentViewHolder extends RecyclerView.ViewHolder {
        private ItemCommentBinding binding;

        private ItemCommentViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
