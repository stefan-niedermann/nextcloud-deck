package it.niedermann.nextcloud.deck.ui.card.comments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCommentBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.Mention;
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.DimensionUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static it.niedermann.nextcloud.deck.Application.readBrandMainColor;
import static it.niedermann.nextcloud.deck.ui.branding.BrandedActivity.getSecondaryForegroundColorDependingOnTheme;
import static it.niedermann.nextcloud.deck.util.ClipboardUtil.copyToClipboard;

public class CardCommentsAdapter extends RecyclerView.Adapter<ItemCommentViewHolder> {

    private int mainColor;
    @NonNull
    private final List<DeckComment> comments = new ArrayList<>();
    @NonNull
    private final Account account;
    @NonNull
    private final MenuInflater menuInflater;
    @NonNull
    private final CommentDeletedListener commentDeletedListener;
    @NonNull
    private final FragmentManager fragmentManager;

    CardCommentsAdapter(@NonNull Context context, @NonNull Account account, @NonNull MenuInflater menuInflater, @NonNull CommentDeletedListener commentDeletedListener, @NonNull FragmentManager fragmentManager) {
        this.account = account;
        this.menuInflater = menuInflater;
        this.commentDeletedListener = commentDeletedListener;
        this.fragmentManager = fragmentManager;
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
        final Context context = holder.itemView.getContext();
        final DeckComment comment = comments.get(position);

        ViewUtil.addAvatar(holder.binding.avatar, account.getUrl(), comment.getActorId(), DimensionUtil.dpToPx(context, R.dimen.icon_size_details), R.drawable.ic_person_grey600_24dp);
        holder.binding.message.setText(comment.getMessage());
        holder.binding.actorDisplayName.setText(comment.getActorDisplayName());
        holder.binding.creationDateTime.setText(DateUtil.getRelativeDateTimeString(context, comment.getCreationDateTime().getTime()));
        holder.itemView.setOnClickListener(View::showContextMenu);
        holder.itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            menuInflater.inflate(R.menu.comment_menu, menu);
            menu.findItem(android.R.id.copy).setOnMenuItemClickListener(item -> copyToClipboard(context, comment.getMessage()));
            if (account.getUserName().equals(comment.getActorId())) {
                menu.findItem(R.id.delete).setOnMenuItemClickListener(item -> {
                    commentDeletedListener.onCommentDeleted(comment.getLocalId());
                    return true;
                });
                menu.findItem(android.R.id.edit).setOnMenuItemClickListener(item -> {
                    CardCommentsEditDialogFragment.newInstance(comment.getLocalId(), comment.getMessage()).show(fragmentManager, CardCommentsAdapter.class.getCanonicalName());
                    return true;
                });
            } else {
                menu.findItem(R.id.delete).setVisible(false);
                menu.findItem(android.R.id.edit).setVisible(false);
            }
        });

        DrawableCompat.setTint(holder.binding.notSyncedYet.getDrawable(), mainColor);
        holder.binding.notSyncedYet.setVisibility(DBStatus.LOCAL_EDITED.equals(comment.getStatusEnum()) ? View.VISIBLE : View.GONE);

        TooltipCompat.setTooltipText(holder.binding.creationDateTime, DateFormat.getDateTimeInstance().format(comment.getCreationDateTime()));
        setupMentions(comment.getMentions(), holder.binding.message);
    }

    private void setupMentions(List<Mention> mentions, TextView tv) {
        Context context = tv.getContext();
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
                    .load(account.getUrl() + "/index.php/avatar/" + messageBuilder.subSequence(spanStart + 1, spanEnd).toString() + "/" + DimensionUtil.dpToPx(context, R.dimen.icon_size_details))
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

    @SuppressWarnings("WeakerAccess")
    public void updateComments(@NonNull List<DeckComment> comments) {
        this.comments.clear();
        this.comments.addAll(comments);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
