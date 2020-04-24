package it.niedermann.nextcloud.deck.ui.filter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.util.ColorUtil;

@SuppressWarnings("WeakerAccess")
public class LabelFilterAdapter extends RecyclerView.Adapter<LabelFilterAdapter.LabelViewHolder> {
    @NonNull
    private final Context context;
    @NonNull
    private final List<Label> labels = new ArrayList<>();
    @NonNull
    private final List<Label> selectedLabels = new ArrayList<>();

    public LabelFilterAdapter(@NonNull Context context, @NonNull List<Label> labels, @NonNull List<Label> selectedLabels) {
        super();
        this.context = context;
        this.labels.addAll(labels);
        this.selectedLabels.addAll(selectedLabels);
        setHasStableIds(true);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return labels.get(position).getLocalId();
    }

    @NonNull
    @Override
    public LabelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_filter_label, viewGroup, false);
        return new LabelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelViewHolder viewHolder, int position) {
        viewHolder.bind(labels.get(position));
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    public List<Label> getSelected() {
        return selectedLabels;
    }

    class LabelViewHolder extends RecyclerView.ViewHolder {

        // TODO Use ViewBinding
        private Chip chip;

        LabelViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.label);
        }

        void bind(final Label label) {
            chip.setText(label.getTitle());
            final int labelColor = Color.parseColor("#" + label.getColor());
            chip.setChipBackgroundColor(ColorStateList.valueOf(labelColor));
            final int color = ColorUtil.getForegroundColorForBackgroundColor(labelColor);
            chip.setTextColor(color);
            itemView.setSelected(selectedLabels.contains(label));

            itemView.setOnClickListener(view -> {
                if (selectedLabels.contains(label)) {
                    selectedLabels.remove(label);
                    itemView.setSelected(false);
                } else {
                    selectedLabels.add(label);
                    itemView.setSelected(true);
                }
            });
        }
    }
}
