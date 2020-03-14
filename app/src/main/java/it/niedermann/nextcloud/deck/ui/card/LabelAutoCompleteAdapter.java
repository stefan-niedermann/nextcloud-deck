package it.niedermann.nextcloud.deck.ui.card;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAutocompleteDropdownBinding;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.util.AutoCompleteAdapter;
import it.niedermann.nextcloud.deck.util.ViewUtil;

public class LabelAutoCompleteAdapter extends AutoCompleteAdapter<Label> {
    @Nullable
    private Label createLabel;
    private String lastFilterText;
    private boolean canManage = false;

    LabelAutoCompleteAdapter(@NonNull ComponentActivity activity, long accountId, long boardId, long cardId) {
        super(activity, accountId, boardId, cardId);
        syncManager.getFullBoardById(accountId, boardId).observe(activity, (fullBoard) -> {
            if (fullBoard.getBoard().isPermissionManage()) {
                canManage = true;
                createLabel = new Label();
                createLabel.setLocalId(ITEM_CREATE);
                createLabel.setBoardId(boardId);
                createLabel.setAccountId(accountId);
                createLabel.setColor(Integer.toHexString(this.activity.getResources().getColor(R.color.grey600)));
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder<ItemAutocompleteDropdownBinding> holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            ItemAutocompleteDropdownBinding binding = ItemAutocompleteDropdownBinding.inflate(inflater, parent, false);
            holder = new ViewHolder<>(binding);
            convertView = binding.getRoot();
            convertView.setTag(holder);
        }

        int iconResource = R.drawable.ic_label_grey600_24dp;
        if ((lastFilterText != null && lastFilterText.length() > 0) && (position == itemList.size() - (canManage ? 1 : 0))) {
            iconResource = R.drawable.ic_plus;
        }

        holder.binding.icon.setImageDrawable(
                ViewUtil.getTintedImageView(activity, iconResource, "#" + getItem(position).getColor()
                ));
        holder.binding.label.setText(getItem(position).getTitle());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new AutoCompleteFilter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    lastFilterText = constraint.toString();
                    activity.runOnUiThread(() -> {
                        LiveData<List<Label>> liveData = constraint.toString().trim().length() > 0
                                ? syncManager.searchNotYetAssignedLabelsByTitle(accountId, boardId, cardId, constraint.toString())
                                : syncManager.findProposalsForLabelsToAssign(accountId, boardId, cardId, activity.getResources().getInteger(R.integer.max_labels_suggested));
                        liveData.observe(activity, (labels -> {
                            labels.removeAll(itemsToExclude);
                            final boolean constraintLengthGreaterZero = constraint.toString().trim().length() > 0;
                            if (canManage && constraintLengthGreaterZero) {
                                if (createLabel == null) {
                                    throw new IllegalStateException("Owner has right to edit card, but createLable is null");
                                }
                                createLabel.setTitle(String.format(activity.getString(R.string.label_add), constraint));
                                labels.add(createLabel);
                            }
                            filterResults.values = labels;
                            filterResults.count = labels.size();
                            publishResults(constraint, filterResults);
                        }));
                    });
                }
                return filterResults;
            }
        };
    }

    String getLastFilterText() {
        return this.lastFilterText;
    }
}
