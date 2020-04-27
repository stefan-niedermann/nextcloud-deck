package it.niedermann.nextcloud.deck.ui.filter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.databinding.ItemFilterLabelBinding;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.util.ColorUtil;

@SuppressWarnings("WeakerAccess")
public class LabelFilterAdapter extends RecyclerView.Adapter<LabelFilterAdapter.LabelViewHolder> {
    @NonNull
    private final List<Label> labels = new ArrayList<>();
    @NonNull
    private final List<Label> selectedLabels = new ArrayList<>();

    public LabelFilterAdapter(@NonNull List<Label> labels, @NonNull List<Label> selectedLabels) {
        super();
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
    public LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LabelViewHolder(ItemFilterLabelBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
        private ItemFilterLabelBinding binding;

        LabelViewHolder(@NonNull ItemFilterLabelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final Label label) {
            binding.label.setText(label.getTitle());
            final int labelColor = Color.parseColor("#" + label.getColor());
            binding.label.setChipBackgroundColor(ColorStateList.valueOf(labelColor));
            final int color = ColorUtil.getForegroundColorForBackgroundColor(labelColor);
            binding.label.setTextColor(color);
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
