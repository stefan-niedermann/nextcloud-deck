package it.niedermann.nextcloud.deck.javafx.services;

import com.jthemedetecor.OsThemeDetector;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import javafx.scene.Scene;

public class ThemeService {

    private final OsThemeDetector detector;

    private final Collection<WeakReference<Scene>> scenes = new HashSet<>();

    public ThemeService(OsThemeDetector detector) {
        this.detector = detector;
        detector.registerListener(isDark -> {
            for (final var sceneRef : scenes) {
                final var scene = sceneRef.get();
                if (scene == null) {
                    scenes.remove(sceneRef);
                    return;
                }
                setDarkMode(scene, isDark);
            }
        });
    }

    public void bind(Scene scene) {
        scenes.add(new WeakReference<>(scene));
        setDarkMode(scene, detector.isDark());
    }

    public void setDarkMode(Scene scene, boolean darkModeEnabled) {
        final var darkModeCssUrl = Objects.requireNonNull(getClass().getClassLoader().getResource("css/dark.css"));
        final var darkModeCssContent = darkModeCssUrl.toExternalForm();

        if (true || darkModeEnabled) {
            if (!scene.getStylesheets().contains(darkModeCssContent)) {
                scene.getStylesheets().add(darkModeCssContent);
            }
        } else {
            scene.getStylesheets().remove(darkModeCssContent);
        }
    }
}
