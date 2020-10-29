package it.niedermann.nextcloud.deck.ui.view.labelchip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Label;

@SuppressLint("ViewConstructor")
public class LabelChip extends Chip {

    private final Label label;

    protected final FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    );

    public LabelChip(@NonNull Context context, @NonNull Label label, @Px int gutter) {
        super(context);
        this.label = label;

        params.setMargins(0, 0, gutter, 0);
        setLayoutParams(params);
        setEnsureMinTouchTargetSize(false);
        setMinHeight(0);
        setChipMinHeight(0);
        setPadding(0, gutter, 0, gutter);
        setChipStartPadding(gutter);
        setTextStartPadding(gutter);
        setTextEndPadding(gutter);
        setChipEndPadding(gutter);
        setClickable(false);

        try {
            int labelColor = label.getColor();
            ColorStateList c = ColorStateList.valueOf(labelColor);
            setChipBackgroundColor(c);
            setTextColor(ColorUtil.INSTANCE.getForegroundColorForBackgroundColor(labelColor));
        } catch (IllegalArgumentException e) {
            DeckLog.logError(e);
        }
    }

    public Label getLabel() {
        return this.label;
    }
}