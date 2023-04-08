package it.niedermann.nextcloud.deck.ui.theme;

import static com.nextcloud.android.common.ui.util.PlatformThemeUtil.isDarkMode;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.nextcloud.android.common.ui.color.ColorUtil;
import com.nextcloud.android.common.ui.theme.MaterialSchemes;
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase;
import com.nextcloud.android.common.ui.theme.utils.AndroidViewThemeUtils;
import com.nextcloud.android.common.ui.theme.utils.AndroidXViewThemeUtils;
import com.nextcloud.android.common.ui.theme.utils.DialogViewThemeUtils;
import com.nextcloud.android.common.ui.theme.utils.MaterialViewThemeUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import it.niedermann.nextcloud.deck.R;
import scheme.Scheme;

public class ThemeUtils extends ViewThemeUtilsBase {

    private static final ConcurrentMap<Integer, ThemeUtils> CACHE = new ConcurrentHashMap<>();

    public final AndroidViewThemeUtils platform;
    public final MaterialViewThemeUtils material;
    public final AndroidXViewThemeUtils androidx;
    public final DialogViewThemeUtils dialog;
    public final DeckViewThemeUtils deck;

    private ThemeUtils(
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

    public static ThemeUtils of(@ColorInt int color, @NonNull Context context) {
        return CACHE.computeIfAbsent(color, c -> new ThemeUtils(
                MaterialSchemes.Companion.fromColor(c),
                new ColorUtil(context)
        ));
    }

    public static ThemeUtils defaultBrand(@NonNull Context context) {
        return of(ContextCompat.getColor(context, R.color.defaultBrand), context);
    }

    @Deprecated
    public static Scheme createScheme(@ColorInt int color, @NonNull Context context) {
        return isDarkMode(context) ? Scheme.dark(color) : Scheme.light(color);
    }
}
