package it.niedermann.nextcloud.deck.ui.card;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCardDefaultBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import scheme.Scheme;

public class DefaultCardViewHolder extends AbstractCardViewHolder {
    private final ItemCardDefaultBinding binding;
    private final int maxCoverImagesCount;

    @SuppressWarnings("WeakerAccess")
    public DefaultCardViewHolder(@NonNull ItemCardDefaultBinding binding, int maxCoverImagesCount) {
        super(binding.getRoot());
        this.binding = binding;
        this.maxCoverImagesCount = maxCoverImagesCount;
    }

    /**
     * Removes all {@link OnClickListener} and {@link OnLongClickListener}
     */
    @Override
    public void bind(@NonNull FullCard fullCard, @NonNull Account account, @Nullable Long boardRemoteId, boolean hasEditPermission, @MenuRes int optionsMenu, @NonNull CardOptionsItemSelectedListener optionsItemsSelectedListener, @NonNull String counterMaxValue, @NonNull Scheme scheme) {
        super.bind(fullCard, account, boardRemoteId, hasEditPermission, optionsMenu, optionsItemsSelectedListener, counterMaxValue, scheme);

        final var context = itemView.getContext();

        if (fullCard.getAssignedUsers() != null && fullCard.getAssignedUsers().size() > 0) {
            binding.overlappingAvatars.setAvatars(account, fullCard.getAssignedUsers());
            binding.overlappingAvatars.setVisibility(View.VISIBLE);
        } else {
            binding.overlappingAvatars.setVisibility(View.GONE);
        }

        setupCoverImages(account, binding.coverImages, fullCard, maxCoverImagesCount);

        final int attachmentsCount = fullCard.getAttachments().size();
        if (attachmentsCount == 0) {
            binding.cardCountAttachments.setVisibility(View.GONE);
        } else {
            setupCounter(binding.cardCountAttachments, counterMaxValue, attachmentsCount);
            binding.cardCountAttachments.setVisibility(View.VISIBLE);
        }

        final int commentsCount = fullCard.getCommentCount();
        if (commentsCount == 0) {
            binding.cardCountComments.setVisibility(View.GONE);
        } else {
            setupCounter(binding.cardCountComments, counterMaxValue, commentsCount);

            binding.cardCountComments.setVisibility(View.VISIBLE);
        }

        final var labels = fullCard.getLabels();
        if (labels != null && labels.size() > 0) {
            binding.labels.updateLabels(labels);
            binding.labels.setVisibility(View.VISIBLE);
        } else {
            binding.labels.removeAllViews();
            binding.labels.setVisibility(View.GONE);
        }

        final var taskStatus = fullCard.getCard().getTaskStatus();
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
}