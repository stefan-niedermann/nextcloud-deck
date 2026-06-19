package it.niedermann.nextcloud.deck.ui.card.activities;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.MenuInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nextcloud.android.common.ui.theme.utils.ColorRole;

import java.util.function.Function;

import it.niedermann.android.util.ClipboardUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemActivityBinding;
import it.niedermann.nextcloud.deck.model.enums.ActivityType;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.util.DateUtil;

public class CardActivityViewHolder extends RecyclerView.ViewHolder {
    public ItemActivityBinding binding;

    @SuppressWarnings("WeakerAccess")
    public CardActivityViewHolder(ItemActivityBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Activity activity, @NonNull MenuInflater inflater, @Nullable ThemeUtils utils) {
        final var context = itemView.getContext();
        binding.date.setText(DateUtil.getRelativeDateTimeString(context, activity.getLastModified().toEpochMilli()));
        binding.subject.setText(activity.getSubject());
        itemView.setOnClickListener(View::showContextMenu);
        itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            inflater.inflate(R.menu.activity_menu, menu);
            menu.findItem(android.R.id.copy).setOnMenuItemClickListener(item -> ClipboardUtil.copyToClipboard(context, activity.getSubject()));
        });

        final var type = ActivityType.findById(activity.getType());
        bindImageResource(type);
        if (utils != null) {
            applyTheme(context, utils, type);
        }
    }

    private void bindImageResource(@NonNull ActivityType type) {
        switch (type) {
            case CHANGE -> binding.type.setImageResource(R.drawable.type_change_36dp);
            case ADD -> binding.type.setImageResource(R.drawable.type_add_color_36dp);
            case DELETE -> binding.type.setImageResource(R.drawable.type_delete_color_36dp);
            case ARCHIVE -> binding.type.setImageResource(R.drawable.type_archive_36dp);
            case TAGGED_WITH_LABEL -> binding.type.setImageResource(R.drawable.type_label_36dp);
            case COMMENT -> binding.type.setImageResource(R.drawable.type_comment_36dp);
            case FILES -> binding.type.setImageResource(R.drawable.type_file_36dp);
            case HISTORY -> binding.type.setImageResource(R.drawable.type_history_36dp);
            default -> binding.type.setImageResource(R.drawable.ic_app_logo);
        }
    }

    private void applyTheme(@NonNull Context context, @NonNull ThemeUtils utils, @NonNull ActivityType type) {
        utils.platform.colorTextView(binding.subject, ColorRole.ON_SURFACE);
        utils.platform.colorTextView(binding.date, ColorRole.ON_SURFACE_VARIANT);

        final Function<Integer, ColorStateList> getColor = color ->
                ColorStateList.valueOf(ContextCompat.getColor(context, color));

        switch (type) {
            case ADD -> binding.type.setImageTintList(getColor.apply(R.color.activity_create));
            case DELETE -> binding.type.setImageTintList(getColor.apply(R.color.activity_delete));
            default -> utils.platform.colorImageView(binding.type, ColorRole.ON_SURFACE_VARIANT);
        }
    }
}