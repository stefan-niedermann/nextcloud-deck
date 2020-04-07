package it.niedermann.nextcloud.deck.ui.card.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemActivityBinding;
import it.niedermann.nextcloud.deck.model.enums.ActivityType;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.util.DateUtil;

import static it.niedermann.nextcloud.deck.util.ClipboardUtil.copyToClipboard;

public class CardActivityAdapter extends RecyclerView.Adapter<CardActivityViewHolder> {

    @NonNull
    private final List<Activity> activities;
    @NonNull
    private final MenuInflater menuInflater;

    @SuppressWarnings("WeakerAccess")
    public CardActivityAdapter(@NonNull List<Activity> activities, @NonNull MenuInflater menuInflater) {
        super();
        this.activities = activities;
        this.menuInflater = menuInflater;
    }

    @NonNull
    @Override
    public CardActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        ItemActivityBinding binding = ItemActivityBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CardActivityViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardActivityViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();
        final Activity activity = activities.get(position);
        holder.binding.date.setText(DateUtil.getRelativeDateTimeString(context, activity.getLastModified().getTime()));
        holder.binding.subject.setText(activity.getSubject());
        holder.itemView.setOnClickListener(View::showContextMenu);
        holder.itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            menuInflater.inflate(R.menu.activity_menu, menu);
            menu.findItem(android.R.id.copy).setOnMenuItemClickListener(item -> copyToClipboard(context, activity.getSubject()));
        });
        switch (ActivityType.findById(activity.getType())) {
            case CHANGE:
                holder.binding.type.setImageResource(R.drawable.type_change_36dp);
                break;
            case ADD:
                holder.binding.type.setImageResource(R.drawable.type_add_color_36dp);
                break;
            case DELETE:
                holder.binding.type.setImageResource(R.drawable.type_delete_color_36dp);
                break;
            case ARCHIVE:
                holder.binding.type.setImageResource(R.drawable.type_archive_grey600_36dp);
                break;
            case DECK:
            case HISTORY:
            case FILES:
                break;
            case COMMENT:
                holder.binding.type.setImageResource(R.drawable.type_comment_grey600_36dp);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }
}
