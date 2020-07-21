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
    private final List<OcsProjectResource> resources = new ArrayList<>();

    public CardProjectResourceAdapter(@NonNull Account account) {
        this.account = account;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return resources.get(position).getLocalId();
    }

    @NonNull
    @Override
    public CardProjectResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardProjectResourceViewHolder(ItemProjectResourceBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindViewHolder(@NonNull CardProjectResourceViewHolder holder, int position) {
        holder.bind(account, resources.get(position));
    }

    public void setResources(@NonNull List<OcsProjectResource> resources) {
        this.resources.clear();
        this.resources.addAll(resources);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.resources.size();
    }
}
