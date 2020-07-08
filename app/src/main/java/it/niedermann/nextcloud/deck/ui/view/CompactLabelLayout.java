package it.niedermann.nextcloud.deck.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.model.Label;

public class CompactLabelLayout extends LabelLayout {

    public CompactLabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected LabelChip createLabelChip(@NonNull Label label) {
        return new CompactLabelChip(getContext(), label, gutter);
    }
}
