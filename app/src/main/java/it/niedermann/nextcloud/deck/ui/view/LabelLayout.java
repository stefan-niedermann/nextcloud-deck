package it.niedermann.nextcloud.deck.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.google.android.flexbox.FlexboxLayout;

import java.util.LinkedList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Label;

public class LabelLayout extends FlexboxLayout {

    private List<LabelChip> labelList = new LinkedList<>();

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

    /**
     * Remove all labels from the view which are not in the labels list
     */
    private void removeObsoleteLabels(List<Label> labels) {
        labelList:
        for (int i = 0; i < labelList.size(); i++) {
            LabelChip currentChip = labelList.get(i);
            final Long existingLabelLocalId = currentChip.getLabelLocalId();
            for (Label label : labels) {
                if (existingLabelLocalId.equals(label.getLocalId())) {
                    continue labelList;
                }
            }
            removeViewAt(i);
            labelList.remove(currentChip);
            i--;
        }
    }

    /**
     * Add all labels to the view which are not yet in the view but in the labels list
     */
    private void addNewLabels(List<Label> labels) {
        int oldLabelSize = labelList.size();
        labelList:
        for (Label label : labels) {
            for (int i = 0; i < oldLabelSize; i++) {
                final LabelChip chip = labelList.get(i);
                final Long existingLabelLocalId = chip.getLabelLocalId();
                if (existingLabelLocalId.equals(label.getLocalId())) {
                    continue labelList;
                }
            }
            LabelChip chip = new LabelChip(getContext(), label);
            addView(chip);
            labelList.add(chip);
        }
    }
}
