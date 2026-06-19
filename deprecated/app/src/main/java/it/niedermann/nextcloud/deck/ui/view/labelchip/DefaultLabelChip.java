package it.niedermann.nextcloud.deck.ui.view.labelchip;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import it.niedermann.nextcloud.deck.model.Label;

import static android.text.TextUtils.TruncateAt.MIDDLE;

@SuppressLint("ViewConstructor")
public class DefaultLabelChip extends LabelChip {

    public DefaultLabelChip(@NonNull Context context, @NonNull Label label, @Px int gutter) {
        super(context, label, gutter);
        setText(label.getTitle());
        setEllipsize(MIDDLE);
    }
}