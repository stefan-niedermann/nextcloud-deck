package it.niedermann.nextcloud.deck.ui.card;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getSecondaryForegroundColorDependingOnTheme;
import static it.niedermann.nextcloud.deck.util.MimeTypeUtil.TEXT_PLAIN;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.android.crosstabdnd.DragAndDropAdapter;
import it.niedermann.android.crosstabdnd.DraggedItemLocalState;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.ItemCardCompactBinding;
import it.niedermann.nextcloud.deck.databinding.ItemCardDefaultBinding;
import it.niedermann.nextcloud.deck.databinding.ItemCardDefaultOnlyTitleBinding;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.branding.Branded;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.movecard.MoveCardDialogFragment;
import it.niedermann.nextcloud.deck.util.CardUtil;

public class CardAdapter extends RecyclerView.Adapter<AbstractCardViewHolder> implements DragAndDropAdapter<FullCard>, CardOptionsItemSelectedListener, Branded {

    private final ExecutorService executor;
    private final boolean compactMode;
    @NonNull
    protected final MainViewModel mainViewModel;
    @NonNull
    protected final FragmentManager fragmentManager;
    private final long stackId;
    @NonNull
    protected final Activity activity;
    @Nullable
    private final SelectCardListener selectCardListener;
    @NonNull
    protected List<FullCard> cardList = new ArrayList<>();
    @NonNull
    protected String counterMaxValue;
    @ColorInt
    protected int mainColor;
    @StringRes
    private final int shareLinkRes;
    protected final int maxCoverImages;

    public CardAdapter(@NonNull Activity activity, @NonNull FragmentManager fragmentManager, long stackId, @NonNull MainViewModel mainViewModel, @Nullable SelectCardListener selectCardListener) {
        this(activity, fragmentManager, stackId, mainViewModel, selectCardListener, Executors.newSingleThreadExecutor());
    }

