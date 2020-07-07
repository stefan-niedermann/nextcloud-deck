package it.niedermann.nextcloud.deck.ui.card;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.niedermann.android.crosstabdnd.DragAndDropAdapter;
import it.niedermann.android.crosstabdnd.DraggedItemLocalState;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCardBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.branding.Branded;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.movecard.MoveCardDialogFragment;
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getSecondaryForegroundColorDependingOnTheme;
import static it.niedermann.nextcloud.deck.util.MimeTypeUtil.TEXT_PLAIN;

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> implements DragAndDropAdapter<FullCard>, CardOptionsItemSelectedListener, Branded {

    protected final SyncManager syncManager;

    protected final FragmentManager fragmentManager;
    protected final Account account;
    @Nullable
    protected final Long boardRemoteId;
    private final long boardLocalId;
    private final long stackId;
    protected final boolean hasEditPermission;
    @NonNull
    private final Context context;
    @Nullable
    private final SelectCardListener selectCardListener;
    protected List<FullCard> cardList = new LinkedList<>();
    protected LifecycleOwner lifecycleOwner;
    @NonNull
    private final List<FullStack> availableStacks = new ArrayList<>();
    protected String counterMaxValue;

    protected int mainColor;
    @StringRes
    private int shareLinkRes;

    public CardAdapter(@NonNull Context context, @NonNull FragmentManager fragmentManager, @NonNull Account account, long boardLocalId, @Nullable Long boardRemoteId, long stackId, boolean hasEditPermission, @NonNull SyncManager syncManager, @NonNull LifecycleOwner lifecycleOwner, @Nullable SelectCardListener selectCardListener) {
        this.context = context;
        this.counterMaxValue = context.getString(R.string.counter_max_value);
        this.fragmentManager = fragmentManager;
        this.lifecycleOwner = lifecycleOwner;
        this.account = account;
        this.shareLinkRes = account.getServerDeckVersionAsObject().getShareLinkResource();
        this.boardLocalId = boardLocalId;
        this.boardRemoteId = boardRemoteId;
        this.stackId = stackId;
        this.hasEditPermission = hasEditPermission;
        this.syncManager = syncManager;
        this.selectCardListener = selectCardListener;
        this.mainColor = context.getResources().getColor(R.color.defaultBrand);
        syncManager.getStacksForBoard(account.getId(), boardLocalId).observe(this.lifecycleOwner, (stacks) -> {
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
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new CardViewHolder(ItemCardBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder viewHolder, int position) {
        @NonNull FullCard fullCard = cardList.get(position);
        viewHolder.bind(fullCard, account, boardRemoteId, hasEditPermission, R.menu.card_menu, this, counterMaxValue, mainColor);

        // Only enable details view if there is no one waiting for selecting a card.
        viewHolder.bindCardClickListener((v) -> {
            if (selectCardListener == null) {
                context.startActivity(EditActivity.createEditCardIntent(context, account, boardLocalId, fullCard.getLocalId()));
            } else {
                selectCardListener.onCardSelected(fullCard);
            }
        });

        // Only enable Drag and Drop if there is no one waiting for selecting a card.
        if (selectCardListener == null) {
            viewHolder.bindCardLongClickListener((v) -> {
                DeckLog.log("Starting drag and drop");
                v.startDrag(ClipData.newPlainText("cardid", String.valueOf(fullCard.getLocalId())),
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

    public void setCardList(@NonNull List<FullCard> cardList) {
        this.cardList.clear();
        this.cardList.addAll(cardList);
        notifyDataSetChanged();
    }

    @Override
    public void applyBrand(int mainColor) {
        this.mainColor = getSecondaryForegroundColorDependingOnTheme(context, mainColor);
        notifyDataSetChanged();
    }

    @Override
    public boolean onCardOptionsItemSelected(@NonNull MenuItem menuItem, @NonNull FullCard fullCard) {
        switch (menuItem.getItemId()) {
            case R.id.share_link: {
                Intent shareIntent = new Intent()
                        .setAction(Intent.ACTION_SEND)
                        .setType(TEXT_PLAIN)
                        .putExtra(Intent.EXTRA_SUBJECT, fullCard.getCard().getTitle())
                        .putExtra(Intent.EXTRA_TITLE, fullCard.getCard().getTitle())
                        .putExtra(Intent.EXTRA_TEXT, account.getUrl() + context.getString(shareLinkRes, boardRemoteId, fullCard.getCard().getId()));
                context.startActivity(Intent.createChooser(shareIntent, fullCard.getCard().getTitle()));
            }
            case R.id.action_card_assign: {
                new Thread(() -> syncManager.assignUserToCard(syncManager.getUserByUidDirectly(fullCard.getCard().getAccountId(), account.getUserName()), fullCard.getCard())).start();
                return true;
            }
            case R.id.action_card_unassign: {
                new Thread(() -> syncManager.unassignUserFromCard(syncManager.getUserByUidDirectly(fullCard.getCard().getAccountId(), account.getUserName()), fullCard.getCard())).start();
                return true;
            }
            case R.id.action_card_move: {
                DeckLog.verbose("[Move card] Launch move dialog for " + Card.class.getSimpleName() + " \"" + fullCard.getCard().getTitle() + "\" (#" + fullCard.getLocalId() + ") from " + Stack.class.getSimpleName() + " #" + +stackId);
                MoveCardDialogFragment.newInstance(fullCard.getAccountId(), boardId, fullCard.getLocalId()).show(fragmentManager, MoveCardDialogFragment.class.getSimpleName());
                return true;
            }
            case R.id.action_card_archive: {
                final WrappedLiveData<FullCard> archiveLiveData = syncManager.archiveCard(fullCard);
                observeOnce(archiveLiveData, lifecycleOwner, (v) -> {
                    if (archiveLiveData.hasError()) {
                        ExceptionDialogFragment.newInstance(archiveLiveData.getError(), account).show(fragmentManager, ExceptionDialogFragment.class.getSimpleName());
                    }
                });
                return true;
            }
            case R.id.action_card_delete: {
                final WrappedLiveData<Void> deleteLiveData = syncManager.deleteCard(fullCard.getCard());
                observeOnce(deleteLiveData, lifecycleOwner, (v) -> {
                    if (deleteLiveData.hasError()) {
                        ExceptionDialogFragment.newInstance(deleteLiveData.getError(), account).show(fragmentManager, ExceptionDialogFragment.class.getSimpleName());
                    }
                });
                return true;
            }
        }
        return true;
    }
}
