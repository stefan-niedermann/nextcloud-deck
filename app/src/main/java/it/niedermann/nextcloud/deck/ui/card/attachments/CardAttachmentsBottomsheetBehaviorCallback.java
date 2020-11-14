package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.content.Context;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.core.content.ContextCompat;

import com.google.android.material.animation.ArgbEvaluatorCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.niedermann.android.util.DimensionUtil;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;

public class CardAttachmentsBottomsheetBehaviorCallback extends BottomSheetBehavior.BottomSheetCallback {
    @NonNull
    private final OnBackPressedCallback backPressedCallback;
    @NonNull
    private final FloatingActionButton fab;
    @NonNull
    private final View pickerBackdrop;
    @NonNull
    private final BottomNavigationView bottomNavigation;
    @ColorInt
    private final int backdropColorExpanded;
    @ColorInt
    private final int backdropColorCollapsed;
    @Px
    private final int bottomNavigationHeight;

    private float lastOffset = -1;

    public CardAttachmentsBottomsheetBehaviorCallback(@NonNull Context context,
                                                      @NonNull OnBackPressedCallback backPressedCallback,
                                                      @NonNull FloatingActionButton fab,
                                                      @NonNull View pickerBackdrop,
                                                      @NonNull BottomNavigationView bottomNavigation,
                                                      @ColorRes int backdropColorExpanded,
                                                      @ColorRes int backdropColorCollapsed,
                                                      @DimenRes int bottomNavigationHeight
    ) {
        this.backPressedCallback = backPressedCallback;
        this.fab = fab;
        this.pickerBackdrop = pickerBackdrop;
        this.bottomNavigation = bottomNavigation;
        this.backdropColorExpanded = ContextCompat.getColor(context, backdropColorExpanded);
        this.backdropColorCollapsed = ContextCompat.getColor(context, backdropColorCollapsed);
        this.bottomNavigationHeight = DimensionUtil.INSTANCE.dpToPx(context, bottomNavigationHeight);
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
            bottomNavigation.setTranslationY(bottomSheetPercentageShown * bottomNavigationHeight);
            if (slideOffset <= lastOffset && slideOffset != 0) {
                if (fab.getVisibility() == GONE) {
                    fab.show();
                }
            } else {
                if (fab.getVisibility() == VISIBLE) {
                    fab.hide();
                }
            }
        }
        lastOffset = slideOffset;
    }
}