    private CardAdapter(@NonNull Activity activity, @NonNull FragmentManager fragmentManager, long stackId, @NonNull MainViewModel mainViewModel, @Nullable SelectCardListener selectCardListener, @NonNull ExecutorService executor) {
        this.activity = activity;
        this.counterMaxValue = this.activity.getString(R.string.counter_max_value);
        this.fragmentManager = fragmentManager;
        this.shareLinkRes = mainViewModel.getCurrentAccount().getServerDeckVersionAsObject().getShareLinkResource();
        this.stackId = stackId;
        this.mainViewModel = mainViewModel;
        this.selectCardListener = selectCardListener;
        this.mainColor = ContextCompat.getColor(this.activity, R.color.defaultBrand);
        this.compactMode = getDefaultSharedPreferences(this.activity).getBoolean(this.activity.getString(R.string.pref_key_compact), false);
        this.maxCoverImages = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(activity.getString(R.string.pref_key_cover_images), true)
                ? activity.getResources().getInteger(R.integer.max_cover_images)
                : 0;
        setHasStableIds(true);
        this.executor = executor;
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
        @NonNull final var fullCard = cardList.get(position);
        viewHolder.bind(fullCard, mainViewModel.getCurrentAccount(), mainViewModel.getCurrentBoardRemoteId(), mainViewModel.currentBoardHasEditPermission(), R.menu.card_menu, this, counterMaxValue, mainColor);

        // Only enable details view if there is no one waiting for selecting a card.
        viewHolder.bindCardClickListener((v) -> {
            if (selectCardListener == null) {
                activity.startActivity(EditActivity.createEditCardIntent(activity, mainViewModel.getCurrentAccount(), mainViewModel.getCurrentBoardLocalId(), fullCard.getLocalId()));
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

    public void setCardList(@NonNull List<FullCard> cardList) {
        this.cardList.clear();
        this.cardList.addAll(cardList);
        notifyDataSetChanged();
    }

    @Override
    public void applyBrand(int mainColor) {
        this.mainColor = getSecondaryForegroundColorDependingOnTheme(activity, mainColor);
        notifyDataSetChanged();
    }

    @Override
    public boolean onCardOptionsItemSelected(@NonNull MenuItem menuItem, @NonNull FullCard fullCard) {
        final int itemId = menuItem.getItemId();
        final var account = mainViewModel.getCurrentAccount();
        if (itemId == R.id.share_link) {
            final var shareIntent = new Intent()
                    .setAction(Intent.ACTION_SEND)
                    .setType(TEXT_PLAIN)
                    .putExtra(Intent.EXTRA_SUBJECT, fullCard.getCard().getTitle())
                    .putExtra(Intent.EXTRA_TITLE, fullCard.getCard().getTitle())
                    .putExtra(Intent.EXTRA_TEXT, account.getUrl() + activity.getString(shareLinkRes, mainViewModel.getCurrentBoardRemoteId(), fullCard.getCard().getId()));
            activity.startActivity(Intent.createChooser(shareIntent, fullCard.getCard().getTitle()));
            return true;
        } else if (itemId == R.id.share_content) {
            final var shareIntent = new Intent()
                    .setAction(Intent.ACTION_SEND)
                    .setType(TEXT_PLAIN)
                    .putExtra(Intent.EXTRA_SUBJECT, fullCard.getCard().getTitle())
                    .putExtra(Intent.EXTRA_TITLE, fullCard.getCard().getTitle())
                    .putExtra(Intent.EXTRA_TEXT, CardUtil.getCardContentAsString(activity, fullCard));
            activity.startActivity(Intent.createChooser(shareIntent, fullCard.getCard().getTitle()));
        } else if (itemId == R.id.action_card_assign) {
            executor.submit(() -> mainViewModel.assignUserToCard(mainViewModel.getUserByUidDirectly(fullCard.getCard().getAccountId(), account.getUserName()), fullCard.getCard()));
            return true;
        } else if (itemId == R.id.action_card_unassign) {
            executor.submit(() -> mainViewModel.unassignUserFromCard(mainViewModel.getUserByUidDirectly(fullCard.getCard().getAccountId(), account.getUserName()), fullCard.getCard()));
            return true;
        } else if (itemId == R.id.action_card_move) {
            DeckLog.verbose("[Move card] Launch move dialog for " + Card.class.getSimpleName() + " \"" + fullCard.getCard().getTitle() + "\" (#" + fullCard.getLocalId() + ") from " + Stack.class.getSimpleName() + " #" + +stackId);
            MoveCardDialogFragment
                    .newInstance(fullCard.getAccountId(), mainViewModel.getCurrentBoardLocalId(), fullCard.getCard().getTitle(), fullCard.getLocalId(), CardUtil.cardHasCommentsOrAttachments(fullCard))
                    .show(fragmentManager, MoveCardDialogFragment.class.getSimpleName());
            return true;
        } else if (itemId == R.id.action_card_archive) {
            mainViewModel.archiveCard(fullCard, new IResponseCallback<>() {
                @Override
                public void onResponse(FullCard response) {
                    DeckLog.info("Successfully archived", Card.class.getSimpleName(), fullCard.getCard().getTitle());
                }

                @Override
                public void onError(Throwable throwable) {
                    IResponseCallback.super.onError(throwable);
                    activity.runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, account).show(fragmentManager, ExceptionDialogFragment.class.getSimpleName()));
                }
            });
            return true;
        } else if (itemId == R.id.action_card_delete) {
            mainViewModel.deleteCard(fullCard.getCard(), new IResponseCallback<>() {
                @Override
                public void onResponse(Void response) {
                    DeckLog.info("Successfully deleted card", fullCard.getCard().getTitle());
                }

                @Override
                public void onError(Throwable throwable) {
                    if (!SyncManager.ignoreExceptionOnVoidError(throwable)) {
                        IResponseCallback.super.onError(throwable);
                        activity.runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, account).show(fragmentManager, ExceptionDialogFragment.class.getSimpleName()));
                    }
                }
            });
            return true;
        }
        return true;
    }
}
