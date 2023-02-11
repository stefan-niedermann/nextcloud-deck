package it.niedermann.nextcloud.deck.ui.theme;

import static it.niedermann.nextcloud.deck.DeckApplication.readCurrentAccountColor;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

public class ThemedPreferenceCategory extends PreferenceCategory {

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        final var view = holder.itemView.findViewById(android.R.id.title);
        @Nullable final Context context = getContext();
        if (view instanceof TextView) {
            final var scheme = ThemeUtils.createScheme(readCurrentAccountColor(context), context);
            ((TextView) view).setTextColor(scheme.getOnPrimaryContainer());
        }
    }

    public ThemedPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ThemedPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ThemedPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThemedPreferenceCategory(Context context) {
        super(context);
    }
}
