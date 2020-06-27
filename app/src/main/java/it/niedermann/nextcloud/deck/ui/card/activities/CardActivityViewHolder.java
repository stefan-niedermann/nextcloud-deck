package it.niedermann.nextcloud.deck.ui.card.activities;

import android.content.Context;
import android.view.MenuInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemActivityBinding;
import it.niedermann.nextcloud.deck.model.enums.ActivityType;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.util.DateUtil;

import static it.niedermann.nextcloud.deck.util.ClipboardUtil.copyToClipboard;

public class CardActivityViewHolder extends RecyclerView.ViewHolder {
    public ItemActivityBinding binding;

    @SuppressWarnings("WeakerAccess")
    public CardActivityViewHolder(ItemActivityBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Activity activity, @NonNull MenuInflater inflater) {
        final Context context = itemView.getContext();
        binding.date.setText(DateUtil.getRelativeDateTimeString(context, activity.getLastModified().getTime()));
        binding.subject.setText(activity.getSubject());
        itemView.setOnClickListener(View::showContextMenu);
        itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            inflater.inflate(R.menu.activity_menu, menu);
            menu.findItem(android.R.id.copy).setOnMenuItemClickListener(item -> copyToClipboard(context, activity.getSubject()));
        });
        switch (ActivityType.findById(activity.getType())) {
            case CHANGE:
                binding.type.setImageResource(R.drawable.type_change_36dp);
                break;
            case ADD:
                binding.type.setImageResource(R.drawable.type_add_color_36dp);
                break;
            case DELETE:
                binding.type.setImageResource(R.drawable.type_delete_color_36dp);
                break;
            case ARCHIVE:
                binding.type.setImageResource(R.drawable.type_archive_grey600_36dp);
                break;
            case TAGGED_WITH_LABEL:
                binding.type.setImageResource(R.drawable.type_label_grey600_36dp);
                break;
            case COMMENT:
                binding.type.setImageResource(R.drawable.type_comment_grey600_36dp);
                break;
            case FILES:
                binding.type.setImageResource(R.drawable.type_file_36dp);
            case HISTORY:
                binding.type.setImageResource(R.drawable.type_file_36dp);
            case DECK:
            default:
                break;
        }
    }
}