package it.niedermann.nextcloud.deck.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.google.android.flexbox.FlexboxLayout;

import java.util.LinkedList;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Label;

public class LabelLayout extends FlexboxLayout {

    private List<LabelChip> chipList = new LinkedList<>();

    public LabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instead of clearing and adding all labels, one can use this method to avoid flickering
     */
    public void updateLabels(@NonNull List<Label> labels) {
        removeObsoleteLabels(labels);
        addNewLabels(labels);
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        this.chipList.clear();
    }

    @Override
    public void removeViewAt(int index) {
        // TODO this should also remove the item from chipList
        DeckLog.logError(new UnsupportedOperationException("Not implemented yet"));
        super.removeViewAt(index);
    }

    /**
     * Remove all labels from the view which are not in the labels list
     */
    private void removeObsoleteLabels(List<Label> labels) {
        chipList:
        for (int i = 0; i < chipList.size(); i++) {
            LabelChip currentChip = chipList.get(i);
            final Long existingLabelLocalId = currentChip.getLabelLocalId();
            for (Label label : labels) {
                if (existingLabelLocalId.equals(label.getLocalId())) {
                    continue chipList;
                }
            }
            super.removeViewAt(i);
            chipList.remove(currentChip);
            i--;
        }
    }

    /**
     * Add all labels to the view which are not yet in the view but in the labels list
     */
    private void addNewLabels(List<Label> labels) {
        int oldLabelSize = chipList.size();
        labelList:
        for (Label label : labels) {
            for (int i = 0; i < oldLabelSize; i++) {
                final LabelChip currentChip = chipList.get(i);
                final Long existingLabelLocalId = currentChip.getLabelLocalId();
                if (existingLabelLocalId.equals(label.getLocalId())) {
                    continue labelList;
                }
            }
            LabelChip chip = new LabelChip(getContext(), label);
            addView(chip);
            chipList.add(chip);
        }
    }
}
