package it.niedermann.nextcloud.deck.ui.card;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCardBinding;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper;
import it.niedermann.nextcloud.deck.ui.EditActivity;
import it.niedermann.nextcloud.deck.ui.helper.dnd.DraggedCardLocalState;
import it.niedermann.nextcloud.deck.util.ColorUtil;
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.DimensionUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ItemCardViewHolder> {

    private static final String TAG = CardAdapter.class.getCanonicalName();

    //    public static final int REQUEST_CODE_START_EDIT_ACTIVITY = 100;
    public static final String BUNDLE_KEY_ACCOUNT = "account";
    public static final String BUNDLE_KEY_ACCOUNT_ID = "accountId";
    public static final String BUNDLE_KEY_LOCAL_ID = "localId";
    public static final String BUNDLE_KEY_BOARD_ID = "boardId";
    public static final String BUNDLE_KEY_STACK_ID = "stackId";
    public static final String BUNDLE_KEY_CAN_EDIT = "canEdit";
    public static final Long NO_LOCAL_ID = -1L;

    private Context context;
    private List<FullCard> cardList = new LinkedList<>();
    private SingleSignOnAccount account;
    private final SyncManager syncManager;
    private final long boardId;
    private final long stackId;
    private final boolean canEdit;
    private LifecycleOwner lifecycleOwner;
    private List<FullStack> availableStacks = new ArrayList<>();

    @Nullable
    private final SelectCardListener selectCardListener;

    private int maxAvatarCount;
    private int maxLabelsShown;
    private int maxLabelsChars;

    public CardAdapter(long boardId, long stackId, boolean canEdit, @NonNull SyncManager syncManager, @NonNull Fragment fragment) {
        this(boardId, stackId, canEdit, syncManager, fragment, null);
    }

    public CardAdapter(long boardId, long stackId, boolean canEdit, @NonNull SyncManager syncManager, @NonNull Fragment fragment, @Nullable SelectCardListener selectCardListener) {
        this.lifecycleOwner = fragment;
        this.boardId = boardId;
        this.stackId = stackId;
        this.canEdit = canEdit;
        this.syncManager = syncManager;
        this.selectCardListener = selectCardListener;
    }

    @NonNull
    @Override
    public ItemCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        context = viewGroup.getContext();

        maxAvatarCount = context.getResources().getInteger(R.integer.max_avatar_count);
        maxLabelsShown = context.getResources().getInteger(R.integer.max_labels_shown);
        maxLabelsChars = context.getResources().getInteger(R.integer.max_labels_chars);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ItemCardBinding binding = ItemCardBinding.inflate(layoutInflater, viewGroup, false);
        try {
            account = SingleAccountHelper.getCurrentSingleSignOnAccount(context);
        } catch (NextcloudFilesAppAccountNotFoundException e) {
            DeckLog.logError(e);
        } catch (NoCurrentAccountSelectedException e) {
            DeckLog.logError(e);
        }
        return new ItemCardViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemCardViewHolder viewHolder, int position) {
        FullCard card = cardList.get(position);

        viewHolder.binding.card.setOnClickListener((View clickedView) -> {
            if (selectCardListener == null) {
                Intent intent = new Intent(clickedView.getContext(), EditActivity.class);
                intent.putExtra(BUNDLE_KEY_ACCOUNT_ID, card.getAccountId());
                intent.putExtra(BUNDLE_KEY_BOARD_ID, boardId);
                intent.putExtra(BUNDLE_KEY_LOCAL_ID, card.getLocalId());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                selectCardListener.onCardSelected(card);
            }
        });
        if (canEdit && selectCardListener == null) {
            viewHolder.binding.card.setOnLongClickListener((View draggedView) -> {
                ClipData dragData = ClipData.newPlainText("cardid", card.getLocalId() + "");

                // Starts the drag
                draggedView.startDrag(dragData,  // the data to be dragged
                        new View.DragShadowBuilder(draggedView),  // the drag shadow builder
                        new DraggedCardLocalState(card, viewHolder.binding.card, this, position),      // no need to use local data
                        0          // flags (not currently used, set to 0)
                );
                viewHolder.binding.card.setVisibility(View.INVISIBLE);
                DeckLog.log("onLongClickListener");
                return true;
            });
            setupMoveMenu(card.getAccountId(), boardId);
        } else {
            viewHolder.binding.cardMenu.setVisibility(View.GONE);
        }
        viewHolder.binding.cardTitle.setText(card.getCard().getTitle());
        String description = card.getCard().getDescription();
        if (description != null && description.length() > 0) {
            viewHolder.binding.cardDescription.setText(card.getCard().getDescription());
            viewHolder.binding.cardDescription.setVisibility(View.VISIBLE);
        } else {
            viewHolder.binding.cardDescription.setVisibility(View.GONE);
        }

        boolean showDetails = false;

        if (card.getAssignedUsers() != null && card.getAssignedUsers().size() > 0 && account.url != null) {
            setupAvatars(viewHolder.binding.peopleList, card);
            viewHolder.binding.peopleList.setVisibility(View.VISIBLE);
            showDetails = true;
        } else {
            viewHolder.binding.peopleList.setVisibility(View.GONE);
        }

        if (DBStatus.LOCAL_EDITED.equals(card.getStatusEnum())) {
            viewHolder.binding.notSyncedYet.setVisibility(View.VISIBLE);
        }

        if (card.getCard().getDueDate() != null) {
            setupDueDate(viewHolder.binding.cardDueDate, card.getCard());
            viewHolder.binding.cardDueDate.setVisibility(View.VISIBLE);
            showDetails = true;
        } else {
            viewHolder.binding.cardDueDate.setVisibility(View.GONE);
        }

        final int attachmentsCount = card.getAttachments().size();

        if (attachmentsCount == 0) {
            viewHolder.binding.cardCountAttachments.setVisibility(View.GONE);
        } else {
            setupCounter(viewHolder.binding.cardCountAttachments, attachmentsCount);

            viewHolder.binding.cardCountAttachments.setVisibility(View.VISIBLE);
            showDetails = true;
        }

        final int commentsCount = card.getCommentCount();

        if (commentsCount == 0) {
            viewHolder.binding.cardCountComments.setVisibility(View.GONE);
        } else {
            setupCounter(viewHolder.binding.cardCountComments, commentsCount);

            viewHolder.binding.cardCountComments.setVisibility(View.VISIBLE);
            showDetails = true;
        }

        viewHolder.binding.labels.removeAllViews();
        if (card.getLabels() != null && card.getLabels().size() > 0) {
            setupLabels(viewHolder.binding.labels, card.getLabels());
            viewHolder.binding.labels.setVisibility(View.VISIBLE);
            showDetails = true;
        } else {
            viewHolder.binding.labels.setVisibility(View.GONE);
        }

        if (showDetails) {
            viewHolder.binding.cardDetailsContainer.setVisibility(View.VISIBLE);
        } else {
            viewHolder.binding.cardDetailsContainer.setVisibility(View.GONE);
        }

        viewHolder.binding.cardMenu.setOnClickListener(v -> onOverflowIconClicked(v, card));
    }

    private void setupMoveMenu(long accountId, long boardId) {
        syncManager.getStacksForBoard(accountId, boardId).observe(lifecycleOwner, (stacks) -> availableStacks = stacks);
    }

    private void setupLabels(@NonNull ChipGroup labels, List<Label> labelList) {
        Chip chip;
        for (int i = 0; i < labelList.size(); i++) {
            if (i > maxLabelsShown - 1 && labelList.size() > maxLabelsShown) {
                chip = new Chip(context);
                chip.setChipIcon(ContextCompat.getDrawable(context, R.drawable.ic_more_horiz_black_24dp));
                chip.setEnsureMinTouchTargetSize(false);
                chip.setTextStartPadding(0);
                chip.setTextEndPadding(0);
                labels.addView(chip);
                break;
            }
            Label label = labelList.get(i);
            chip = new Chip(context);
            chip.setEnsureMinTouchTargetSize(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                chip.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            String labelTitle = label.getTitle();
            if (labelTitle.length() > maxLabelsChars - 1) {
                chip.setText(labelTitle.substring(0, maxLabelsChars));
            } else {
                chip.setText(labelTitle.substring(0, 1));
            }

            try {
                int labelColor = Color.parseColor("#" + label.getColor());
                ColorStateList c = ColorStateList.valueOf(labelColor);
                chip.setChipBackgroundColor(c);
                chip.setTextColor(ColorUtil.getForegroundColorForBackgroundColor(labelColor));
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "error parsing label color", e);
            }

            labels.addView(chip);
        }
    }

    private void setupCounter(@NonNull TextView textView, int count) {
        if (count > 99) {
            textView.setText(context.getString(R.string.counter_max_value));
        } else if (count > 1) {
            textView.setText(String.valueOf(count));
        } else if (count == 1) {
            textView.setText("");
        }
    }

    private void setupDueDate(@NonNull TextView cardDueDate, Card card) {
        cardDueDate.setText(DateUtil.getRelativeDateTimeString(this.context, card.getDueDate().getTime()));
        ViewUtil.themeDueDate(this.context, cardDueDate, card.getDueDate());
    }

    private void setupAvatars(@NonNull RelativeLayout peopleList, FullCard card) {
        int avatarSize = DimensionUtil.getAvatarDimension(context, R.dimen.avatar_size_small);
        peopleList.removeAllViews();
        RelativeLayout.LayoutParams avatarLayoutParams;
        int avatarCount;
        for (avatarCount = 0; avatarCount < card.getAssignedUsers().size() && avatarCount < maxAvatarCount; avatarCount++) {
            avatarLayoutParams = new RelativeLayout.LayoutParams(avatarSize, avatarSize);
            avatarLayoutParams.setMargins(0, 0, avatarCount * context.getResources().getDimensionPixelSize(R.dimen.avatar_overlapping_small), 0);
            avatarLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            ImageView avatar = new ImageView(context);
            avatar.setLayoutParams(avatarLayoutParams);
            peopleList.addView(avatar);
            avatar.requestLayout();
            ViewUtil.addAvatar(context, avatar, account.url, card.getAssignedUsers().get(avatarCount).getUid(), avatarSize, R.drawable.ic_person_grey600_24dp);
        }

        // Recalculate container size based on avatar count
        int size = context.getResources().getDimensionPixelSize(R.dimen.avatar_overlapping_small) * (avatarCount - 1) + avatarSize;
        ViewGroup.LayoutParams rememberParam = peopleList.getLayoutParams();
        rememberParam.width = size;
        peopleList.setLayoutParams(rememberParam);
    }

    @Override
    public int getItemCount() {
        return cardList == null ? 0 : cardList.size();
    }

    public void setCardList(@NonNull List<FullCard> cardList) {
        this.cardList = cardList;
        notifyDataSetChanged();
    }

    public FullCard getItem(int position) {
        return cardList.get(position);
    }

    public void addItem(FullCard fullCard, int position) {
        cardList.add(position, fullCard);
    }

    public void moveItem(int fromPosition, int toPosition) {
        cardList.add(toPosition, cardList.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }

    public void removeItem(int position) {
        cardList.remove(position);
        notifyItemRemoved(position);
    }

    private void onOverflowIconClicked(View view, FullCard card) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.card_menu);
        prepareOptionsMenu(popup.getMenu(), card);

        popup.setOnMenuItemClickListener(item -> optionsItemSelected(item, card));
        popup.show();
    }

    private void prepareOptionsMenu(Menu menu, FullCard card) {
        if (containsUser(card.getAssignedUsers(), account.userId)) {
            menu.removeItem(menu.findItem(R.id.action_card_assign).getItemId());
        } else {
            menu.removeItem(menu.findItem(R.id.action_card_unassign).getItemId());
        }
    }

    public List<FullCard> getCardList() {
        return cardList;
    }

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

    private boolean optionsItemSelected(MenuItem item, FullCard card) {
        switch (item.getItemId()) {
            case R.id.action_card_assign: {
                try {
                    SingleSignOnAccount ssoa = SingleAccountHelper.getCurrentSingleSignOnAccount(context);
                    new Thread(() -> syncManager.assignUserToCard(syncManager.getUserByUidDirectly(card.getCard().getAccountId(), ssoa.userId), card.getCard())).start();
                } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
                    DeckLog.logError(e);
                }
                return true;
            }
            case R.id.action_card_unassign: {
                try {
                    SingleSignOnAccount ssoa = SingleAccountHelper.getCurrentSingleSignOnAccount(context);
                    new Thread(() -> syncManager.unassignUserFromCard(syncManager.getUserByUidDirectly(card.getCard().getAccountId(), ssoa.userId), card.getCard())).start();
                } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
                    DeckLog.logError(e);
                }
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
                final FullCard newCard = card;
                new AlertDialog.Builder(context)
                        .setSingleChoiceItems(items, currentStackItem, (dialog, which) -> {
                            dialog.cancel();
                            newCard.getCard().setStackId(availableStacks.get(which).getStack().getLocalId());
                            LiveDataHelper.observeOnce(syncManager.updateCard(newCard), lifecycleOwner, (c) -> {
                                // Nothing to do here...
                            });
                            DeckLog.log("Moved card \"" + card.getCard().getTitle() + "\" to \"" + availableStacks.get(which).getStack().getTitle() + "\"");
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setTitle(context.getString(R.string.action_card_move_title, card.getCard().getTitle()))
                        .show();
                return true;
            }
            case R.id.action_card_archive: {
                syncManager.archiveCard(card);
                return true;
            }
            case R.id.action_card_delete: {
                syncManager.deleteCard(card.getCard());
                return true;
            }
        }
        return true;
    }

    static class ItemCardViewHolder extends RecyclerView.ViewHolder {
        private ItemCardBinding binding;

        private ItemCardViewHolder(ItemCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface SelectCardListener {
        void onCardSelected(FullCard fullCard);
    }
}
