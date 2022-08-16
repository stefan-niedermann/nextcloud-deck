package it.niedermann.nextcloud.deck.ui.upcomingcards;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCardCompactBinding;
import it.niedermann.nextcloud.deck.databinding.ItemCardDefaultBinding;
import it.niedermann.nextcloud.deck.databinding.ItemCardDefaultOnlyTitleBinding;
import it.niedermann.nextcloud.deck.databinding.ItemSectionBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.card.AbstractCardViewHolder;
import it.niedermann.nextcloud.deck.ui.card.CompactCardViewHolder;
import it.niedermann.nextcloud.deck.ui.card.DefaultCardOnlyTitleViewHolder;
import it.niedermann.nextcloud.deck.ui.card.DefaultCardViewHolder;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;

public class UpcomingCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final boolean compactMode;
    @NonNull
    protected final FragmentManager fragmentManager;
    @NonNull
    protected final Activity activity;
    @NonNull
    protected List<Object> items = new ArrayList<>();
    @NonNull
    protected String counterMaxValue;
    @ColorInt
    protected int mainColor;
    @NonNull
    private final BiConsumer<Account, Card> assignCard;
    @NonNull
    private final BiConsumer<Account, Card> unassignCard;
    @NonNull
    private final Consumer<FullCard> archiveCard;
    @NonNull
    private final Consumer<Card> deleteCard;
    private final int maxCoverImages;

    public UpcomingCardsAdapter(@NonNull Activity activity, @NonNull FragmentManager fragmentManager,
                                @NonNull BiConsumer<Account, Card> assignCard,
                                @NonNull BiConsumer<Account, Card> unassignCard,
                                @NonNull Consumer<FullCard> archiveCard,
                                @NonNull Consumer<Card> deleteCard) {
        this.activity = activity;
        this.counterMaxValue = this.activity.getString(R.string.counter_max_value);
        this.fragmentManager = fragmentManager;
        this.mainColor = ContextCompat.getColor(this.activity, R.color.defaultBrand);
        this.compactMode = getDefaultSharedPreferences(this.activity).getBoolean(this.activity.getString(R.string.pref_key_compact), false);
        this.assignCard = assignCard;
        this.unassignCard = unassignCard;
        this.archiveCard = archiveCard;
        this.deleteCard = deleteCard;
        this.maxCoverImages = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(activity.getString(R.string.pref_key_cover_images), true)
                ? activity.getResources().getInteger(R.integer.max_cover_images)
                : 0;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        final var item = items.get(position);
        if (item.getClass() == UpcomingCardsAdapterSectionItem.class || item instanceof UpcomingCardsAdapterSectionItem) {
            return items
                    .stream()
                    .filter(i -> (i.getClass() == UpcomingCardsAdapterSectionItem.class || i instanceof UpcomingCardsAdapterSectionItem))
                    .collect(Collectors.toList())
                    .indexOf(item) * -1;
        } else if (item.getClass() == UpcomingCardsAdapterItem.class || item instanceof UpcomingCardsAdapterItem) {
            return ((UpcomingCardsAdapterItem) item).getFullCard().getLocalId();
        } else {
            throw new IllegalStateException(item.getClass().getSimpleName() + " must be a " + UpcomingCardsAdapterSectionItem.class.getSimpleName() + " or " + UpcomingCardsAdapterItem.class.getSimpleName());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == R.layout.item_section) {
            return new UpcomingCardsSectionViewHolder(ItemSectionBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
        } else if (viewType == R.layout.item_card_compact) {
            return new CompactCardViewHolder(ItemCardCompactBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false), this.maxCoverImages);
        } else if (viewType == R.layout.item_card_default_only_title) {
            return new DefaultCardOnlyTitleViewHolder(ItemCardDefaultOnlyTitleBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
        }
        return new DefaultCardViewHolder(ItemCardDefaultBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false), this.maxCoverImages);
    }

    @Override
    public int getItemViewType(int position) {
        final var item = items.get(position);
        if (item.getClass() == UpcomingCardsAdapterSectionItem.class || item instanceof UpcomingCardsAdapterSectionItem) {
            return R.layout.item_section;
        } else if (item.getClass() == UpcomingCardsAdapterItem.class || item instanceof UpcomingCardsAdapterItem) {
            if (compactMode) {
                return R.layout.item_card_compact;
            } else {
                final var fullCard = ((UpcomingCardsAdapterItem) item).getFullCard();
                if (fullCard.getAttachments().size() == 0
                        && fullCard.getAssignedUsers().size() == 0
                        && fullCard.getLabels().size() == 0
                        && fullCard.getCommentCount() == 0
                        && fullCard.getCard().getTaskStatus().taskCount == 0) {
                    return R.layout.item_card_default_only_title;
                }
                return R.layout.item_card_default;
            }
        } else {
            throw new IllegalStateException(item.getClass().getSimpleName() + " must be a " + UpcomingCardsAdapterSectionItem.class.getSimpleName() + " or " + UpcomingCardsAdapterItem.class.getSimpleName());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final Object item = items.get(position);
        if (item.getClass() == UpcomingCardsAdapterSectionItem.class || item instanceof UpcomingCardsAdapterSectionItem) {
            if (viewHolder.getClass() == UpcomingCardsSectionViewHolder.class || viewHolder instanceof UpcomingCardsSectionViewHolder) {
                final var cardItem = (UpcomingCardsAdapterSectionItem) item;
                ((UpcomingCardsSectionViewHolder) viewHolder).bind(cardItem.getTitle());
            } else {
                throw new IllegalStateException("Item at position " + position + " is a " + item.getClass().getSimpleName() + " but viewHolder is no " + UpcomingCardsSectionViewHolder.class.getSimpleName());
            }
        } else if (item.getClass() == UpcomingCardsAdapterItem.class || item instanceof UpcomingCardsAdapterItem) {
            if (viewHolder instanceof AbstractCardViewHolder) {
                final var cardItem = (UpcomingCardsAdapterItem) item;
                final var cardViewHolder = ((AbstractCardViewHolder) viewHolder);
                cardViewHolder.bind(cardItem.getFullCard(), cardItem.getAccount(), cardItem.getCurrentBoardRemoteId(), cardItem.currentBoardHasEditPermission(), R.menu.card_menu,
                        new UpcomingCardsOptionsItemSelectedListener(
                                cardItem.getAccount(),
                                activity,
                                fragmentManager,
                                cardItem.getCurrentBoardRemoteId(),
                                cardItem.getCurrentBoardLocalId(),
                                assignCard,
                                unassignCard,
                                archiveCard,
                                deleteCard
                        ), counterMaxValue, mainColor);
                cardViewHolder.bindCardClickListener((v) -> activity.startActivity(EditActivity.createEditCardIntent(activity, cardItem.getAccount(), cardItem.getCurrentBoardLocalId(), cardItem.getFullCard().getLocalId())));
            } else {
                throw new IllegalStateException("Item at position " + position + " is a " + item.getClass().getSimpleName() + " but viewHolder is no " + AbstractCardViewHolder.class.getSimpleName());
            }
        } else {
            throw new IllegalStateException(item.getClass().getSimpleName() + " must be a " + UpcomingCardsAdapterSectionItem.class.getSimpleName() + " or " + UpcomingCardsAdapterItem.class.getSimpleName());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(@NonNull List<UpcomingCardsAdapterItem> items) {
        this.items.clear();
        this.items.addAll(UpcomingCardsUtil.addDueDateSeparators(activity, items));
        notifyDataSetChanged();
    }
}
