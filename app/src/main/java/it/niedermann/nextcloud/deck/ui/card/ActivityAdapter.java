package it.niedermann.nextcloud.deck.ui.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemActivityBinding;
import it.niedermann.nextcloud.deck.model.enums.ActivityType;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.util.DateUtil;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivitiesViewHolder> {

    @NonNull
    private List<Activity> activities;
    private Context context;

    public ActivityAdapter(@NonNull List<Activity> activities) {
        super();
        this.activities = activities;
    }

    @NonNull
    @Override
    public ActivitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        ItemActivityBinding binding = ItemActivityBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ActivitiesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivitiesViewHolder holder, int position) {
        Activity activity = activities.get(position);
        holder.binding.date.setText(DateUtil.getRelativeDateTimeString(context, activity.getLastModified().getTime()));
        holder.binding.subject.setText(activity.getSubject());
        switch (ActivityType.findById(activity.getType())) {
            case DECK:
                break;
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
                break;
            case HISTORY:
                break;
            case FILES:
                break;
            case COMMENT:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    static class ActivitiesViewHolder extends RecyclerView.ViewHolder {
        ItemActivityBinding binding;

        private ActivitiesViewHolder(ItemActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
