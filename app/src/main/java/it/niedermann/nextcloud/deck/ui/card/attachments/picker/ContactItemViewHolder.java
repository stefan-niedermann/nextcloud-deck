package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import java.util.function.BiConsumer;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemPickerUserBinding;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static it.niedermann.nextcloud.deck.util.VCardUtil.getColorBasedOnDisplayName;

public class ContactItemViewHolder extends RecyclerView.ViewHolder {

    private final ItemPickerUserBinding binding;

    public ContactItemViewHolder(@NonNull ItemPickerUserBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Uri uri, @Nullable Bitmap image, @NonNull String displayName, @Nullable String contactInformation, @NonNull BiConsumer<Uri, Pair<String, RequestBuilder<?>>> onSelect) {
        itemView.setOnClickListener((v) -> onSelect.accept(uri, new Pair<>(displayName, image == null ? null : Glide.with(itemView.getContext()).load(image))));
        binding.title.setText(displayName);
        binding.contactInformation.setText(contactInformation);
        if (image == null) {
            binding.initials.setVisibility(VISIBLE);
            binding.initials.setText(TextUtils.isEmpty(displayName)
                    ? null
                    : String.valueOf(displayName.charAt(0))
            );
            Glide.with(itemView.getContext())
                    .load(new ColorDrawable(getColorBasedOnDisplayName(itemView.getContext(), displayName)))
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.avatar);
        } else {
            binding.initials.setVisibility(GONE);
            binding.initials.setText(null);
            Glide.with(itemView.getContext())
                    .load(image)
                    .placeholder(R.drawable.ic_person_grey600_24dp)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.avatar);
        }
    }

    public void bindError() {
        itemView.setOnClickListener(null);
        Glide.with(itemView.getContext())
                .load(R.drawable.ic_person_grey600_24dp)
                .into(binding.avatar);
    }
}
