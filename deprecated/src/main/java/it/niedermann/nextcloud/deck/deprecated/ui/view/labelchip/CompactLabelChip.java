package it.niedermann.nextcloud.deck.deprecated.ui.view.labelchip;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Label;

@SuppressLint("ViewConstructor")
public class CompactLabelChip extends LabelChip {

    public CompactLabelChip(@NonNull Context context, @NonNull Label label, @Px int gutter) {
        super(context, label, gutter);
        params.setFlexBasisPercent(1 / 6.5f);
        setHeight(getResources().getDimensionPixelSize(R.dimen.compact_label_height));
    }
}