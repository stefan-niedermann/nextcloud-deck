package it.niedermann.nextcloud.deck.ui.card.attachments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.animation.ArgbEvaluatorCompat;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CardAttachmentsBottomsheetBehaviorCallback extends BottomSheetBehavior.BottomSheetCallback {
    @NonNull
    private final OnBackPressedCallback backPressedCallback;
    @NonNull
    private final FloatingActionButton fab;
    @NonNull
    private final View pickerBackdrop;
    @ColorInt
    private final int backdropColorExpanded;
    @ColorInt
    private final int backdropColorCollapsed;

    private float lastOffset = -1;

    public CardAttachmentsBottomsheetBehaviorCallback(@NonNull Context context,
                                                      @NonNull OnBackPressedCallback backPressedCallback,
                                                      @NonNull FloatingActionButton fab,
                                                      @NonNull View pickerBackdrop
    ) {
        this.backPressedCallback = backPressedCallback;
        this.fab = fab;
        this.pickerBackdrop = pickerBackdrop;
        final var color = ContextCompat.getColor(context, android.R.color.black);
        this.backdropColorExpanded = Color.argb(127, Color.red(color), Color.green(color), Color.blue(color));
        this.backdropColorCollapsed = ContextCompat.getColor(context, android.R.color.transparent);
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        if (newState == STATE_HIDDEN) {
            backPressedCallback.setEnabled(false);
            if (pickerBackdrop.getVisibility() != GONE) {
                pickerBackdrop.setVisibility(GONE);
            }
        } else if (pickerBackdrop.getVisibility() != VISIBLE) {
            pickerBackdrop.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        if (slideOffset <= 0) {
            final float bottomSheetPercentageShown = slideOffset * -1;
            pickerBackdrop.setBackgroundColor(ArgbEvaluatorCompat.getInstance().evaluate(bottomSheetPercentageShown, backdropColorExpanded, backdropColorCollapsed));
            if (slideOffset <= lastOffset && slideOffset != 0) {
                fab.show();
            } else {
                fab.hide();
            }
        }
        lastOffset = slideOffset;
    }
}
