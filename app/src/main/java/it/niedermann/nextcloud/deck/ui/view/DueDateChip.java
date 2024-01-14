package it.niedermann.nextcloud.deck.ui.view;

import static java.time.temporal.ChronoUnit.DAYS;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.content.ContextCompat;

import com.google.android.material.chip.Chip;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import it.niedermann.android.util.DimensionUtil;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.util.DateUtil;

public class DueDateChip extends Chip {

    protected @ColorInt int colorOnSurface;

    public DueDateChip(Context context) {
        super(context);
        initialize();
    }

    public DueDateChip(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public DueDateChip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        setEnsureMinTouchTargetSize(false);
        setMinHeight(0);
        setChipMinHeight(0);
        @Px final var padding = DimensionUtil.INSTANCE.dpToPx(getContext(), R.dimen.spacer_1x);
        setPadding(padding, padding, padding, padding);
        setClickable(false);

        final var typedValue = new TypedValue();
        final var theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true);
        this.colorOnSurface = typedValue.data;
    }

    public void setDueDate(@NonNull Instant date, boolean isDone) {
        setText(DateUtil.getRelativeDateTimeString(getContext(), date.toEpochMilli()));

        @DrawableRes final int chipIconRes;
        @Nullable @ColorRes final Integer textColorRes;
        @ColorRes final int backgroundColorRes;

        if (isDone) { // Done
            chipIconRes = R.drawable.ic_check_white_24dp;
            backgroundColorRes = R.color.due_done;
            textColorRes = R.color.due_text_done;

        } else {
            final long diff = DAYS.between(LocalDate.now(), date.atZone(ZoneId.systemDefault()).toLocalDate());

            if (diff == 0) { // Today
                chipIconRes = R.drawable.ic_time_24;
                backgroundColorRes = R.color.due_today;
                textColorRes = R.color.due_text_today;

            } else if (diff < 0) { // Overdue
                chipIconRes = R.drawable.ic_time_filled_24;
                backgroundColorRes = R.color.due_overdue;
                textColorRes = R.color.due_text_overdue;

            } else { // Future
                chipIconRes = R.drawable.ic_time_24;
                backgroundColorRes = android.R.color.transparent;
                textColorRes = null;
            }
        }

        setChipIcon(ContextCompat.getDrawable(getContext(), chipIconRes));
        setChipBackgroundColorResource(backgroundColorRes);

        if (textColorRes == null) {
            setTextColor(colorOnSurface);
            setChipIconTint(ColorStateList.valueOf(colorOnSurface));

        } else {
            setTextColor(ContextCompat.getColor(getContext(), textColorRes));
            setChipIconTintResource(textColorRes);
        }
    }
}