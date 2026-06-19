package it.niedermann.nextcloud.deck.ui.card.activities;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.databinding.ItemActivityBinding;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;

public class CardActivityAdapter extends RecyclerView.Adapter<CardActivityViewHolder> {

    @NonNull
    private final List<Activity> activities = new ArrayList<>();
    @Nullable
    private ThemeUtils utils;
    @NonNull
    private final MenuInflater menuInflater;

    @SuppressWarnings("WeakerAccess")
    public CardActivityAdapter(@NonNull MenuInflater menuInflater) {
        super();
        this.menuInflater = menuInflater;
    }

    @NonNull
    @Override
    public CardActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final var context = parent.getContext();
        final var binding = ItemActivityBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CardActivityViewHolder(binding);
    }

    public void setData(@NonNull List<Activity> activities, @NonNull ThemeUtils utils) {
        this.activities.clear();
        this.activities.addAll(activities);
        this.utils = utils;
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull CardActivityViewHolder holder, int position) {
        holder.bind(activities.get(position), menuInflater, utils);
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }
}
