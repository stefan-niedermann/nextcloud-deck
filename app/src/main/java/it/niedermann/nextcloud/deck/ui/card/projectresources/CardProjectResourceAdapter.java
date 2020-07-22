package it.niedermann.nextcloud.deck.ui.card.projectresources;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.databinding.ItemProjectResourceBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;

public class CardProjectResourceAdapter extends RecyclerView.Adapter<CardProjectResourceViewHolder> {

    @NonNull
    private final Account account;
    @NonNull
    private final List<OcsProjectResource> resources;

    public CardProjectResourceAdapter(@NonNull Account account, @NonNull List<OcsProjectResource> resources) {
        this.account = account;
        this.resources = new ArrayList<>(resources.size());
        this.resources.addAll(resources);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return resources.get(position).getLocalId();
    }

    @NonNull
    @Override
    public CardProjectResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardProjectResourceViewHolder(ItemProjectResourceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CardProjectResourceViewHolder holder, int position) {
        holder.bind(account, resources.get(position));
    }

    @Override
    public int getItemCount() {
        return this.resources.size();
    }
}
