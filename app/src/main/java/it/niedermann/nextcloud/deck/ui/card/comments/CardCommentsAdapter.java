package it.niedermann.nextcloud.deck.ui.card.comments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCommentBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.Mention;
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
        setupMentions(comment.getMentions(), viewHolder.binding.message);
        viewHolder.binding.actorDisplayName.setText(comment.getActorDisplayName());
        viewHolder.binding.creationDateTime.setText(DateUtil.getRelativeDateTimeString(context, comment.getCreationDateTime().getTime()));
        TooltipCompat.setTooltipText(viewHolder.binding.creationDateTime, DateFormat.getDateTimeInstance().format(comment.getCreationDateTime()));
    }

    private void setupMentions(List<Mention> mentions, TextView tv) {
//            int count = (str.split("@" + mentions.size(), -1).length) - 1;
        new Thread(() -> {
            Map<Mention, Bitmap> mentionAvatarMap = new HashMap<>(mentions.size());
            CountDownLatch responseWaiter = new CountDownLatch(mentionAvatarMap.size());
            for (Mention m : mentions) {
                Glide.with(context)
                        .asBitmap()
                        .load(account.getUrl() + "/index.php/avatar/" + m.getMentionId() + "/64?v=4")
                        .apply(RequestOptions.circleCropTransform())
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                mentionAvatarMap.put(m, resource);
                                responseWaiter.countDown();
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            }
            try {
                responseWaiter.await();
                SpannableString string = new SpannableString(tv.getText());
                for (Mention m : mentionAvatarMap.keySet()) {
                    if (tv.getText().toString().contains("@" + m.getMentionId())) {
                        Bitmap avatar = mentionAvatarMap.get(m);
                        if (avatar == null) {
                            avatar = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_person_grey600_24dp);
                        }
                        string.setSpan(new ImageSpan(context, avatar), tv.getText().toString().indexOf("@" + m.getMentionId()), tv.getText().toString().indexOf("@" + m.getMentionId()) + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv.setText(string);
                    }
                    tv.invalidate();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
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
