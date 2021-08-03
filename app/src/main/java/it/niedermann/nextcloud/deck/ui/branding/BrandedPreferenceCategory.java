package it.niedermann.nextcloud.deck.ui.branding;

import static it.niedermann.nextcloud.deck.DeckApplication.readCurrentAccountColor;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getSecondaryForegroundColorDependingOnTheme;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

public class BrandedPreferenceCategory extends PreferenceCategory {

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        final var view = holder.itemView.findViewById(android.R.id.title);
        @Nullable final Context context = getContext();
        if (context != null && view instanceof TextView) {
            @ColorInt final int mainColor = getSecondaryForegroundColorDependingOnTheme(context, readCurrentAccountColor(context));
            ((TextView) view).setTextColor(mainColor);
        }
    }

    public BrandedPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public BrandedPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BrandedPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BrandedPreferenceCategory(Context context) {
        super(context);
    }
}
