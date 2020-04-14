package it.niedermann.nextcloud.deck.ui.branding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import it.niedermann.nextcloud.deck.Application;

public class BrandedTimePickerDialog extends TimePickerDialog implements Branded {

    @Override
    public void applyBrand(int mainColor, int textColor) {
        @ColorInt int buttonTextColor = BrandedActivity.getColorDependingOnTheme(requireContext(), mainColor);
        if (buttonTextColor != mainColor) {
            setOkColor(buttonTextColor);
            setCancelColor(buttonTextColor);
        }
        setAccentColor(mainColor);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setThemeDark(Application.getAppTheme(getContext()));
        Application.registerBrandedComponent(requireContext(), this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        Application.deregisterBrandedComponent(this);
        super.onDestroy();
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
     * @param callback     How the parent is notified that the time is set.
     * @param is24HourMode True to render 24 hour mode, false to render AM / PM selectors.
     * @return a new TimePickerDialog instance.
     */
    @SuppressWarnings({"unused", "SameParameterValue"})
    public static TimePickerDialog newInstance(OnTimeSetListener callback, boolean is24HourMode) {
        Calendar now = Calendar.getInstance();
        return newInstance(callback, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), is24HourMode);
    }
}
