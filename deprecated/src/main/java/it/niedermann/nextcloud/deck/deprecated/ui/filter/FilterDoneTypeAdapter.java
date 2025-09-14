package it.niedermann.nextcloud.deck.deprecated.ui.filter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

import it.niedermann.nextcloud.deck.databinding.ItemFilterDonetypeBinding;
import it.niedermann.nextcloud.deck.model.enums.EDoneType;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.Themed;

public class FilterDoneTypeAdapter extends RecyclerView.Adapter<FilterDoneTypeAdapter.DoneTypeViewHolder> {
    @NonNull
    private final EDoneType[] doneTypes = EDoneType.values();
    private int selectedDoneTypePosition;
    @Nullable
    private final SelectionListener<EDoneType> selectionListener;
    @ColorInt
    private final int color;

    @SuppressWarnings("WeakerAccess")
    public FilterDoneTypeAdapter(@NonNull EDoneType selectedDoneType, @Nullable SelectionListener<EDoneType> selectionListener, @ColorInt int color) {
        super();
        this.selectedDoneTypePosition = Arrays.binarySearch(doneTypes, selectedDoneType);
        this.selectionListener = selectionListener;
        this.color = color;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public DoneTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DoneTypeViewHolder(ItemFilterDonetypeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DoneTypeViewHolder viewHolder, int position) {
        viewHolder.bind(doneTypes[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return doneTypes.length;
    }

    class DoneTypeViewHolder extends RecyclerView.ViewHolder implements Themed {
        private final ItemFilterDonetypeBinding binding;

        DoneTypeViewHolder(@NonNull ItemFilterDonetypeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final EDoneType doneType) {
            binding.doneType.setText(doneType.toString(binding.doneType.getContext()));
            itemView.setSelected(doneTypes[selectedDoneTypePosition].equals(doneType));
            applyTheme(color);

            itemView.setOnClickListener(view -> {
                final int oldSelection = selectedDoneTypePosition;
                if (doneTypes[selectedDoneTypePosition].equals(doneType)) {
                    selectedDoneTypePosition = Arrays.binarySearch(doneTypes, EDoneType.NO_FILTER);
                    itemView.setSelected(false);
                    if (selectionListener != null) {
                        selectionListener.onItemSelected(EDoneType.NO_FILTER);
                    }
                    notifyItemChanged(selectedDoneTypePosition);
                } else {
                    selectedDoneTypePosition = Arrays.binarySearch(doneTypes, doneType);
                    itemView.setSelected(true);
                    if (selectionListener != null) {
                        selectionListener.onItemSelected(doneType);
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
