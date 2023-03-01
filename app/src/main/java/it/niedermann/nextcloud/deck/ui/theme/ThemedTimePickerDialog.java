package it.niedermann.nextcloud.deck.ui.theme;

import static com.nextcloud.android.common.ui.util.PlatformThemeUtil.isDarkMode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.time.LocalTime;

import scheme.Scheme;

public class ThemedTimePickerDialog extends TimePickerDialog implements Themed {

    private static final String BUNDLE_KEY_COLOR = "color";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final var args = getArguments();
        if (args == null || !args.containsKey(BUNDLE_KEY_COLOR)) {
            throw new IllegalArgumentException("Please provide at least local comment id");
        }

        applyTheme(args.getInt(BUNDLE_KEY_COLOR));
        setThemeDark(isDarkMode(requireContext()));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void applyTheme(int color) {
        final var scheme = ThemeUtils.createScheme(color, requireContext());

        @ColorInt final int buttonTextColor = scheme.getOnPrimaryContainer();
        setOkColor(buttonTextColor);
        setCancelColor(buttonTextColor);

        setAccentColor(Scheme.dark(color).getPrimaryContainer());
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
                                               int hourOfDay, int minute, int second, boolean is24HourMode, @ColorInt int color) {
        final var dialog = new ThemedTimePickerDialog();

        final var args = new Bundle();
        args.putInt(BUNDLE_KEY_COLOR, color);
        dialog.setArguments(args);

        dialog.initialize(callback, hourOfDay, minute, second, is24HourMode);
        return dialog;
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
                                               int hourOfDay, int minute, boolean is24HourMode, @ColorInt int color) {
        return newInstance(callback, hourOfDay, minute, 0, is24HourMode, color);
    }

    /**
     * Create a new TimePickerDialog instance initialized to the current system time
     *
     * @param callback     How the parent is notified that the time is set.
     * @param is24HourMode True to render 24 hour mode, false to render AM / PM selectors.
     * @return a new TimePickerDialog instance.
     */
    @SuppressWarnings({"SameParameterValue"})
    public static TimePickerDialog newInstance(OnTimeSetListener callback, boolean is24HourMode, @ColorInt int color) {
        final var now = LocalTime.now();
        return newInstance(callback, now.getHour(), now.getMinute(), is24HourMode, color);
    }
}
