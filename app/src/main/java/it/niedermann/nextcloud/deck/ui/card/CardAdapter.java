package it.niedermann.nextcloud.deck.ui.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCardBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.branding.Branded;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getSecondaryForegroundColorDependingOnTheme;
import static it.niedermann.nextcloud.deck.util.MimeTypeUtil.TEXT_PLAIN;

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> implements DragAndDropAdapter<FullCard>, CardViewHolder.CardOptionsItemSelectedListener, Branded {

    protected final SyncManager syncManager;

    private final FragmentManager fragmentManager;
    protected final Account account;
    @Nullable
    protected final Long boardRemoteId;
    protected final long boardLocalId;
    protected final long stackId;
    protected final boolean hasEditPermission;
    @NonNull
    private final Context context;
    @Nullable
    private final SelectCardListener selectCardListener;
    protected List<FullCard> cardList = new LinkedList<>();
    private LifecycleOwner lifecycleOwner;
    private List<FullStack> availableStacks;
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
            if (stacks == null) {
                availableStacks = new ArrayList<>(0);
            } else {
                availableStacks = new ArrayList<>(stacks.size());
                availableStacks.addAll(stacks);
            }
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
        viewHolder.bind(cardList.get(position), this, position, account, boardLocalId, boardRemoteId, hasEditPermission, selectCardListener, R.menu.card_menu, this, counterMaxValue, mainColor);
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
