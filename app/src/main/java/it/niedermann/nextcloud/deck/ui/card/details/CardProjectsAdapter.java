package it.niedermann.nextcloud.deck.ui.card.details;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.databinding.ItemProjectBinding;
import it.niedermann.nextcloud.deck.model.ocs.projects.full.OcsProjectWithResources;
import it.niedermann.nextcloud.deck.ui.card.projectresources.CardProjectResourcesDialog;

public class CardProjectsAdapter extends RecyclerView.Adapter<CardProjectsViewHolder> {

    @NonNull
    private final List<OcsProjectWithResources> projects;
    @NonNull
    private final FragmentManager fragmentManager;

    public CardProjectsAdapter(@NonNull List<OcsProjectWithResources> projects, @NonNull FragmentManager fragmentManager) {
        this.projects = new ArrayList<>(projects.size());
        this.projects.addAll(projects);
        this.fragmentManager = fragmentManager;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return projects.get(position).getLocalId();
    }

    @NonNull
    @Override
    public CardProjectsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardProjectsViewHolder(ItemProjectBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CardProjectsViewHolder holder, int position) {
        final OcsProjectWithResources project = projects.get(position);
        holder.bind(project, (v) -> CardProjectResourcesDialog.newInstance(project.getName(), project.getResources()).show(fragmentManager, CardProjectResourcesDialog.class.getSimpleName()));
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }
}
