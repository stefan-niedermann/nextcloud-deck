package it.niedermann.nextcloud.deck.ui.theme;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.nextcloud.android.common.ui.color.ColorUtil;
import com.nextcloud.android.common.ui.theme.MaterialSchemes;
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase;
import com.nextcloud.android.common.ui.theme.utils.AndroidViewThemeUtils;
import com.nextcloud.android.common.ui.theme.utils.AndroidXViewThemeUtils;
import com.nextcloud.android.common.ui.theme.utils.DialogViewThemeUtils;
import com.nextcloud.android.common.ui.theme.utils.MaterialViewThemeUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import scheme.Scheme;

public class ViewThemeUtils extends ViewThemeUtilsBase {

    private static final ConcurrentMap<Integer, ViewThemeUtils> CACHE = new ConcurrentHashMap<>();

    public final AndroidViewThemeUtils platform;
    public final MaterialViewThemeUtils material;
    public final AndroidXViewThemeUtils androidx;
    public final DialogViewThemeUtils dialog;
    public final DeckViewThemeUtils deck;

    private ViewThemeUtils(
            final MaterialSchemes schemes,
            final ColorUtil colorUtil
    ) {
        super(schemes);

        this.platform = new AndroidViewThemeUtils(schemes, colorUtil);
        this.material = new MaterialViewThemeUtils(schemes, colorUtil);
        this.androidx = new AndroidXViewThemeUtils(schemes, this.platform);
        this.dialog = new DialogViewThemeUtils(schemes);
        this.deck = new DeckViewThemeUtils(schemes, this.material, this.platform);
    }

    public static ViewThemeUtils of(@ColorInt int color, @NonNull Context context) {
        return CACHE.computeIfAbsent(color, c -> new ViewThemeUtils(
                MaterialSchemes.Companion.fromColor(c),
                new ColorUtil(context)
        ));
    }

    /**
     * Since we may collide with dark theme in this area, we have to make sure that the color is visible depending on the background
     */
    @ColorInt
    public int getOnPrimaryContainer(@NonNull Context context) {
        return withScheme(context, Scheme::getOnPrimaryContainer);
    }

    @ColorInt
    public static int readBrandMainColor(@NonNull Context context) {
        final var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        DeckLog.log("--- Read:", context.getString(R.string.shared_preference_theme_main));
        return sharedPreferences.getInt(context.getString(R.string.shared_preference_theme_main), ContextCompat.getColor(context, R.color.defaultBrand));
    }

    public static void saveBrandColors(@NonNull Context context, @ColorInt int color) {
        final var editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write:", context.getString(R.string.shared_preference_theme_main), "|", color);
        editor.putInt(context.getString(R.string.shared_preference_theme_main), color);
        editor.apply();
    }

    public static void clearBrandColors(@NonNull Context context) {
        final var editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Remove:", context.getString(R.string.shared_preference_theme_main));
        editor.remove(context.getString(R.string.shared_preference_theme_main));
        editor.apply();
    }
}
