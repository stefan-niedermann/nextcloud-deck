package it.niedermann.nextcloud.deck.ui.card;

import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.Contract;

import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCardBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class CardViewHolder extends RecyclerView.ViewHolder {
    private ItemCardBinding binding;

    @SuppressWarnings("WeakerAccess")
    public CardViewHolder(@NonNull ItemCardBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    /**
     * Removes all {@link OnClickListener} and {@link OnLongClickListener}
     */
    public void bind(@NonNull FullCard fullCard, @NonNull Account account, @Nullable Long boardRemoteId, boolean hasEditPermission, @MenuRes int optionsMenu, @NonNull CardOptionsItemSelectedListener optionsItemsSelectedListener, @NonNull String counterMaxValue, @ColorInt int mainColor) {
        final Context context = itemView.getContext();

        bindCardClickListener(null);
        bindCardLongClickListener(null);
        binding.cardMenu.setVisibility(hasEditPermission ? View.VISIBLE : View.GONE);
        binding.cardTitle.setText(fullCard.getCard().getTitle().trim());

        if (fullCard.getAssignedUsers() != null && fullCard.getAssignedUsers().size() > 0) {
            binding.overlappingAvatars.setAvatars(account, fullCard.getAssignedUsers());
            binding.overlappingAvatars.setVisibility(View.VISIBLE);
        } else {
            binding.overlappingAvatars.setVisibility(View.GONE);
        }

        DrawableCompat.setTint(binding.notSyncedYet.getDrawable(), mainColor);
        binding.notSyncedYet.setVisibility(DBStatus.LOCAL_EDITED.equals(fullCard.getStatusEnum()) ? View.VISIBLE : View.GONE);

        if (fullCard.getCard().getDueDate() != null) {
            setupDueDate(binding.cardDueDate, fullCard.getCard());
            binding.cardDueDate.setVisibility(View.VISIBLE);
        } else {
            binding.cardDueDate.setVisibility(View.GONE);
        }

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

        List<Label> labels = fullCard.getLabels();
        if (labels != null && labels.size() > 0) {
            binding.labels.updateLabels(labels);
            binding.labels.setVisibility(View.VISIBLE);
        } else {
            binding.labels.removeAllViews();
            binding.labels.setVisibility(View.GONE);
        }

        Card.TaskStatus taskStatus = fullCard.getCard().getTaskStatus();
        if (taskStatus.taskCount > 0) {
            binding.cardCountTasks.setText(context.getResources().getString(R.string.task_count, String.valueOf(taskStatus.doneCount), String.valueOf(taskStatus.taskCount)));
            binding.cardCountTasks.setVisibility(View.VISIBLE);
        } else {
            binding.cardCountTasks.setVisibility(View.GONE);
        }

        binding.cardMenu.setOnClickListener(view -> {
            final PopupMenu popup = new PopupMenu(context, view);
            popup.inflate(optionsMenu);
            final Menu menu = popup.getMenu();
            if (containsUser(fullCard.getAssignedUsers(), account.getUserName())) {
                menu.removeItem(menu.findItem(R.id.action_card_assign).getItemId());
            } else {
                menu.removeItem(menu.findItem(R.id.action_card_unassign).getItemId());
            }
            if (boardRemoteId == null || fullCard.getCard().getId() == null) {
                menu.removeItem(R.id.share_link);
            }

            popup.setOnMenuItemClickListener(item -> optionsItemsSelectedListener.onCardOptionsItemSelected(item, fullCard));
            popup.show();
        });
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

    private static void setupDueDate(@NonNull TextView cardDueDate, @NonNull Card card) {
        final Context context = cardDueDate.getContext();
        cardDueDate.setText(DateUtil.getRelativeDateTimeString(context, card.getDueDate().getTime()));
        ViewUtil.themeDueDate(context, cardDueDate, card.getDueDate());
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