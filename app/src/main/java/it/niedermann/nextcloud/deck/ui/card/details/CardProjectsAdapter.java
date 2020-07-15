package it.niedermann.nextcloud.deck.ui.card.details;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.databinding.ItemProjectResourceBinding;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProject;

public class CardProjectsAdapter extends RecyclerView.Adapter<CardProjectsViewHolder> {

    @NonNull
    private List<OcsProject> projects;

    public CardProjectsAdapter(@NonNull List<OcsProject> projects) {
        this.projects = new ArrayList<>(projects.size());
        this.projects.addAll(projects);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return projects.get(position).getLocalId();
    }

    @NonNull
    @Override
    public CardProjectsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardProjectsViewHolder(ItemProjectResourceBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindViewHolder(@NonNull CardProjectsViewHolder holder, int position) {
        holder.bind(projects.get(position));
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }
}
