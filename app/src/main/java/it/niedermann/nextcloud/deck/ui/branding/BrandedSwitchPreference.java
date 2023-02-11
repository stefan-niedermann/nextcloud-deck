package it.niedermann.nextcloud.deck.ui.branding;

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

public class BrandedSwitchPreference extends SwitchPreference implements Branded {

    @Nullable
    private ViewThemeUtils utils = null;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Nullable
    private Switch switchView;

    public BrandedSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public BrandedSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BrandedSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BrandedSwitchPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        if (holder.itemView instanceof ViewGroup) {
            switchView = findSwitchWidget(holder.itemView);
            applyBrand();
        }
    }

    @Override
    public void applyBrand(@ColorInt int color) {
        this.utils = ViewThemeUtils.of(color, getContext());
        // onBindViewHolder is called after applyBrand, therefore we have to store the given values and apply them later.
        applyBrand();
    }

    private void applyBrand() {
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
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
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
