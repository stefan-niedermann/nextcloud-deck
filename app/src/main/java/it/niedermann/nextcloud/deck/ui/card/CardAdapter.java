package it.niedermann.nextcloud.deck.ui.card;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

import android.app.Activity;
import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.android.crosstabdnd.DragAndDropAdapter;
import it.niedermann.android.crosstabdnd.DraggedItemLocalState;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCardCompactBinding;
import it.niedermann.nextcloud.deck.databinding.ItemCardDefaultBinding;
import it.niedermann.nextcloud.deck.databinding.ItemCardDefaultOnlyTitleBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;

public class CardAdapter extends RecyclerView.Adapter<AbstractCardViewHolder> implements DragAndDropAdapter<FullCard>, CardOptionsItemSelectedListener {

    private final boolean compactMode;
    @Nullable
    private Account account;
    @Nullable
    private FullBoard fullBoard;
    @NonNull
    private final Activity activity;
    @Nullable
    private final SelectCardListener selectCardListener;
    @NonNull
    private final CardActionListener cardActionListener;
    @NonNull
    private final List<FullCard> cardList = new ArrayList<>();
    @NonNull
    private final String counterMaxValue;
    @Nullable
    private ThemeUtils utils;
    private final int maxCoverImages;

    public CardAdapter(
            @NonNull Activity activity,
            @NonNull CardActionListener cardActionListener,
            @Nullable SelectCardListener selectCardListener
    ) {
        this.activity = activity;
        this.counterMaxValue = this.activity.getString(R.string.counter_max_value);
        this.cardActionListener = cardActionListener;
        this.selectCardListener = selectCardListener;
        this.compactMode = getDefaultSharedPreferences(this.activity).getBoolean(this.activity.getString(R.string.pref_key_compact), false);
        this.maxCoverImages = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(activity.getString(R.string.pref_key_cover_images), true) ? activity.getResources().getInteger(R.integer.max_cover_images) : 0;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return cardList.get(position).getLocalId();
    }

    @NonNull
    @Override
    public AbstractCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == R.layout.item_card_compact) {
            return new CompactCardViewHolder(ItemCardCompactBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false), this.maxCoverImages);
        } else if (viewType == R.layout.item_card_default_only_title) {
            return new DefaultCardOnlyTitleViewHolder(ItemCardDefaultOnlyTitleBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
        }
        return new DefaultCardViewHolder(ItemCardDefaultBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false), this.maxCoverImages);
    }

    @Override
    public int getItemViewType(int position) {
        if (compactMode) {
            return R.layout.item_card_compact;
        } else {
            final var fullCard = cardList.get(position);
            if (fullCard.getAttachments().size() == 0
                    && fullCard.getAssignedUsers().size() == 0
                    && fullCard.getLabels().size() == 0
                    && fullCard.getCommentCount() == 0
                    && fullCard.getCard().getTaskStatus().taskCount == 0) {
                return R.layout.item_card_default_only_title;
            }
            return R.layout.item_card_default;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractCardViewHolder viewHolder, int position) {
        if (account == null) {
            throw new IllegalStateException("Tried to bind viewholder while account is still null");
        }
        if (fullBoard == null) {
            throw new IllegalStateException("Tried to bind viewholder while fullBoard is still null");
        }

        @NonNull final var fullCard = cardList.get(position);
        viewHolder.bind(fullCard, account, fullBoard.getBoard().getId(), fullBoard.board.isPermissionEdit(), R.menu.card_menu, this, counterMaxValue, utils);

        // Only enable details view if there is no one waiting for selecting a card.
        viewHolder.bindCardClickListener((v) -> {
            if (selectCardListener == null) {
                activity.startActivity(EditActivity.createEditCardIntent(activity, account, fullBoard.getBoard().getLocalId(), fullCard.getLocalId()));
            } else {
                selectCardListener.onCardSelected(fullCard, fullBoard.getLocalId());
            }
        });

        // Only enable Drag and Drop if there is no one waiting for selecting a card.
        if (selectCardListener == null) {
            viewHolder.bindCardLongClickListener((v) -> {
                DeckLog.log("Starting drag and drop");
                v.startDragAndDrop(ClipData.newPlainText("cardid", String.valueOf(fullCard.getLocalId())),
                        new View.DragShadowBuilder(v),
                        new DraggedItemLocalState<>(fullCard, viewHolder.getDraggable(), this, position),
                        0
                );
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public void insertItem(FullCard fullCard, int position) {
        cardList.add(position, fullCard);
        notifyItemInserted(position);
    }

    @NonNull
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

    public void setAccount(@NonNull Account account) {
        this.account = account;
    }

    public void setFullBoard(@NonNull FullBoard fullBoard) {
        this.fullBoard = fullBoard;
    }

    public void setCardList(@NonNull List<FullCard> cardList, @ColorInt int color) {
        this.utils = ThemeUtils.of(color, activity);
        this.cardList.clear();
        this.cardList.addAll(cardList);
        notifyDataSetChanged();
    }

    @Override
    public boolean onCardOptionsItemSelected(@NonNull MenuItem menuItem, @NonNull FullCard fullCard) {
        final int itemId = menuItem.getItemId();
        if (itemId == R.id.share_link) {
            if (fullBoard == null) {
                DeckLog.warn("Can not share link to card", fullCard.getCard().getTitle(), "because fullBoard is null");
                return false;
            }
            cardActionListener.onShareLink(fullBoard, fullCard);
            return true;
        } else if (itemId == R.id.share_content) {
            cardActionListener.onShareContent(fullCard);
        } else if (itemId == R.id.action_card_assign) {
            cardActionListener.onAssignCurrentUser(fullCard);
            return true;
        } else if (itemId == R.id.action_card_unassign) {
            cardActionListener.onUnassignCurrentUser(fullCard);
            return true;
        } else if (itemId == R.id.action_card_move) {
            if (fullBoard == null) {
                DeckLog.warn("Can not move card", fullCard.getCard().getTitle(), "because fullBoard is null");
                return false;
            }
            cardActionListener.onMove(fullBoard, fullCard);
            return true;
        } else if (itemId == R.id.action_card_archive) {
            cardActionListener.onArchive(fullCard);
            return true;
        } else if (itemId == R.id.action_card_delete) {
            cardActionListener.onDelete(fullCard);
            return true;
        }
        return true;
    }
}
