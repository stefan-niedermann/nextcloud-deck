package it.niedermann.nextcloud.deck.ui.card;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.niedermann.android.crosstabdnd.DragAndDropAdapter;
import it.niedermann.android.crosstabdnd.DraggedItemLocalState;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCardBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper;
import it.niedermann.nextcloud.deck.ui.branding.Branded;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class CardAdapter extends RecyclerView.Adapter<ItemCardViewHolder> implements DragAndDropAdapter<FullCard>, Branded {

    protected final SyncManager syncManager;

    private final Account account;
    private final long boardId;
    private final long stackId;
    private final boolean canEdit;
    @NonNull
    private final Context context;
    @Nullable
    private final SelectCardListener selectCardListener;
    private List<FullCard> cardList = new LinkedList<>();
    private LifecycleOwner lifecycleOwner;
    private List<FullStack> availableStacks = new ArrayList<>();
    private String counterMaxValue;

    private int mainColor;

    public CardAdapter(@NonNull Context context, @NonNull Account account, long boardId, long stackId, boolean canEdit, @NonNull SyncManager syncManager, @NonNull LifecycleOwner lifecycleOwner, @Nullable SelectCardListener selectCardListener) {
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
        this.account = account;
        this.boardId = boardId;
        this.stackId = stackId;
        this.canEdit = canEdit;
        this.syncManager = syncManager;
        this.selectCardListener = selectCardListener;
        this.mainColor = context.getResources().getColor(R.color.primary);
        syncManager.getStacksForBoard(account.getId(), boardId).observe(this.lifecycleOwner, (stacks) -> {
            availableStacks.clear();
            availableStacks.addAll(stacks);
        });
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return cardList.get(position).getLocalId();
    }

    @NonNull
    @Override
    public ItemCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        final Context context = viewGroup.getContext();
        counterMaxValue = context.getString(R.string.counter_max_value);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ItemCardBinding binding = ItemCardBinding.inflate(layoutInflater, viewGroup, false);
        return new ItemCardViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemCardViewHolder viewHolder, int position) {
        final Context context = viewHolder.itemView.getContext();
        final FullCard card = cardList.get(position);

        viewHolder.binding.card.setOnClickListener((v) -> {
            if (selectCardListener == null) {
                context.startActivity(EditActivity.createEditCardIntent(context, account, boardId, card.getLocalId()));
            } else {
                selectCardListener.onCardSelected(card);
            }
        });
        if (canEdit && selectCardListener == null) {
            viewHolder.binding.card.setOnLongClickListener((v) -> {
                DeckLog.log("Starting drag and drop");
                v.startDrag(ClipData.newPlainText("cardid", String.valueOf(card.getLocalId())),
                        new View.DragShadowBuilder(v),
                        new DraggedItemLocalState<>(card, viewHolder.binding.card, this, position),
                        0
                );
                return true;
            });
        } else {
            viewHolder.binding.cardMenu.setVisibility(View.GONE);
        }
        viewHolder.binding.cardTitle.setText(card.getCard().getTitle().trim());

        if (card.getAssignedUsers() != null && card.getAssignedUsers().size() > 0) {
            viewHolder.binding.overlappingAvatars.setAvatars(account, card.getAssignedUsers());
            viewHolder.binding.overlappingAvatars.setVisibility(View.VISIBLE);
        } else {
            viewHolder.binding.overlappingAvatars.setVisibility(View.GONE);
        }

        DrawableCompat.setTint(viewHolder.binding.notSyncedYet.getDrawable(), mainColor);
        viewHolder.binding.notSyncedYet.setVisibility(DBStatus.LOCAL_EDITED.equals(card.getStatusEnum()) ? View.VISIBLE : View.GONE);

        if (card.getCard().getDueDate() != null) {
            setupDueDate(viewHolder.binding.cardDueDate, card.getCard());
            viewHolder.binding.cardDueDate.setVisibility(View.VISIBLE);
        } else {
            viewHolder.binding.cardDueDate.setVisibility(View.GONE);
        }

        final int attachmentsCount = card.getAttachments().size();

        if (attachmentsCount == 0) {
            viewHolder.binding.cardCountAttachments.setVisibility(View.GONE);
        } else {
            setupCounter(viewHolder.binding.cardCountAttachments, attachmentsCount);
            viewHolder.binding.cardCountAttachments.setVisibility(View.VISIBLE);
        }

        final int commentsCount = card.getCommentCount();

        if (commentsCount == 0) {
            viewHolder.binding.cardCountComments.setVisibility(View.GONE);
        } else {
            setupCounter(viewHolder.binding.cardCountComments, commentsCount);

            viewHolder.binding.cardCountComments.setVisibility(View.VISIBLE);
        }

        List<Label> labels = card.getLabels();
        if (labels != null && labels.size() > 0) {
            viewHolder.binding.labels.updateLabels(labels);
            viewHolder.binding.labels.setVisibility(View.VISIBLE);
        } else {
            viewHolder.binding.labels.removeAllViews();
            viewHolder.binding.labels.setVisibility(View.GONE);
        }

        Card.TaskStatus taskStatus = card.getCard().getTaskStatus();
        if (taskStatus.taskCount > 0) {
            viewHolder.binding.cardCountTasks.setText(context.getResources().getString(R.string.task_count, String.valueOf(taskStatus.doneCount), String.valueOf(taskStatus.taskCount)));
            viewHolder.binding.cardCountTasks.setVisibility(View.VISIBLE);
        } else {
            viewHolder.binding.cardCountTasks.setVisibility(View.GONE);
        }

        viewHolder.binding.cardMenu.setOnClickListener(v -> onOverflowIconClicked(v, card));
    }

    private void setupCounter(@NonNull TextView textView, int count) {
        if (count > 99) {
            textView.setText(counterMaxValue);
        } else if (count > 1) {
            textView.setText(String.valueOf(count));
        } else if (count == 1) {
            textView.setText("");
        }
    }

    private void setupDueDate(@NonNull TextView cardDueDate, @NotNull Card card) {
        final Context context = cardDueDate.getContext();
        cardDueDate.setText(DateUtil.getRelativeDateTimeString(context, card.getDueDate().getTime()));
        ViewUtil.themeDueDate(context, cardDueDate, card.getDueDate());
    }

    @Override
    public int getItemCount() {
        return cardList == null ? 0 : cardList.size();
    }

    public void insertItem(FullCard fullCard, int position) {
        cardList.add(position, fullCard);
        notifyItemInserted(position);
    }

    @Override
    public List<FullCard> getItemList() {
        return this.cardList;
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        cardList.add(toPosition, cardList.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void removeItem(int position) {
        cardList.remove(position);
        notifyItemRemoved(position);
    }

    protected void onOverflowIconClicked(@NotNull View view, FullCard card) {
        final Context context = view.getContext();
        final PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.card_menu);
        prepareOptionsMenu(popup.getMenu(), card);

        popup.setOnMenuItemClickListener(item -> optionsItemSelected(context, item, card));
        popup.show();
    }

    protected void prepareOptionsMenu(Menu menu, @NotNull FullCard card) {
        if (containsUser(card.getAssignedUsers(), account.getUserName())) {
            menu.removeItem(menu.findItem(R.id.action_card_assign).getItemId());
        } else {
            menu.removeItem(menu.findItem(R.id.action_card_unassign).getItemId());
        }
    }

    public void setCardList(@NonNull List<FullCard> cardList) {
        this.cardList.clear();
        this.cardList.addAll(cardList);
        notifyDataSetChanged();
    }

    @Contract("null, _ -> false")
    private boolean containsUser(List<User> userList, String username) {
        if (userList != null) {
            for (User user : userList) {
                if (user.getPrimaryKey().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean optionsItemSelected(@NonNull Context context, @NotNull MenuItem item, FullCard fullCard) {
        switch (item.getItemId()) {
            case R.id.action_card_assign: {
                new Thread(() -> syncManager.assignUserToCard(syncManager.getUserByUidDirectly(fullCard.getCard().getAccountId(), account.getUserName()), fullCard.getCard())).start();
                return true;
            }
            case R.id.action_card_unassign: {
                new Thread(() -> syncManager.unassignUserFromCard(syncManager.getUserByUidDirectly(fullCard.getCard().getAccountId(), account.getUserName()), fullCard.getCard())).start();
                return true;
            }
            case R.id.action_card_move: {
                int currentStackItem = 0;
                CharSequence[] items = new CharSequence[availableStacks.size()];
                for (int i = 0; i < availableStacks.size(); i++) {
                    final Stack stack = availableStacks.get(i).getStack();
                    items[i] = stack.getTitle();
                    if (stack.getLocalId().equals(stackId)) {
                        currentStackItem = i;
                    }
                }
                final FullCard newCard = fullCard;
                new BrandedAlertDialogBuilder(context)
                        .setSingleChoiceItems(items, currentStackItem, (dialog, which) -> {
                            dialog.cancel();
                            newCard.getCard().setStackId(availableStacks.get(which).getStack().getLocalId());
                            LiveDataHelper.observeOnce(syncManager.updateCard(newCard), lifecycleOwner, (c) -> {
                                // Nothing to do here...
                            });
                            DeckLog.log("Moved card \"" + fullCard.getCard().getTitle() + "\" to \"" + availableStacks.get(which).getStack().getTitle() + "\"");
                        })
                        .setNeutralButton(android.R.string.cancel, null)
                        .setTitle(context.getString(R.string.action_card_move_title, fullCard.getCard().getTitle()))
                        .show();
                return true;
            }
            case R.id.action_card_archive: {
                // TODO error handling
                syncManager.archiveCard(fullCard);
                return true;
            }
            case R.id.action_card_delete: {
                // TODO error handling
                syncManager.deleteCard(fullCard.getCard());
                return true;
            }
        }
        return true;
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {
        this.mainColor = BrandedActivity.getSecondaryForegroundColorDependingOnTheme(context, mainColor);
        notifyDataSetChanged();
    }
}
