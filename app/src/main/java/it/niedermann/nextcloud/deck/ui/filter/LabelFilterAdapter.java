package it.niedermann.nextcloud.deck.ui.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.util.ViewUtil;

@SuppressWarnings("WeakerAccess")
public class LabelFilterAdapter extends RecyclerView.Adapter<LabelFilterAdapter.LabelViewHolder> {
    @NonNull
    private final Context context;
    @NonNull
    private final List<Label> labels = new ArrayList<>();
    @NonNull
    private final List<Long> selectedLabelIds = new ArrayList<>();

    public LabelFilterAdapter(@NonNull Context context, @NonNull List<Label> labels, @NonNull List<Long> selectedLabelIds) {
        super();
        this.context = context;
        this.labels.addAll(labels);
        this.selectedLabelIds.addAll(selectedLabelIds);
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_label, viewGroup, false);
        return new LabelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelViewHolder viewHolder, int position) {
        viewHolder.bind(labels.get(position));
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    public List<Long> getSelected() {
        return selectedLabelIds;
    }

    class LabelViewHolder extends RecyclerView.ViewHolder {

        // TODO Use ViewBinding
        private TextView textView;
        private ImageView imageView;

        LabelViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.displayname);
            imageView = itemView.findViewById(R.id.label);
        }

        void bind(final Label label) {
            imageView.setImageDrawable(ViewUtil.getTintedImageView(imageView.getContext(), R.drawable.ic_label_grey600_24dp, "#" + label.getColor()));
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
