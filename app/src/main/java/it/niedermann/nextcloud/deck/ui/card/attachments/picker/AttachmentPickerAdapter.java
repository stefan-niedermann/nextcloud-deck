package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.nextcloud.android.common.ui.theme.utils.ColorRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.databinding.ItemAttachmentPickerTypeBinding;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.Themed;

@SuppressWarnings("WeakerAccess")
public class AttachmentPickerAdapter extends RecyclerView.Adapter<AttachmentPickerAdapter.AttachmentPickerViewHolder> implements Themed {

    @Nullable
    @ColorInt
    private Integer color;
    @NonNull
    private final List<AttachmentPicker<?, ?>> picker = new ArrayList<>();
    @NonNull
    private final Consumer<CompletableFuture<List<Uri>>> resultConsumer;

    public AttachmentPickerAdapter(
            @NonNull Collection<AttachmentPicker<?, ?>> pickers,
            @NonNull Consumer<CompletableFuture<List<Uri>>> resultConsumer
    ) {
        super();
        this.picker.addAll(pickers);
        this.resultConsumer = resultConsumer;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public AttachmentPickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AttachmentPickerViewHolder(ItemAttachmentPickerTypeBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentPickerViewHolder holder, int position) {
        holder.bind(picker.get(position), resultConsumer);
        if (color != null) {
            holder.applyTheme(color);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return picker.size();
    }

    @Override
    public void applyTheme(@ColorInt int color) {
        this.color = color;
        notifyDataSetChanged();
    }

    public static class AttachmentPickerViewHolder extends RecyclerView.ViewHolder implements Themed {

        private final ItemAttachmentPickerTypeBinding binding;

        private AttachmentPickerViewHolder(@NonNull ItemAttachmentPickerTypeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(@NonNull AttachmentPicker<?, ?> picker,
                         @NonNull Consumer<CompletableFuture<List<Uri>>> resultConsumer) {
            binding.icon.setImageResource(picker.icon);
            binding.label.setText(picker.label);
            binding.getRoot().setOnClickListener((v) -> {
                final var context = binding.getRoot().getContext();
                final var result = picker.ensurePermissionsAndLaunchPicker(context);
                resultConsumer.accept(result);
            });
        }

        @Override
        public void applyTheme(int color) {
            final var utils = ThemeUtils.of(color, binding.getRoot().getContext());

            utils.deck.colorImageViewBackgroundAndIconSecondary(binding.icon);
            utils.platform.colorTextView(binding.label, ColorRole.ON_SURFACE);
        }
    }
}
