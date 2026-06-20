package it.niedermann.nextcloud.deck.ui.card.details;

import static androidx.recyclerview.widget.RecyclerView.NO_ID;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.databinding.ItemDependentBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;

@SuppressWarnings("WeakerAccess")
public class DependentsAdapter extends RecyclerView.Adapter<DependentViewHolder> {

    @Nullable
    private RecyclerView recyclerView;
    private final Account account;
    @NonNull
    private final List<Card> cards = new ArrayList<>();
    @NonNull
    private final Consumer<Card> cardClickedListener;

    DependentsAdapter(
            @NonNull Consumer<Card> cardClickedListener,
            @NonNull Account account
    ) {
        super();
        this.cardClickedListener = cardClickedListener;
        this.account = account;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        final var id = cards.get(position).getLocalId();
        return id == null ? NO_ID : id;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @NonNull
    @Override
    public DependentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final var context = parent.getContext();
        return new DependentViewHolder(ItemDependentBinding.inflate(LayoutInflater.from(context)));
    }

    @Override
    public void onBindViewHolder(@NonNull DependentViewHolder holder, int position) {
        final var card = cards.get(position);
        holder.bind(account, card, cardClickedListener);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setCards(@NonNull List<Card> cards) {
        this.cards.clear();
        this.cards.addAll(cards);
        updateRecylcerViewVisibility();
        notifyDataSetChanged();
    }

    public void addCard(@NonNull Card card) {
        this.cards.add(card);
        updateRecylcerViewVisibility();
        notifyItemInserted(this.cards.size());
    }

    public void removeCard(@NonNull Card card) {
        final int index = this.cards.indexOf(card);
        this.cards.remove(card);
        updateRecylcerViewVisibility();
        notifyItemRemoved(index);
    }

    private void updateRecylcerViewVisibility() {
        if (this.recyclerView != null) {
            this.recyclerView.setVisibility(this.getItemCount() > 0 ? View.VISIBLE : View.GONE);
        }
    }
}
