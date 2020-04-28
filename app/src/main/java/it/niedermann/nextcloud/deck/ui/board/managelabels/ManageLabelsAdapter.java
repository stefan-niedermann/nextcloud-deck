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
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.ui.branding.Branded;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;

public class ManageLabelsAdapter extends RecyclerView.Adapter<ManageLabelsViewHolder> implements Branded {

    private int mainColor;

    @NonNull
    private ManageLabelListener listener;
    @NonNull
    private List<Label> labels = new LinkedList<>();
    @NonNull
    private Context context;

    ManageLabelsAdapter(@NonNull ManageLabelListener listener, @NonNull Context context) {
        this.listener = listener;
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
        return new ManageLabelsViewHolder(ItemManageLabelBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ManageLabelsViewHolder holder, int position) {
        holder.bind(labels.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    public void remove(@NonNull Label label) {
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
