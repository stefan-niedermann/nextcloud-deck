package it.niedermann.nextcloud.deck.ui.branding;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;

import it.niedermann.nextcloud.deck.R;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getSecondaryForegroundColorDependingOnTheme;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.isBrandingEnabled;

public class BrandedSwitchPreference extends SwitchPreference implements Branded {

    @ColorInt
    private Integer mainColor = null;

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

        if (isBrandingEnabled(getContext()) && holder.itemView instanceof ViewGroup) {
            switchView = findSwitchWidget(holder.itemView);
            if (mainColor != null) {
                applyBrand();
            }
        }
    }

    @Override
    public void applyBrand(@ColorInt int mainColor) {
        this.mainColor = mainColor;
        // onBindViewHolder is called after applyBrand, therefore we have to store the given values and apply them later.
        if (isBrandingEnabled(getContext())) {
            applyBrand();
        }
    }

    private void applyBrand() {
        if (switchView != null) {
            final int finalMainColor = getSecondaryForegroundColorDependingOnTheme(getContext(), mainColor);
            // int trackColor = Color.argb(77, Color.red(finalMainColor), Color.green(finalMainColor), Color.blue(finalMainColor));
            DrawableCompat.setTintList(switchView.getThumbDrawable(), new ColorStateList(
                    new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                    new int[]{finalMainColor, getContext().getResources().getColor(R.color.fg_secondary)}
            ));
            DrawableCompat.setTintList(switchView.getTrackDrawable(), new ColorStateList(
                    new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                    new int[]{finalMainColor, getContext().getResources().getColor(R.color.fg_secondary)}
            ));
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
                View child = viewGroup.getChildAt(i);
                if (child instanceof ViewGroup) {
                    Switch result = findSwitchWidget(child);
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
