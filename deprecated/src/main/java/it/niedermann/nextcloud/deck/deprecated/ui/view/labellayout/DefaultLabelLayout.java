package it.niedermann.nextcloud.deck.deprecated.ui.view.labellayout;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.deprecated.ui.view.labelchip.DefaultLabelChip;

public class DefaultLabelLayout extends LabelLayout {

    public DefaultLabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected DefaultLabelChip createLabelChip(@NonNull Label label) {
        return new DefaultLabelChip(getContext(), label, gutter);
    }
}
