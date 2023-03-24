package it.niedermann.nextcloud.deck.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import it.niedermann.nextcloud.deck.R;

public class FilterIndicator extends MaterialButton {

    public FilterIndicator(Context context) {
        this(context, null, 0);
    }

    public FilterIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, R.style.ThemeOverlay_Material3_Button_IconButton);
        setIcon(ContextCompat.getDrawable(context, R.drawable.filter_active));
    }

}