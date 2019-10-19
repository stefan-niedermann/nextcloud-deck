package it.niedermann.nextcloud.deck.ui.card;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindColor;
import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;

public class LabelAutoCompleteAdapter extends BaseAdapter implements Filterable {
    public static final long CREATE_ID = Long.MIN_VALUE;
    private Context context;
    private List<Label> labelList = new ArrayList<>();
    private SyncManager syncManager;
    private long accountId;
    private long boardId;
    private long cardId;
    private LifecycleOwner owner;
    private Label createLabel;
    private String lastFilterText;
    private boolean canManage = false;

    @BindInt(R.integer.max_labels_suggested)
    int maxLabelsSuggested;
    @BindString(R.string.label_add)
    String createLabelText;
    @BindColor(R.color.grey600)
    int createLabelColor;

    LabelAutoCompleteAdapter(@NonNull LifecycleOwner owner, Activity activity, long accountId, long boardId, long cardId) {
        ButterKnife.bind(this, activity);
        this.owner = owner;
        this.context = activity;
        this.accountId = accountId;
        this.boardId = boardId;
        this.cardId = cardId;
        syncManager = new SyncManager(activity);
        syncManager.getFullBoardById(accountId, boardId).observe(owner, (fullBoard) -> {
            if (fullBoard.getBoard().isPermissionManage()) {
                createLabel = new Label();
                createLabel.setLocalId(CREATE_ID);
                createLabel.setBoardId(boardId);
                createLabel.setAccountId(accountId);
                createLabel.setColor(Integer.toHexString(createLabelColor));
                canManage = true;
            }
        });
    }

    @Override
    public int getCount() {
        return labelList.size();
    }

    @Override
    public Label getItem(int position) {
        return labelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_autocomplete_dropdown, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        int iconResource = R.drawable.ic_label_grey600_24dp;
        if((lastFilterText != null && lastFilterText.length() > 0) &&  (position == labelList.size() - (canManage ? 1 : 0))) {
            iconResource = R.drawable.ic_plus;
        }

        holder.icon.setImageDrawable(
                ViewUtil.getTintedImageView(context, iconResource,"#" + getItem(position).getColor()
                ));
        holder.label.setText(getItem(position).getTitle());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            final FilterResults filterResults = new FilterResults();

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    lastFilterText = constraint.toString();
                    Objects.requireNonNull(((Fragment) owner).getActivity()).runOnUiThread(() -> {
                        LiveData<List<Label>> liveData = constraint.length() > 0
                                ? syncManager.searchLabelByTitle(accountId, boardId, constraint.toString())
                                : syncManager.findProposalsForLabelsToAssign(accountId, boardId, cardId, maxLabelsSuggested);
                        observeOnce(liveData, owner, labels -> {
                            if (canManage && constraint.length() > 0) {
                                createLabel.setTitle(String.format(createLabelText, constraint));
                            }
                            if (labels != null) {
                                if (canManage && constraint.length() > 0) {
                                    labels.add(createLabel);
                                }
                                filterResults.values = labels;
                                filterResults.count = labels.size();
                                publishResults(constraint, filterResults);
                            } else {
                                List<Label> createLabels = new ArrayList<>();
                                if (canManage && constraint.length() > 0) {
                                    createLabels.add(createLabel);
                                }
                                filterResults.values = createLabels;
                                filterResults.count = createLabels.size();
                            }
                        });
                    });
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    if (!labelList.equals(results.values)) {
                        labelList = (List<Label>) results.values;
                    }
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    String getLastFilterText() {
        return this.lastFilterText;
    }

    static class ViewHolder {
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.label)
        TextView label;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
