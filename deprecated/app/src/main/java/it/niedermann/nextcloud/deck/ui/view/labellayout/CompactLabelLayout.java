package it.niedermann.nextcloud.deck.ui.view.labellayout;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.ui.view.labelchip.CompactLabelChip;
import it.niedermann.nextcloud.deck.ui.view.labelchip.LabelChip;

public class CompactLabelLayout extends LabelLayout {

    public CompactLabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected LabelChip createLabelChip(@NonNull Label label) {
        return new CompactLabelChip(getContext(), label, gutter);
    }
}
