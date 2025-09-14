package it.niedermann.nextcloud.deck.deprecated.ui.theme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;

public class ThemedSwitchPreference extends SwitchPreference implements Themed {

    @Nullable
    private ThemeUtils utils = null;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Nullable
    private Switch switchView;

    public ThemedSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ThemedSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ThemedSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThemedSwitchPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        if (holder.itemView instanceof ViewGroup) {
            switchView = findSwitchWidget(holder.itemView);
            applyTheme();
        }
    }

    @Override
    public void applyTheme(@ColorInt int color) {
        this.utils = ThemeUtils.of(color, getContext());
        // onBindViewHolder is called after applyTheme, therefore we have to store the given values and apply them later.
        applyTheme();
    }

    private void applyTheme() {
        if (utils != null && switchView != null) {
            utils.platform.colorSwitch(switchView);
        }
    }

    /**
     * Recursively go through view tree until we find an android.widget.Switch
     *
     * @param view Root view to start searching
     * @return A Switch class or null
     * @see <a href="https://gist.github.com/marchold/45e22839eb94aa14dfb5">Source</a>
     */
    private Switch findSwitchWidget(View view) {
        if (view instanceof Switch) {
            return (Switch) view;
        }
        if (view instanceof ViewGroup viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                final var child = viewGroup.getChildAt(i);
                if (child instanceof ViewGroup) {
                    @SuppressLint("UseSwitchCompatOrMaterialCode") final var result = findSwitchWidget(child);
                    if (result != null) return result;
                }
                if (child instanceof Switch) {
                    return (Switch) child;
                }
            }
        }
        return null;
    }
}
