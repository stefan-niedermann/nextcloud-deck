package it.niedermann.nextcloud.deck.ui.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Label;

public class LabelFilterAdapter extends RecyclerView.Adapter<LabelFilterAdapter.LabelViewHolder> {
    private Context context;
    private List<Label> labels;
    private List<Long> selectedLabelIds;

    public LabelFilterAdapter(@NonNull Context context, @NonNull List<Label> labels, @NonNull List<Long> selectedLabelIds) {
        super();
        this.context = context;
        this.labels = labels;
        this.selectedLabelIds = selectedLabelIds;
        setHasStableIds(true);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return labels.get(position).getLocalId();
    }

    @NonNull
    @Override
    public LabelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, viewGroup, false);
        return new LabelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelViewHolder multiViewHolder, int position) {
        multiViewHolder.bind(labels.get(position));
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    public List<Long> getSelected() {
        return selectedLabelIds;
    }

    class LabelViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        LabelViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.displayname);
            imageView = itemView.findViewById(R.id.avatar);
        }

        void bind(final Label label) {
            imageView.setVisibility(selectedLabelIds.contains(label.getLocalId()) ? View.VISIBLE : View.GONE);
            textView.setText(label.getTitle());

            itemView.setOnClickListener(view -> {
                if (selectedLabelIds.contains(label.getLocalId())) {
                    selectedLabelIds.remove(label.getLocalId());
                    itemView.setSelected(false);
                } else {
                    selectedLabelIds.add(label.getLocalId());
                    itemView.setSelected(true);
                }
            });
        }
    }
}
