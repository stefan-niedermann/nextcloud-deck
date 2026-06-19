package it.niedermann.nextcloud.deck.ui.filter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

import it.niedermann.nextcloud.deck.databinding.ItemFilterDuetypeBinding;
import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.Themed;

public class FilterDueTypeAdapter extends RecyclerView.Adapter<FilterDueTypeAdapter.DueTypeViewHolder> {
    @NonNull
    private final EDueType[] dueTypes = EDueType.values();
    private int selectedDueTypePosition;
    @Nullable
    private final SelectionListener<EDueType> selectionListener;
    @ColorInt
    private final int color;

    @SuppressWarnings("WeakerAccess")
    public FilterDueTypeAdapter(@NonNull EDueType selectedDueType, @Nullable SelectionListener<EDueType> selectionListener, @ColorInt int color) {
        super();
        this.selectedDueTypePosition = Arrays.binarySearch(dueTypes, selectedDueType);
        this.selectionListener = selectionListener;
        this.color = color;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public DueTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DueTypeViewHolder(ItemFilterDuetypeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DueTypeViewHolder viewHolder, int position) {
        viewHolder.bind(dueTypes[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dueTypes.length;
    }

    class DueTypeViewHolder extends RecyclerView.ViewHolder implements Themed {
        private final ItemFilterDuetypeBinding binding;

        DueTypeViewHolder(@NonNull ItemFilterDuetypeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final EDueType dueType) {
            binding.dueType.setText(dueType.toString(binding.dueType.getContext()));
            itemView.setSelected(dueTypes[selectedDueTypePosition].equals(dueType));
            applyTheme(color);

            itemView.setOnClickListener(view -> {
                final int oldSelection = selectedDueTypePosition;
                if (dueTypes[selectedDueTypePosition].equals(dueType)) {
                    selectedDueTypePosition = Arrays.binarySearch(dueTypes, EDueType.NO_FILTER);
                    itemView.setSelected(false);
                    if (selectionListener != null) {
                        selectionListener.onItemSelected(EDueType.NO_FILTER);
                    }
                    notifyItemChanged(selectedDueTypePosition);
                } else {
                    selectedDueTypePosition = Arrays.binarySearch(dueTypes, dueType);
                    itemView.setSelected(true);
                    if (selectionListener != null) {
                        selectionListener.onItemSelected(dueType);
                    }
                }
                notifyItemChanged(oldSelection);
            });
        }

        @Override
        public void applyTheme(int color) {
            final var utils = ThemeUtils.of(color, itemView.getContext());
            utils.deck.themeSelectedCheck(binding.selectedCheck.getContext(), binding.selectedCheck.getDrawable());
        }
    }
}
