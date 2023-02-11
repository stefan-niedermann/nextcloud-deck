package it.niedermann.nextcloud.deck.ui.theme;

import static it.niedermann.nextcloud.deck.DeckApplication.isDarkTheme;
import static it.niedermann.nextcloud.deck.ui.theme.ViewThemeUtils.readBrandMainColor;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.util.DeckColorUtil;

public class ThemedDatePickerDialog extends DatePickerDialog implements Themed {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final var context = requireContext();
        setThemeDark(isDarkTheme(context));
        applyTheme(readBrandMainColor(context));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void applyTheme(int color) {
        final var utils = ViewThemeUtils.of(color, requireContext());

        @ColorInt final int buttonTextColor = utils.getOnPrimaryContainer(requireContext());
        setOkColor(buttonTextColor);
        setCancelColor(buttonTextColor);
        setAccentColor(
                DeckColorUtil.contrastRatioIsSufficientBigAreas(Color.WHITE, color)
                        ? color
                        // Text in picker title is always white (also in dark mode)
                        : isThemeDark()
                        ? Color.BLACK
                        : ContextCompat.getColor(requireContext(), R.color.accent)
        );
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
    public static DatePickerDialog newInstance(OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        final var dialog = new ThemedDatePickerDialog();
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
    public static DatePickerDialog newInstance(OnDateSetListener callback, Calendar initialSelection) {
        final var dialog = new ThemedDatePickerDialog();
        dialog.initialize(callback, initialSelection);
        return dialog;
    }
}
