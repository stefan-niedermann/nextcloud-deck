package it.niedermann.nextcloud.deck.ui.preparecreate;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.databinding.ItemPrepareCreateStackBinding;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.ui.theme.Themed;

public class PickStackAdapter extends RecyclerView.Adapter<PickStackViewHolder> implements Themed {

    @Nullable
    @ColorInt
    Integer color;
    @Nullable
    private Stack selectedStack = null;
    private final List<Stack> stacks = new ArrayList<>();
    @NonNull
    private final Consumer<Stack> onStackSelectedListener;

    public PickStackAdapter(@NonNull Consumer<Stack> onStackSelectedListener) {
        this.onStackSelectedListener = onStackSelectedListener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public PickStackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PickStackViewHolder(ItemPrepareCreateStackBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PickStackViewHolder holder, int position) {
        holder.bind(stacks.get(position), stack -> {
            setSelection(stack);
            onStackSelectedListener.accept(selectedStack);
        }, selectedStack, color);
    }

    @Override
    public int getItemCount() {
        return stacks.size();
    }

    @Override
    public long getItemId(int position) {
        return stacks.get(position).getLocalId();
    }

    public void setStacks(@NonNull Collection<Stack> stacks) {
        this.stacks.clear();
        this.selectedStack = null;
        this.stacks.addAll(stacks);
        notifyDataSetChanged();
    }

    public void setSelection(@NonNull Stack stack) {
        final var previousPosition = getPosition(selectedStack);
        final var nextPosition = getPosition(stack);
        this.selectedStack = stack;
        previousPosition.ifPresent(this::notifyItemChanged);
        nextPosition.ifPresent(this::notifyItemChanged);
    }

    private Optional<Integer> getPosition(@Nullable Stack stack) {
        if (stack == null) {
            return Optional.empty();
        }

        for (int i = 0; i < stacks.size(); i++) {
            if (stacks.get(i).getLocalId().equals(stack.getLocalId())) {
                return Optional.of(i);
            }
        }

        return Optional.empty();
    }

    @Override
    public void applyTheme(int color) {
        this.color = color;
        notifyDataSetChanged();
    }
}
