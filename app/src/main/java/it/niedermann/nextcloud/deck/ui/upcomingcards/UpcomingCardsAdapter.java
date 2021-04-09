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

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemCardCompactBinding;
import it.niedermann.nextcloud.deck.databinding.ItemCardDefaultBinding;
import it.niedermann.nextcloud.deck.databinding.ItemCardDefaultOnlyTitleBinding;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.card.AbstractCardViewHolder;
import it.niedermann.nextcloud.deck.ui.card.CompactCardViewHolder;
import it.niedermann.nextcloud.deck.ui.card.DefaultCardOnlyTitleViewHolder;
import it.niedermann.nextcloud.deck.ui.card.DefaultCardViewHolder;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class UpcomingCardsAdapter extends RecyclerView.Adapter<AbstractCardViewHolder> {

    private final boolean compactMode;
    @NonNull
    protected final FragmentManager fragmentManager;
    @NonNull
    protected final Activity activity;
    @NonNull
    protected List<UpcomingCardsAdapterItem> cardList = new ArrayList<>();
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
        return cardList.get(position).getFullCard().getLocalId();
    }

    @NonNull
    @Override
    public AbstractCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == R.layout.item_card_compact) {
            return new CompactCardViewHolder(ItemCardCompactBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
        } else if (viewType == R.layout.item_card_default_only_title) {
            return new DefaultCardOnlyTitleViewHolder(ItemCardDefaultOnlyTitleBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
        }
        return new DefaultCardViewHolder(ItemCardDefaultBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (compactMode) {
            return R.layout.item_card_compact;
        } else {
            final FullCard fullCard = cardList.get(position).getFullCard();
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
        UpcomingCardsAdapterItem item = cardList.get(position);
        viewHolder.bind(item.getFullCard(), item.getAccount(), item.getCurrentBoardRemoteId(), false, R.menu.card_menu, (a, b) -> true, counterMaxValue, mainColor);
        viewHolder.bindCardClickListener((v) -> activity.startActivity(EditActivity.createEditCardIntent(activity, item.getAccount(), item.getCurrentBoardLocalId(), item.getFullCard().getLocalId())));
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public void setCardList(@NonNull List<UpcomingCardsAdapterItem> cardList) {
        this.cardList.clear();
        this.cardList.addAll(cardList);
        notifyDataSetChanged();
    }
}
