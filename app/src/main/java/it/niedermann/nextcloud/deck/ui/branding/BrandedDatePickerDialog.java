package it.niedermann.nextcloud.deck.ui.branding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import it.niedermann.nextcloud.deck.Application;

public class BrandedDatePickerDialog extends DatePickerDialog implements Branded {

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
     * Create a new DatePickerDialog instance with a specific initial selection.
     *
     * @param callBack    How the parent is notified that the date is set.
     * @param year        The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth  The initial day of the dialog.
     * @return a new DatePickerDialog instance.
     */
    public static DatePickerDialog newInstance(OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        DatePickerDialog ret = new BrandedDatePickerDialog();
        ret.initialize(callBack, year, monthOfYear, dayOfMonth);
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
