package it.niedermann.nextcloud.deck.ui.branding;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.time.LocalTime;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.util.DeckColorUtil;

import static it.niedermann.nextcloud.deck.DeckApplication.isDarkTheme;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getSecondaryForegroundColorDependingOnTheme;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.isBrandingEnabled;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.readBrandMainColor;

public class BrandedTimePickerDialog extends TimePickerDialog implements Branded {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @Nullable Context context = getContext();
        if (context != null) {
            setThemeDark(isDarkTheme(context));
            if (isBrandingEnabled(context)) {
                applyBrand(readBrandMainColor(context));
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void applyBrand(int mainColor) {
        @ColorInt final int buttonTextColor = getSecondaryForegroundColorDependingOnTheme(requireContext(), mainColor);
        setOkColor(buttonTextColor);
        setCancelColor(buttonTextColor);
        // Text in picker title is always white
        setAccentColor(DeckColorUtil.contrastRatioIsSufficientBigAreas(Color.WHITE, mainColor) ? mainColor : ContextCompat.getColor(requireContext(), R.color.accent));
    }

    /**
     * Create a new TimePickerDialog instance with a given intial selection
     *
     * @param callback     How the parent is notified that the time is set.
     * @param hourOfDay    The initial hour of the dialog.
     * @param minute       The initial minute of the dialog.
     * @param second       The initial second of the dialog.
     * @param is24HourMode True to render 24 hour mode, false to render AM / PM selectors.
     * @return a new TimePickerDialog instance.
     */
    @SuppressWarnings({"SameParameterValue"})
    public static TimePickerDialog newInstance(OnTimeSetListener callback,
                                               int hourOfDay, int minute, int second, boolean is24HourMode) {
        TimePickerDialog ret = new BrandedTimePickerDialog();
        ret.initialize(callback, hourOfDay, minute, second, is24HourMode);
        return ret;
    }

    /**
     * Create a new TimePickerDialog instance with a given initial selection
     *
     * @param callback     How the parent is notified that the time is set.
     * @param hourOfDay    The initial hour of the dialog.
     * @param minute       The initial minute of the dialog.
     * @param is24HourMode True to render 24 hour mode, false to render AM / PM selectors.
     * @return a new TimePickerDialog instance.
     */
    public static TimePickerDialog newInstance(OnTimeSetListener callback,
                                               int hourOfDay, int minute, boolean is24HourMode) {
        return newInstance(callback, hourOfDay, minute, 0, is24HourMode);
    }

    /**
     * Create a new TimePickerDialog instance initialized to the current system time
     *
     * @param callback     How the parent is notified that the time is set.
     * @param is24HourMode True to render 24 hour mode, false to render AM / PM selectors.
     * @return a new TimePickerDialog instance.
     */
    @SuppressWarnings({"SameParameterValue"})
    public static TimePickerDialog newInstance(OnTimeSetListener callback, boolean is24HourMode) {
        LocalTime now = LocalTime.now();
        return newInstance(callback, now.getHour(), now.getMinute(), is24HourMode);
    }
}
