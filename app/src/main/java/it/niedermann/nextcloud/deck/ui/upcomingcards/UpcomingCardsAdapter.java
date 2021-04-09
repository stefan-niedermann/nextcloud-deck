package it.niedermann.nextcloud.deck.ui.upcomingcards;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCardCompactBinding;
import it.niedermann.nextcloud.deck.databinding.ItemCardDefaultBinding;
import it.niedermann.nextcloud.deck.databinding.ItemCardDefaultOnlyTitleBinding;
import it.niedermann.nextcloud.deck.databinding.ItemSectionBinding;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.card.AbstractCardViewHolder;
import it.niedermann.nextcloud.deck.ui.card.CompactCardViewHolder;
import it.niedermann.nextcloud.deck.ui.card.DefaultCardOnlyTitleViewHolder;
import it.niedermann.nextcloud.deck.ui.card.DefaultCardViewHolder;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

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

    public UpcomingCardsAdapter(@NonNull Activity activity, @NonNull FragmentManager fragmentManager) {
        this.activity = activity;
        this.counterMaxValue = this.activity.getString(R.string.counter_max_value);
        this.fragmentManager = fragmentManager;
        this.mainColor = ContextCompat.getColor(this.activity, R.color.defaultBrand);
        this.compactMode = getDefaultSharedPreferences(this.activity).getBoolean(this.activity.getString(R.string.pref_key_compact), false);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        final Object item = items.get(position);
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
        if (viewType == 0) {
            return new UpcomingCardsSectionViewHolder(ItemSectionBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
        } else if (viewType == R.layout.item_card_compact) {
            return new CompactCardViewHolder(ItemCardCompactBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
        } else if (viewType == R.layout.item_card_default_only_title) {
            return new DefaultCardOnlyTitleViewHolder(ItemCardDefaultOnlyTitleBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
        }
        return new DefaultCardViewHolder(ItemCardDefaultBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public int getItemViewType(int position) {
        final Object item = items.get(position);
        if (item.getClass() == UpcomingCardsAdapterSectionItem.class || item instanceof UpcomingCardsAdapterSectionItem) {
            return R.layout.item_section;
        } else if (item.getClass() == UpcomingCardsAdapterItem.class || item instanceof UpcomingCardsAdapterItem) {
            if (compactMode) {
                return R.layout.item_card_compact;
            } else {
                final FullCard fullCard = ((UpcomingCardsAdapterItem) item).getFullCard();
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
            final UpcomingCardsAdapterSectionItem cardItem = (UpcomingCardsAdapterSectionItem) item;
            if (viewHolder.getClass() == UpcomingCardsSectionViewHolder.class || viewHolder instanceof UpcomingCardsSectionViewHolder) {
                ((UpcomingCardsSectionViewHolder) viewHolder).bind(cardItem.getTitle());
            } else {
                throw new IllegalStateException("Item at position " + position  + " is a " + item.getClass().getSimpleName() + " but viewHolder is no " + UpcomingCardsSectionViewHolder.class.getSimpleName());
            }
        } else if (item.getClass() == UpcomingCardsAdapterItem.class || item instanceof UpcomingCardsAdapterItem) {
            final UpcomingCardsAdapterItem cardItem = (UpcomingCardsAdapterItem) item;
            AbstractCardViewHolder cardViewHolder = ((AbstractCardViewHolder) viewHolder);
            cardViewHolder.bind(cardItem.getFullCard(), cardItem.getAccount(), cardItem.getCurrentBoardRemoteId(), false, R.menu.card_menu, (a, b) -> true, counterMaxValue, mainColor);
            cardViewHolder.bindCardClickListener((v) -> activity.startActivity(EditActivity.createEditCardIntent(activity, cardItem.getAccount(), cardItem.getCurrentBoardLocalId(), cardItem.getFullCard().getLocalId())));
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
        this.items.add(new UpcomingCardsAdapterSectionItem("Hello there"));
        this.items.addAll(items);
        notifyDataSetChanged();
    }
}
