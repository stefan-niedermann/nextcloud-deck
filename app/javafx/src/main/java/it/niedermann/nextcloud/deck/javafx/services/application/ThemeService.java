package it.niedermann.nextcloud.deck.javafx.services.application;

import com.jthemedetecor.OsThemeDetector;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.javafx.di.fx.FxScope;
import jakarta.inject.Inject;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;

@FxScope
public class ThemeService {

    private static final Logger logger = Logger.getLogger(ThemeService.class.getName());

    public static final String KEY_THEME = "theme";

    private final OsThemeDetector detector;
    private final KeyValueStore keyValueStore;

    private final Collection<WeakReference<Scene>> scenes = new HashSet<>();

    private Theme theme = Theme.AUTO;

    @Inject
    public ThemeService(OsThemeDetector detector, KeyValueStore keyValueStore) {
        this.detector = detector;
        this.keyValueStore = keyValueStore;

        detector.registerListener(isDark -> {
            if (theme == Theme.AUTO) {
                updateAllScenes(isDark);
            }
        });

        final var themePreferenceDisposable = Flowable.fromPublisher(this.keyValueStore.getString(KEY_THEME))
                .map(Theme::fromName)
                .subscribe(this::setTheme);
    }

    private void setTheme(Theme theme) {
        this.theme = theme;
        updateAllScenes(isDarkModeEnabled());
    }

    public void bind(Scene scene) {
        scenes.add(new WeakReference<>(scene));
        setDarkMode(scene, isDarkModeEnabled());
    }

    public void bind(Dialog<?> dialog) {
        bind(dialog.getDialogPane().getScene());
    }

    private boolean isDarkModeEnabled() {
        return switch (theme) {
            case AUTO -> detector.isDark();
            case LIGHT -> false;
            case DARK -> true;
        };
    }

    private void updateAllScenes(boolean isDark) {
        for (final var sceneRef : scenes) {
            final var scene = sceneRef.get();
            if (scene == null) {
                scenes.remove(sceneRef);
                continue;
            }
            setDarkMode(scene, isDark);
        }
    }

    private void setDarkMode(Scene scene, boolean darkModeEnabled) {
        final var darkModeCssUrl = Objects.requireNonNull(getClass().getClassLoader().getResource("css/dark.css"));
        final var darkModeCssContent = darkModeCssUrl.toExternalForm();

        if (darkModeEnabled) {
            if (!scene.getStylesheets().contains(darkModeCssContent)) {
                scene.getStylesheets().add(darkModeCssContent);
            }
        } else {
            scene.getStylesheets().remove(darkModeCssContent);
        }
    }
}
