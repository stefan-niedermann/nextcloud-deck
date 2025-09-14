package it.niedermann.nextcloud.deck.deprecated.ui.view;

import static java.time.temporal.ChronoUnit.HOURS;

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
import java.time.LocalDateTime;
import java.time.ZoneId;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.deprecated.util.DateUtil;

public class DueDateChip extends Chip {

    @ColorInt
    protected final int colorOnSurface;
    protected final boolean compactMode;

    public DueDateChip(Context context) {
        this(context, null);
    }

    public DueDateChip(Context context, AttributeSet attrs) {
        this(context, attrs, com.google.android.material.R.attr.chipStyle);
    }

    public DueDateChip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final var typedValue = new TypedValue();
        final var theme = getContext().getTheme();
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSecondary, typedValue, true);
        this.colorOnSurface = typedValue.data;

        final var styles = context.obtainStyledAttributes(attrs, R.styleable.DueDateChip, defStyleAttr, 0);
        this.compactMode = styles.getBoolean(R.styleable.DueDateChip_compactMode, false);
        styles.recycle();

        setEnsureMinTouchTargetSize(false);
        setClickable(false);

        @Px final var padding = getResources().getDimensionPixelSize(R.dimen.spacer_1x);
        setPadding(padding, padding, padding, padding);
        setMinHeight(0);
        setChipMinHeight(0);

        if (compactMode) {
            setChipEndPadding(0);
            setTextEndPadding(0);
        }
    }

    public void setDueDate(@NonNull Instant date, boolean isDone) {
        if (compactMode) {
            setText(null);
        } else {
            setText(DateUtil.getRelativeDateTimeString(getContext(), date.toEpochMilli()));
        }

        @DrawableRes final int chipIconRes;
        @Nullable @ColorRes final Integer textColorRes;
        @ColorRes final int backgroundColorRes;

        if (isDone) { // Done
            chipIconRes = R.drawable.ic_check_circle_24;
            backgroundColorRes = R.color.due_done;
            textColorRes = R.color.due_text_done;

        } else if (date.isBefore(Instant.now())) { // Overdue
            chipIconRes = R.drawable.ic_time_filled_24;
            backgroundColorRes = R.color.due_overdue;
            textColorRes = R.color.due_text_overdue;

        } else if (HOURS.between(LocalDateTime.now(), date.atZone(ZoneId.systemDefault())) < 24) { // Next 24 Hours
            chipIconRes = R.drawable.ic_time_24;
            backgroundColorRes = R.color.due_today;
            textColorRes = R.color.due_text_today;

        } else { // Future
            chipIconRes = R.drawable.ic_time_24;
            backgroundColorRes = R.color.due_future;
            textColorRes = R.color.due_text_future;
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