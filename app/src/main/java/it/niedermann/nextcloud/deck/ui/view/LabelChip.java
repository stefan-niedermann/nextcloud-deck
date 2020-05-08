package it.niedermann.nextcloud.deck.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.util.ColorUtil;

@SuppressLint("ViewConstructor")
public class LabelChip extends Chip {

    private final Label label;

    public LabelChip(@NonNull Context context, @NonNull Label label) {
        super(context);
        this.label = label;

        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, 0, Math.round(context.getResources().getDimension(R.dimen.spacer_1x)), 0);
        setLayoutParams(params);
        setEnsureMinTouchTargetSize(false);

        setText(label.getTitle());
        setEllipsize(TextUtils.TruncateAt.END);

        try {
            int labelColor = Color.parseColor("#" + label.getColor());
            ColorStateList c = ColorStateList.valueOf(labelColor);
            setChipBackgroundColor(c);
            setTextColor(ColorUtil.getForegroundColorForBackgroundColor(labelColor));
        } catch (IllegalArgumentException e) {
            DeckLog.logError(e);
        }
    }

    public Label getLabel() {
        return this.label;
    }
}