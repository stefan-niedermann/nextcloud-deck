package it.niedermann.nextcloud.deck.deprecated.ui.theme;

import static com.nextcloud.android.common.ui.util.PlatformThemeUtil.isDarkMode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import scheme.Scheme;

public class ThemedDatePickerDialog extends DatePickerDialog implements Themed {

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

        @ColorInt final int buttonTextColor = scheme.getOnSecondaryContainer();
        setOkColor(buttonTextColor);
        setCancelColor(buttonTextColor);

        setAccentColor(Scheme.dark(color).getSecondaryContainer());
    }

    /**
     * Create a new DatePickerDialog instance with a specific initial selection.
     *
     * @param callBack    How the parent is notified that the date is set.
     * @param year        The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog. [0 - 11]
     * @param dayOfMonth  The initial day of the dialog.
     * @return a new DatePickerDialog instance.
     */
    public static DatePickerDialog newInstance(OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth, @ColorInt int color) {
        final var dialog = new ThemedDatePickerDialog();

        final var args = new Bundle();
        args.putInt(BUNDLE_KEY_COLOR, color);
        dialog.setArguments(args);

        dialog.initialize(callBack, year, monthOfYear - 1, dayOfMonth);
        return dialog;
    }

    /**
     * Create a new DatePickerDialog instance with a specific initial selection.
     *
     * @param callback         How the parent is notified that the date is set.
     * @param initialSelection A Calendar object containing the original selection of the picker.
     *                         (Time is ignored by trimming the Calendar to midnight in the current
     *                         TimeZone of the Calendar object)
     * @return a new DatePickerDialog instance
     */
    public static DatePickerDialog newInstance(OnDateSetListener callback, Calendar initialSelection, @ColorInt int color) {
        final var dialog = new ThemedDatePickerDialog();

        final var args = new Bundle();
        args.putInt(BUNDLE_KEY_COLOR, color);
        dialog.setArguments(args);

        dialog.initialize(callback, initialSelection);
        return dialog;
    }
}
