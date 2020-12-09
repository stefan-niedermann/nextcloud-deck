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
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class CardActivityViewHolder extends RecyclerView.ViewHolder {
    public ItemActivityBinding binding;

    @SuppressWarnings("WeakerAccess")
    public CardActivityViewHolder(ItemActivityBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Activity activity, @NonNull MenuInflater inflater) {
        final Context context = itemView.getContext();
        binding.date.setText(DateUtil.getRelativeDateTimeString(context, activity.getLastModified().toEpochMilli()));
        binding.subject.setText(activity.getSubject());
        itemView.setOnClickListener(View::showContextMenu);
        itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            inflater.inflate(R.menu.activity_menu, menu);
            menu.findItem(android.R.id.copy).setOnMenuItemClickListener(item -> ClipboardUtil.INSTANCE.copyToClipboard(context, activity.getSubject()));
        });
        final ActivityType type = ActivityType.findById(activity.getType());
        setImageResource(binding.type, type);
        setImageColor(context, binding.type, type);
    }

    private static void setImageResource(@NonNull ImageView imageView, @NonNull ActivityType type) {
        switch (type) {
            case CHANGE:
                imageView.setImageResource(R.drawable.type_change_36dp);
                break;
            case ADD:
                imageView.setImageResource(R.drawable.type_add_color_36dp);
                break;
            case DELETE:
                imageView.setImageResource(R.drawable.type_delete_color_36dp);
                break;
            case ARCHIVE:
                imageView.setImageResource(R.drawable.type_archive_grey600_36dp);
                break;
            case TAGGED_WITH_LABEL:
                imageView.setImageResource(R.drawable.type_label_grey600_36dp);
                break;
            case COMMENT:
                imageView.setImageResource(R.drawable.type_comment_grey600_36dp);
                break;
            case FILES:
                imageView.setImageResource(R.drawable.type_file_36dp);
                break;
            case HISTORY:
                imageView.setImageResource(R.drawable.type_history_36dp);
                break;
            case DECK:
            default:
                imageView.setImageResource(R.drawable.ic_app_logo);
                break;
        }
    }

    private static void setImageColor(@NonNull Context context, @NonNull ImageView imageView, @NonNull ActivityType type) {
        switch (type) {
            case ADD:
                ViewUtil.setImageColor(context, imageView, R.color.activity_create);
                break;
            case DELETE:
                ViewUtil.setImageColor(context, imageView, R.color.activity_delete);
                break;
            default:
                ViewUtil.setImageColor(context, imageView, R.color.grey600);
                break;
        }
    }
}