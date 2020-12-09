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

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.util.DeckColorUtil;

import static it.niedermann.nextcloud.deck.DeckApplication.isDarkTheme;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getSecondaryForegroundColorDependingOnTheme;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.isBrandingEnabled;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.readBrandMainColor;

public class BrandedDatePickerDialog extends DatePickerDialog implements Branded {

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
     * Create a new DatePickerDialog instance with a specific initial selection.
     *
     * @param callBack    How the parent is notified that the date is set.
     * @param year        The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog. [0 - 11]
     * @param dayOfMonth  The initial day of the dialog.
     * @return a new DatePickerDialog instance.
     */
    public static DatePickerDialog newInstance(OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        DatePickerDialog ret = new BrandedDatePickerDialog();
        ret.initialize(callBack, year, monthOfYear - 1, dayOfMonth);
        return ret;
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
        DatePickerDialog ret = new BrandedDatePickerDialog();
        ret.initialize(callback, initialSelection);
        return ret;
    }
}
