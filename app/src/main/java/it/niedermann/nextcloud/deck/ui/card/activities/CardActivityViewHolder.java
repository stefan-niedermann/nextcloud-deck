package it.niedermann.nextcloud.deck.ui.card.activities;

import android.content.Context;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.android.util.ClipboardUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemActivityBinding;
import it.niedermann.nextcloud.deck.model.enums.ActivityType;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.ui.theme.DeckViewThemeUtils;
import it.niedermann.nextcloud.deck.util.DateUtil;

public class CardActivityViewHolder extends RecyclerView.ViewHolder {
    public ItemActivityBinding binding;

    @SuppressWarnings("WeakerAccess")
    public CardActivityViewHolder(ItemActivityBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Activity activity, @NonNull MenuInflater inflater) {
        final var context = itemView.getContext();
        binding.date.setText(DateUtil.getRelativeDateTimeString(context, activity.getLastModified().toEpochMilli()));
        binding.subject.setText(activity.getSubject());
        itemView.setOnClickListener(View::showContextMenu);
        itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            inflater.inflate(R.menu.activity_menu, menu);
            menu.findItem(android.R.id.copy).setOnMenuItemClickListener(item -> ClipboardUtil.copyToClipboard(context, activity.getSubject()));
        });
        final var type = ActivityType.findById(activity.getType());
        setImageResource(binding.type, type);
        setImageColor(context, binding.type, type);
    }

    private static void setImageResource(@NonNull ImageView imageView, @NonNull ActivityType type) {
        switch (type) {
            case CHANGE -> imageView.setImageResource(R.drawable.type_change_36dp);
            case ADD -> imageView.setImageResource(R.drawable.type_add_color_36dp);
            case DELETE -> imageView.setImageResource(R.drawable.type_delete_color_36dp);
            case ARCHIVE -> imageView.setImageResource(R.drawable.type_archive_grey600_36dp);
            case TAGGED_WITH_LABEL ->
                    imageView.setImageResource(R.drawable.type_label_grey600_36dp);
            case COMMENT -> imageView.setImageResource(R.drawable.type_comment_36dp);
            case FILES -> imageView.setImageResource(R.drawable.type_file_36dp);
            case HISTORY -> imageView.setImageResource(R.drawable.type_history_36dp);
            default -> imageView.setImageResource(R.drawable.ic_app_logo);
        }
    }

    private static void setImageColor(@NonNull Context context, @NonNull ImageView imageView, @NonNull ActivityType type) {
        switch (type) {
            case ADD ->
                    DeckViewThemeUtils.setImageColor(context, imageView, R.color.activity_create);
            case DELETE ->
                    DeckViewThemeUtils.setImageColor(context, imageView, R.color.activity_delete);
            default -> DeckViewThemeUtils.setImageColor(context, imageView, R.color.grey600);
        }
    }
}