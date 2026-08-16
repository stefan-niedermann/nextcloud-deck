package it.niedermann.nextcloud.deck.javafx.services.application;

import com.jthemedetecor.OsThemeDetector;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.BackpressureStrategy;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.javafx.di.fx.FxScope;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Inject;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;

@FxScope
public class ThemeService {

    private static final Logger logger = Logger.getLogger(ThemeService.class.getName());

    public static final String KEY_THEME = "theme";

    private final KeyValueStore keyValueStore;

    private final Collection<WeakReference<Scene>> scenes = new HashSet<>();

    private boolean currentDarkModeEnabled = false;

    @Inject
    public ThemeService(OsThemeDetector detector, KeyValueStore keyValueStore) {
        this.keyValueStore = keyValueStore;

        final var themeFlowable = Flowable.fromPublisher(this.keyValueStore.getString(KEY_THEME))
                .map(Theme::fromName)
                .distinctUntilChanged();

        final var osDarkFlowable = Flowable.<Boolean>create(emitter -> {
                    emitter.onNext(detector.isDark());
                    detector.registerListener(emitter::onNext);
                }, BackpressureStrategy.LATEST)
                .distinctUntilChanged();

        final var disposable = Flowable.combineLatest(
                        themeFlowable,
                        osDarkFlowable,
                        (theme, osDark) -> switch (theme) {
                            case AUTO -> osDark;
                            case LIGHT -> false;
                            case DARK -> true;
                        })
                .distinctUntilChanged()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(enabled -> {
                    this.currentDarkModeEnabled = enabled;
                    updateAllScenes(enabled);
                });
    }

    public void bind(Scene scene) {
        scenes.add(new WeakReference<>(scene));
        setDarkMode(scene, currentDarkModeEnabled);
    }

    public void bind(Dialog<?> dialog) {
        bind(dialog.getDialogPane().getScene());
    }

    private void updateAllScenes(boolean isDark) {
        scenes.removeIf(ref -> {
            final var scene = ref.get();
            return scene == null || scene.getWindow() == null || !scene.getWindow().isShowing();
        });
        for (final var sceneRef : scenes) {
            final var scene = sceneRef.get();
            if (scene != null) {
                setDarkMode(scene, isDark);
            }
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
