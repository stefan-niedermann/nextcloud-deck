package it.niedermann.nextcloud.deck.ui.card;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Random;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAutocompleteLabelBinding;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.util.AutoCompleteAdapter;
import it.niedermann.nextcloud.deck.util.ColorUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;

public class LabelAutoCompleteAdapter extends AutoCompleteAdapter<Label> {
    @Nullable
    private Label createLabel;
    private String lastFilterText;
    private boolean canManage = false;

    public LabelAutoCompleteAdapter(@NonNull ComponentActivity activity, long accountId, long boardId, long cardId) {
        super(activity, accountId, boardId, cardId);
        final String[] colors = activity.getResources().getStringArray(R.array.board_default_colors);
        final String createLabelColor = colors[new Random().nextInt(colors.length)].substring(1);
        observeOnce(syncManager.getFullBoardById(accountId, boardId), activity, (fullBoard) -> {
            if (fullBoard.getBoard().isPermissionManage()) {
                canManage = true;
                createLabel = new Label();
                createLabel.setLocalId(ITEM_CREATE);
                createLabel.setBoardId(boardId);
                createLabel.setAccountId(accountId);
                createLabel.setColor(createLabelColor);
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemAutocompleteLabelBinding binding;

        if (convertView != null) {
            binding = ItemAutocompleteLabelBinding.bind(convertView);
        } else {
            binding = ItemAutocompleteLabelBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        }

        final Label label = getItem(position);
        final int labelColor = Color.parseColor("#" + label.getColor());
        final int color = ColorUtil.getForegroundColorForBackgroundColor(labelColor);

        binding.label.setText(label.getTitle());
        binding.label.setChipBackgroundColor(ColorStateList.valueOf(labelColor));
        binding.label.setTextColor(color);

        if (ITEM_CREATE == label.getLocalId()) {
            final Drawable plusIcon = DrawableCompat.wrap(binding.label.getContext().getResources().getDrawable(R.drawable.ic_plus));
            DrawableCompat.setTint(plusIcon, color);
            binding.label.setChipIcon(plusIcon);
        } else {
            binding.label.setChipIcon(null);
        }

        return binding.getRoot();
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
                                : syncManager.findProposalsForLabelsToAssign(accountId, boardId, cardId);
                        observeOnce(liveData, activity, (labels -> {
                            labels.removeAll(itemsToExclude);
                            final boolean constraintLengthGreaterZero = constraint.toString().trim().length() > 0;
                            if (canManage && constraintLengthGreaterZero) {
                                if (createLabel == null) {
                                    throw new IllegalStateException("Owner has right to edit card, but createLabel is null");
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

    public String getLastFilterText() {
        return this.lastFilterText;
    }
}
