package it.niedermann.nextcloud.deck.ui.board.managelabels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemManageLabelBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.ui.branding.Branded;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;

public class ManageLabelsAdapter extends RecyclerView.Adapter<ManageLabelsViewHolder> implements Branded {

    private int mainColor;

    @NonNull
    private Account account;
    @NonNull
    private List<Label> labels = new LinkedList<>();
    @NonNull
    private Context context;

    ManageLabelsAdapter(@NonNull Account account, @NonNull Context context) {
        this.account = account;
        this.context = context;
        this.mainColor = context.getResources().getColor(R.color.primary);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (labels.size() > position) {
            return labels.get(position).getLocalId();
        }
        throw new NoSuchElementException("Current list contains only " + labels.size() + " elements.");
    }

    @NonNull
    @Override
    public ManageLabelsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemManageLabelBinding binding = ItemManageLabelBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ManageLabelsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageLabelsViewHolder holder, int position) {
        holder.bind(labels.get(position));
    }


    @Override
    public int getItemCount() {
        return labels.size();
    }

    public void remove(Label label) {
        final int index = this.labels.indexOf(label);
        if (this.labels.remove(label)) {
            notifyItemRemoved(index);
        }
    }

    public void update(@NonNull List<Label> labels) {
        this.labels.clear();
        this.labels.addAll(labels);
        notifyDataSetChanged();
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {
        if (Application.isBrandingEnabled(context)) {
            this.mainColor = BrandedActivity.getSecondaryForegroundColorDependingOnTheme(context, mainColor);
            notifyDataSetChanged();
        }
    }
}
