package it.niedermann.nextcloud.deck.ui.card;

import android.content.ClipData;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Contract;

import java.util.List;

import it.niedermann.android.crosstabdnd.DraggedItemLocalState;
import it.niedermann.nextcloud.deck.DeckLog;
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
    public CardViewHolder(ItemCardBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull FullCard fullCard, @NonNull CardAdapter cardAdapter, int position, @NonNull Account account, long boardLocalId, @Nullable Long boardRemoteId, boolean hasEditPermission, @Nullable SelectCardListener selectCardListener, @MenuRes int optionsMenu, @NonNull CardOptionsItemSelectedListener optionsItemsSelectedListener, @NonNull String counterMaxValue, @ColorInt int mainColor) {
        final Context context = itemView.getContext();
        binding.card.setOnClickListener((v) -> {
            if (selectCardListener == null) {
                context.startActivity(EditActivity.createEditCardIntent(context, account, boardLocalId, fullCard.getLocalId()));
            } else {
                selectCardListener.onCardSelected(fullCard);
            }
        });
        if (hasEditPermission && selectCardListener == null) {
            binding.card.setOnLongClickListener((v) -> {
                DeckLog.log("Starting drag and drop");
                v.startDrag(ClipData.newPlainText("cardid", String.valueOf(fullCard.getLocalId())),
                        new View.DragShadowBuilder(v),
                        new DraggedItemLocalState<>(fullCard, binding.card, cardAdapter, position),
                        0
                );
                return true;
            });
        } else {
            binding.cardMenu.setVisibility(View.GONE);
        }
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

        binding.cardMenu.setOnClickListener(v -> onOverflowIconClicked(optionsMenu, v, fullCard, account, boardRemoteId, optionsItemsSelectedListener));
    }

    private void onOverflowIconClicked(@MenuRes int optionsMenu, @NonNull View view, FullCard fullCard, @NonNull Account account, @Nullable Long boardRemoteId, @NonNull CardOptionsItemSelectedListener optionsItemsSelectedListener) {
        final Context context = view.getContext();
        final PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(optionsMenu);
        prepareOptionsMenu(popup.getMenu(), fullCard, account, boardRemoteId);

        popup.setOnMenuItemClickListener(item -> optionsItemsSelectedListener.onCardOptionsItemSelected(item, fullCard));
        popup.show();
    }

    private void prepareOptionsMenu(Menu menu, @NonNull FullCard card, @NonNull Account account, @Nullable Long boardRemoteId) {
        if (containsUser(card.getAssignedUsers(), account.getUserName())) {
            menu.removeItem(menu.findItem(R.id.action_card_assign).getItemId());
        } else {
            menu.removeItem(menu.findItem(R.id.action_card_unassign).getItemId());
        }
        if (boardRemoteId == null || card.getCard().getId() == null) {
            menu.removeItem(R.id.share_link);
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

    private void setupCounter(@NonNull TextView textView, @NonNull String counterMaxValue, int count) {
        if (count > 99) {
            textView.setText(counterMaxValue);
        } else if (count > 1) {
            textView.setText(String.valueOf(count));
        } else if (count == 1) {
            textView.setText("");
        }
    }

    private void setupDueDate(@NonNull TextView cardDueDate, @NonNull Card card) {
        final Context context = cardDueDate.getContext();
        cardDueDate.setText(DateUtil.getRelativeDateTimeString(context, card.getDueDate().getTime()));
        ViewUtil.themeDueDate(context, cardDueDate, card.getDueDate());
    }

    public interface CardOptionsItemSelectedListener {
        boolean onCardOptionsItemSelected(@NonNull MenuItem menuItem, @NonNull FullCard fullCard);
    }

}