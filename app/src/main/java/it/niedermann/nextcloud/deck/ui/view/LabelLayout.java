package it.niedermann.nextcloud.deck.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import com.google.android.flexbox.FlexboxLayout;

import java.util.LinkedList;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Label;

import static it.niedermann.nextcloud.deck.util.DimensionUtil.dpToPx;

public class LabelLayout extends FlexboxLayout {

    @Px
    protected int gutter;
    @NonNull
    private List<LabelChip> chipList = new LinkedList<>();

    public LabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gutter = dpToPx(context, R.dimen.spacer_1hx);
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
            final Label existingLabel = currentChip.getLabel();
            for (Label label : labels) {
                if (existingLabel.equals(label)) {
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
                final Label existingLabel = currentChip.getLabel();
                if (existingLabel.equals(label)) {
                    continue labelList;
                }
            }
            LabelChip chip = createLabelChip(label);
            addView(chip);
            chipList.add(chip);
        }
    }

    protected LabelChip createLabelChip(@NonNull Label label) {
        return new LabelChip(getContext(), label, gutter);
    }
}
