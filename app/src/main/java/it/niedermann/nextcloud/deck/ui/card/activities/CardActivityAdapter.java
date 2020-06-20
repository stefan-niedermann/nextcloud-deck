package it.niedermann.nextcloud.deck.ui.card.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.niedermann.nextcloud.deck.databinding.ItemActivityBinding;
import it.niedermann.nextcloud.deck.model.ocs.Activity;

public class CardActivityAdapter extends RecyclerView.Adapter<CardActivityViewHolder> {

    @NonNull
    private final List<Activity> activities;
    @NonNull
    private final MenuInflater menuInflater;

    @SuppressWarnings("WeakerAccess")
    public CardActivityAdapter(@NonNull List<Activity> activities, @NonNull MenuInflater menuInflater) {
        super();
        this.activities = activities;
        this.menuInflater = menuInflater;
    }

    @NonNull
    @Override
    public CardActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        ItemActivityBinding binding = ItemActivityBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CardActivityViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardActivityViewHolder holder, int position) {
        holder.bind(activities.get(position), menuInflater);
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }
}
