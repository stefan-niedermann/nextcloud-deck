package it.niedermann.nextcloud.deck.ui.card;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCardDefaultBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Card.TaskStatus;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.util.AttachmentUtil;
import it.niedermann.nextcloud.deck.util.MimeTypeUtil;

public class DefaultCardViewHolder extends AbstractCardViewHolder {
    private final ItemCardDefaultBinding binding;

    @SuppressWarnings("WeakerAccess")
    public DefaultCardViewHolder(@NonNull ItemCardDefaultBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    /**
     * Removes all {@link OnClickListener} and {@link OnLongClickListener}
     */
    public void bind(@NonNull FullCard fullCard, @NonNull Account account, @Nullable Long boardRemoteId, boolean hasEditPermission, @MenuRes int optionsMenu, @NonNull CardOptionsItemSelectedListener optionsItemsSelectedListener, @NonNull String counterMaxValue, @ColorInt int mainColor) {
        super.bind(fullCard, account, boardRemoteId, hasEditPermission, optionsMenu, optionsItemsSelectedListener, counterMaxValue, mainColor);

        final Context context = itemView.getContext();

        if (fullCard.getAssignedUsers() != null && fullCard.getAssignedUsers().size() > 0) {
            binding.overlappingAvatars.setAvatars(account, fullCard.getAssignedUsers());
            binding.overlappingAvatars.setVisibility(View.VISIBLE);
        } else {
            binding.overlappingAvatars.setVisibility(View.GONE);
        }

        final int attachmentsCount = fullCard.getAttachments().size();
        if (attachmentsCount == 0) {
            binding.cardCountAttachments.setVisibility(View.GONE);
        } else {
            setupCounter(binding.cardCountAttachments, counterMaxValue, attachmentsCount);
            binding.cardCountAttachments.setVisibility(View.VISIBLE);
        }

        binding.coverImages.removeAllViews();
        final List<Attachment> coverImages = fullCard.getAttachments()
                .stream()
                .filter(attachment -> MimeTypeUtil.isImage(attachment.getMimetype()))
                .limit(3)
                .collect(Collectors.toList());
        binding.coverImages.setVisibility(coverImages.size() > 0 ? View.VISIBLE : View.GONE);
        binding.coverImages.setColumnCount(coverImages.size());
        for (int i = 0; i < coverImages.size(); i++) {
            DeckLog.info("Card", fullCard.getCard().getTitle(), "Attachment:", coverImages.get(i).getBasename());
            final ImageView coverImage = new ImageView(binding.coverImages.getContext());
            coverImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            binding.coverImages.addView(coverImage, new GridLayout.LayoutParams(
                    GridLayout.spec(0, GridLayout.CENTER),
                    GridLayout.spec(i, GridLayout.CENTER)));
            final int finalI = i;
            coverImage.post(() -> Glide.with(coverImage).load(AttachmentUtil.getThumbnailUrl(account, fullCard.getId(), coverImages.get(finalI), binding.coverImages.getWidth(), binding.coverImages.getHeight())).into(coverImage));
        }

        final int commentsCount = fullCard.getCommentCount();
        if (commentsCount == 0) {
            binding.cardCountComments.setVisibility(View.GONE);
        } else {
            setupCounter(binding.cardCountComments, counterMaxValue, commentsCount);

            binding.cardCountComments.setVisibility(View.VISIBLE);
        }

        final List<Label> labels = fullCard.getLabels();
        if (labels != null && labels.size() > 0) {
            binding.labels.updateLabels(labels);
            binding.labels.setVisibility(View.VISIBLE);
        } else {
            binding.labels.removeAllViews();
            binding.labels.setVisibility(View.GONE);
        }

        final TaskStatus taskStatus = fullCard.getCard().getTaskStatus();
        if (taskStatus.taskCount > 0) {
            binding.cardCountTasks.setText(context.getResources().getString(R.string.task_count, String.valueOf(taskStatus.doneCount), String.valueOf(taskStatus.taskCount)));
            binding.cardCountTasks.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_check_grey600_24dp), null, null, null);
            binding.cardCountTasks.setVisibility(View.VISIBLE);
        } else {
            final String description = fullCard.getCard().getDescription();
            if (!TextUtils.isEmpty(description)) {
                binding.cardCountTasks.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.ic_baseline_subject_24), null, null, null);
                binding.cardCountTasks.setText(null);
                binding.cardCountTasks.setVisibility(View.VISIBLE);
            } else {
                binding.cardCountTasks.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected TextView getCardDueDate() {
        return binding.cardDueDate;
    }

    @Override
    protected ImageView getNotSyncedYet() {
        return binding.notSyncedYet;
    }

    @Override
    protected TextView getCardTitle() {
        return binding.cardTitle;
    }

    @Override
    protected View getCardMenu() {
        return binding.cardMenu;
    }

    @Override
    protected MaterialCardView getCard() {
        return binding.card;
    }

    public void bindCardClickListener(@Nullable OnClickListener l) {
        binding.card.setOnClickListener(l);
    }

    public void bindCardLongClickListener(@Nullable OnLongClickListener l) {
        binding.card.setOnLongClickListener(l);
    }

    public MaterialCardView getDraggable() {
        return binding.card;
    }


    private static void setupCounter(@NonNull TextView textView, @NonNull String counterMaxValue, int count) {
        if (count > 99) {
            textView.setText(counterMaxValue);
        } else if (count > 1) {
            textView.setText(String.valueOf(count));
        } else if (count == 1) {
            textView.setText("");
        }
    }

    @Contract("null, _ -> false")
    private static boolean containsUser(List<User> userList, String username) {
        if (userList != null) {
            for (User user : userList) {
                if (user.getPrimaryKey().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }
}