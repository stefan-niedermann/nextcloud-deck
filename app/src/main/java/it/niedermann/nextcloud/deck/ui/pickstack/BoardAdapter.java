package it.niedermann.nextcloud.deck.ui.pickstack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemPickStackBoardBinding;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class BoardAdapter extends ArrayAdapter<Board> {

    @NonNull
    private final LayoutInflater inflater;

    @SuppressWarnings("WeakerAccess")
    public BoardAdapter(@NonNull Context context) {
        super(context, R.layout.item_pick_stack_account);
        setDropDownViewResource(R.layout.item_pick_stack_account);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        return Objects.requireNonNull(getItem(position)).getLocalId();
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        final ItemPickStackBoardBinding binding;
        if (convertView == null) {
            binding = ItemPickStackBoardBinding.inflate(inflater, parent, false);
        } else {
            binding = ItemPickStackBoardBinding.bind(convertView);
        }

        final Board item = getItem(position);
        if (item != null) {
            binding.boardTitle.setText(item.getTitle());
            binding.avatar.setImageDrawable(ViewUtil.getTintedImageView(binding.avatar.getContext(), R.drawable.circle_grey600_36dp, "#" + item.getColor()));
        } else {
            DeckLog.logError(new IllegalArgumentException("No item for position " + position));
        }
        return binding.getRoot();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
