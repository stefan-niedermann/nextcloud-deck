package it.niedermann.nextcloud.deck.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Label;

public class LabelLayout extends FlexboxLayout {

    public LabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instead of clearing and adding all labels, one can use this method to avoid flickering
     */
    public void updateLabels(@NonNull List<Label> labels) {
        addNewLabels(labels);
        removeObsoleteLabels(labels);
    }

    /**
     * Remove all labels from the view which are not in the labels list
     */
    private void removeObsoleteLabels(List<Label> labels) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            final View existingView = getChildAt(i);
            if (existingView instanceof LabelChip) {
                final Long existingLabelLocalId = ((LabelChip) existingView).getLabelLocalId();
                boolean idExistsInList = false;
                for (Label label : labels) {
                    if (existingLabelLocalId.equals(label.getLocalId())) {
                        idExistsInList = true;
                        break;
                    }
                }
                if (!idExistsInList) {
                    removeViewAt(i);
                }
            } else {
                DeckLog.logError(new IllegalStateException("binding.labels should only contain child view of type " + LabelChip.class.getCanonicalName()));
            }
        }
    }

    /**
     * Add all labels to the view which are not yet in the view but in the labels list
     */
    private void addNewLabels(List<Label> labels) {
        for (Label label : labels) {
            boolean viewContainsLabel = false;
            for (int i = 0; i < getChildCount(); i++) {
                final View existingView = getChildAt(i);
                if (existingView instanceof LabelChip) {
                    final Long existingLabelLocalId = ((LabelChip) existingView).getLabelLocalId();
                    if (existingLabelLocalId.equals(label.getLocalId())) {
                        viewContainsLabel = true;
                        break;
                    }
                } else {
                    DeckLog.logError(new IllegalStateException("binding.labels should only contain child view of type " + LabelChip.class.getCanonicalName()));
                }
            }
            if (!viewContainsLabel) {
                addView(new LabelChip(getContext(), label));
            }
        }
    }
}
