package it.niedermann.nextcloud.deck.ui.card.details;

import static androidx.recyclerview.widget.RecyclerView.NO_ID;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemDependentBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.Themed;

@SuppressWarnings("WeakerAccess")
public class DependentsAdapter extends RecyclerView.Adapter<DependentViewHolder> implements Themed {

    @NonNull
    private ThemeUtils utils;

    private boolean enabled = false;

    @Nullable
    private RecyclerView recyclerView;

    private final Account account;

    @NonNull
    private final Context context;

    @NonNull
    private final List<Card> cards = new ArrayList<>();

    @NonNull
    private final Consumer<Card> doneStatus;

    @NonNull
    private final Consumer<Card> removeDependent;

    DependentsAdapter(
            @NonNull Context context,
            @NonNull Consumer<Card> doneStatus,
            @NonNull Consumer<Card> removeDependent,
            @NonNull Account account,
            boolean enabled
    ) {
        super();
        this.doneStatus = doneStatus;
        this.removeDependent = removeDependent;
        this.account = account;
        this.context = context.getApplicationContext();
        this.enabled = enabled;
        this.utils = ThemeUtils.of(ContextCompat.getColor(context, R.color.primary), context);
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
        return new DependentViewHolder(ItemDependentBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DependentViewHolder holder, int position) {
        final var card = cards.get(position);
        holder.bind(account, card, doneStatus, removedCard -> {
            removeCard(removedCard);
            removeDependent.accept(removedCard);
        }, utils);
        holder.setEnabled(enabled);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setCards(@NonNull List<Card> cards) {
        this.cards.clear();
        this.cards.addAll(cards);
        updateRecyclerViewVisibility();
        notifyDataSetChanged();
    }

    public void addCard(@NonNull Card card) {
        if (!this.cards.contains(card)) {
            this.cards.add(card);
            updateRecyclerViewVisibility();
            notifyItemInserted(this.cards.size());
        }
    }

    public void removeCard(@NonNull Card card) {
        final int index = this.cards.indexOf(card);
        this.cards.remove(card);
        updateRecyclerViewVisibility();
        notifyItemRemoved(index);
    }

    private void updateRecyclerViewVisibility() {
        if (this.recyclerView != null) {
            this.recyclerView.setVisibility(this.getItemCount() > 0 ? View.VISIBLE : View.GONE);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        notifyDataSetChanged();
    }

    @Override
    public void applyTheme(int color) {
        this.utils = ThemeUtils.of(color, context);
        notifyDataSetChanged();
    }
}
