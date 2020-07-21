package it.niedermann.nextcloud.deck.ui.filter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemFilterLabelBinding;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.util.ColorUtil;

@SuppressWarnings("WeakerAccess")
public class FilterLabelsAdapter extends RecyclerView.Adapter<FilterLabelsAdapter.LabelViewHolder> {
    @NonNull
    private final List<Label> labels = new ArrayList<>();
    @NonNull
    private final List<Label> selectedLabels = new ArrayList<>();
    @Nullable
    private static final Label NOT_ASSIGNED = null;
    @Nullable
    private final SelectionListener<Label> selectionListener;

    public FilterLabelsAdapter(@NonNull List<Label> labels, @NonNull List<Label> selectedLabels, boolean noAssignedLabel, @Nullable SelectionListener<Label> selectionListener) {
        super();
        this.labels.addAll(labels);
        if (noAssignedLabel) {
            this.selectedLabels.add(NOT_ASSIGNED);
        }
        this.selectedLabels.addAll(selectedLabels);
        this.selectionListener = selectionListener;
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
        final Label label = labels.get(position);
        viewHolder.bind(labels.get(position));
        if (position == 0) {
            viewHolder.bindNotAssigned(label);
        } else {
            viewHolder.bind(label);
        }
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
            bindClickListener(label);
        }

        public void bindNotAssigned(Label label) {
            binding.label.setText(itemView.getContext().getString(R.string.no_assigned_label));
            binding.label.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.accent)));
            binding.label.setChipIcon(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_baseline_block_24));
            binding.label.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), android.R.color.transparent)));
            binding.label.setRippleColor(null);
            bindClickListener(label);
        }

        private void bindClickListener(@Nullable Label label) {
            itemView.setOnClickListener(view -> {
                if (selectedLabels.contains(label)) {
                    selectedLabels.remove(label);
                    itemView.setSelected(false);
                    if (selectionListener != null) {
                        selectionListener.onItemDeselected(label);
                    }
                } else {
                    selectedLabels.add(label);
                    itemView.setSelected(true);
                    if (selectionListener != null) {
                        selectionListener.onItemSelected(label);
                    }
                }
            });
        }
    }
}
